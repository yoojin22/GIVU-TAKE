package com.accepted.givutake.user.common.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.service.S3Service;
import com.accepted.givutake.region.entity.Region;
import com.accepted.givutake.region.service.RegionService;
import com.accepted.givutake.user.client.model.AddressSignUpDto;
import com.accepted.givutake.user.client.service.AddressService;
import com.accepted.givutake.user.common.entity.EmailCode;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.enumType.Roles;
import com.accepted.givutake.user.common.model.*;
import com.accepted.givutake.user.common.repository.EmailCodeRepository;
import com.accepted.givutake.user.common.repository.UsersRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class UserService {

    private final UsersRepository userRepository;
    private final RegionService regionService;
    private final EmailCodeRepository emailCodeRepository;
    private final MailService mailService;
    private final AddressService addressService;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final S3Service s3Service;

    // 1800(초) == 30분
    private static final long EMAIL_CODE_EXPIRATION_TIME = 1800;

    public UserService(UsersRepository userRepository, RegionService regionService, PasswordEncoder passwordEncoder, EmailCodeRepository emailCodeRepository, MailService mailService, AddressService addressService, S3Service s3Service) {
        this.userRepository = userRepository;
        this.regionService = regionService;
        this.emailCodeRepository = emailCodeRepository;
        this.mailService = mailService;
        this.addressService = addressService;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;

        // ValidatorFactory와 Validator 초기화
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public void emailSignUp(SignUpDto signUpDto, AddressSignUpDto addressSignUpDto, MultipartFile profileImage) {
        Roles role = signUpDto.getRoles();

        // 유효하지 않은 권한정보가 들어온 경우
        if (!(role == Roles.ROLE_CLIENT || role == Roles.ROLE_CORPORATIONYET)) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 이메일 중복 검사
        this.emailDuplicatedCheck(signUpDto.getEmail());

        // 비밀번호 암호화
        signUpDto.setPassword(this.encode(signUpDto.getPassword()));

        // ref) 관리자는 회원가입할 수 없다. DB를 통해 직접 데이터 추가 요망.
        // 1. 수혜자 회원가입 관련 입력값 검증 및 처리
        if (role == Roles.ROLE_CORPORATIONYET) {
            // 주소값은 들어오면 안된다
            if (addressSignUpDto != null) {
                throw new ApiException(ExceptionEnum.UNEXPECTED_REPRESENTATIVE_ADDRESS_EXCEPTION);
            }
            // 수혜자 회원가입 유효성 검증
            checkArgumentValidityForCorporationSignUp(signUpDto);

            // region 값 가져오기
            String sido = signUpDto.getSido();
            String sigungu = signUpDto.getSigungu();
            Region region = regionService.findRegionBySidoAndSigungu(sido, sigungu);

            String publicProfileImageUrl = null;

            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // s3에 profile image 업로드
                    publicProfileImageUrl = s3Service.uploadProfileImage(profileImage);
                } catch (IOException e) {
                    throw new ApiException(ExceptionEnum.ILLEGAL_PROFILE_IMAGE_EXCEPTION);
                }
            }

            // DB에 회원 정보 저장
            userRepository.save(signUpDto.toEntity(region, publicProfileImageUrl));
        }
        // 2. 사용자 회원가입 관련 입력값 검증 및 처리
        else {
            // 대표 주소는 필수 입력 값
            if (addressSignUpDto == null) {
                throw new ApiException(ExceptionEnum.MISSING_REPRESENTATIVE_ADDRESS_EXCEPTION);
            }
            // 사용자 회원가입 유효성 검증
            checkArgumentValidityForClientSignUp(signUpDto);
            // 대표 주소 DTO의 유효성을 수동으로 검증
            validateAddressSignUpDto(addressSignUpDto);

            String publicProfileImageUrl = null;

            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // s3에 profile image 업로드
                    publicProfileImageUrl = s3Service.uploadProfileImage(profileImage);
                } catch (IOException e) {
                    throw new ApiException(ExceptionEnum.ILLEGAL_PROFILE_IMAGE_EXCEPTION);
                }
            }

            // DB에 저장
            Users savedUser = userRepository.save(signUpDto.toEntity(null, publicProfileImageUrl));

            // 지역 코드 넣기
            String sido = addressSignUpDto.getSido();
            String sigungu = addressSignUpDto.getSigungu();
            int regionIdx = regionService.getRegionIdxBySidoAndSigungu(sido, sigungu);

            // 주소 정보를 DB에 저장
            addressService.saveAddresses(addressSignUpDto.toEntity(savedUser, regionIdx));
        }
    }

    // 수혜자 회원가입 유효성 검증
    public void checkArgumentValidityForCorporationSignUp(SignUpDto signUpDto) {
        // 수혜자는 isMale, birth 값을 가지면 안된다.
        if (signUpDto.getIsMale() != null) {
            throw new ApiException(ExceptionEnum.UNEXPECTED_ISMALE_EXCEPTION);
        }
        if (signUpDto.getBirth() != null) {
            throw new ApiException(ExceptionEnum.UNEXPECTED_BIRTH_EXCEPTION);
        }

        // sido, sigungu 값은 필수!
        String sido = signUpDto.getSido();
        String sigungu = signUpDto.getSigungu();
        if (sido == null || sido.isEmpty()) {
            throw new ApiException(ExceptionEnum.MISSING_SIDO_EXCEPTION);
        }
        if (sigungu == null || sigungu.isEmpty()) {
            throw new ApiException(ExceptionEnum.MISSING_SIGUNGU_EXCEPTION);
        }

    }

    // 사용자 회원가입 유효성 검증
    public void checkArgumentValidityForClientSignUp(SignUpDto signUpDto) {
        // 사용자는 sido, sigungu 값을 가지면 안된다.
        if (signUpDto.getSido() != null) {
            throw new ApiException(ExceptionEnum.UNEXPECTED_SIDO_EXCEPTION);
        }
        if (signUpDto.getSigungu() != null) {
            throw new ApiException(ExceptionEnum.UNEXPECTED_SIGUNGU_EXCEPTION);
        }

        // isMale, birth 값은 필수이다.
        if (signUpDto.getIsMale() == null) {
            throw new ApiException(ExceptionEnum.MISSING_ISMALE_EXCEPTION);
        }
        if (signUpDto.getBirth() == null) {
            throw new ApiException(ExceptionEnum.MISSING_BIRTH_EXCEPTION);
        }

    }

    // 대표 주소 DTO의 유효성을 수동으로 검증
    public void validateAddressSignUpDto(AddressSignUpDto addressSignUpDto) {
        // 1. AddressDto 객체의 유효성 검사 수행
        Set<ConstraintViolation<AddressSignUpDto>> violations = validator.validate(addressSignUpDto);

        // 2. 유효성 검증 결과에서 오류가 있는지 확인
        if (!violations.isEmpty()) {
            // 3. 유효성 검증 실패 시 ConstraintViolationException 발생
            throw new ConstraintViolationException(violations);
        }
    }

    // 이메일 중복 검사
    public void emailDuplicatedCheck(String email) {
        Optional<Users> optionalExistingUsers =  userRepository.findByEmail(email);

        if (optionalExistingUsers.isPresent()) {
            Users savedUser = optionalExistingUsers.get();

            // 이미 탈퇴한 회원일 경우 회원 정보 조회 불가
            if (savedUser.isWithdraw()) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
            }
            else {
                throw new ApiException(ExceptionEnum.DUPLICATED_EMAIL_EXCEPTION);
            }
        }
    }

    // 비밀번호 암호화
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    // 이메일로 회원 정보 조회
    public UserDto getUserByEmail(String email) {
        Optional<Users> optionalExistingUsers =  userRepository.findByEmail(email);

        if (optionalExistingUsers.isPresent()) {
            Users savedUser = optionalExistingUsers.get();

            // 이미 탈퇴한 회원일 경우 회원 정보 조회 불가
            if (savedUser.isWithdraw()) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
            }

            return UserDto.toDto(optionalExistingUsers.get());
        }
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }
    }

    // JWT 토큰으로 회원 정보 수정
    public UserDto modifyUserByEmail(String email, ModifyUserDto modifyUserDto, MultipartFile profileImage) {
        Optional<Users> optionalExistingUsers = userRepository.findByEmail(email);

        if (optionalExistingUsers.isPresent()) {
            Users savedUser = optionalExistingUsers.get();

            // 이미 탈퇴한 회원일 경우 회원 정보 수정 불가
            if (savedUser.isWithdraw()) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
            }

            // 수정할 프로필 사진이 있을 경우, 프로필 사진 변경
            if (profileImage != null && !profileImage.isEmpty()) {

                // 기존의 프로필 사진 삭제
                String originalProfileImageUrl = savedUser.getProfileImageUrl();
                if (originalProfileImageUrl != null) {
                    String objectKey = s3Service.parseObjectKeyFromCloudfrontUrl(originalProfileImageUrl);
                    s3Service.deleteProfileImage(objectKey);
                }

                try {
                    String modifiedProfileImageUrl = s3Service.uploadProfileImage(profileImage);
                    savedUser.setProfileImageUrl(modifiedProfileImageUrl);
                } catch (IOException e) {
                    throw new ApiException(ExceptionEnum.ILLEGAL_PROFILE_IMAGE_EXCEPTION);
                }
            }

            // 사용자 정보 수정
            savedUser.setName(modifyUserDto.getName());
            savedUser.setIsMale(modifyUserDto.getIsMale());
            savedUser.setBirth(modifyUserDto.getBirth());
            savedUser.setMobilePhone(modifyUserDto.getMobilePhone());
            savedUser.setLandlinePhone(modifyUserDto.getLandlinePhone());

            // 변경된 정보 저장
            Users modifiedUser = userRepository.save(savedUser);

            return UserDto.toDto(modifiedUser);
        }
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }
    }

    // JWT 토큰으로 회원 탈퇴
    public void withdrawUserByEmail(String email) {
        Optional<Users> optionalExistingUsers = userRepository.findByEmail(email);

        if (optionalExistingUsers.isPresent()) {
            Users savedUser = optionalExistingUsers.get();

            // 이미 탈퇴한 회원일 경우 회원 탈퇴 불가
            if (savedUser.isWithdraw()) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
            }
            
            // TODO: 회원과 관련된 다른 모든 데이터도 삭제 처리(refresh 토큰 등등..)
            userRepository.updateIsWithdrawByEmail(email, true);

        }
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }
    }

    // DB에 아이디가 email인 사용자가 있는지 없는지 boolean 값으로 반환
    public boolean existUserWithEmail(String email) {
        Optional<Users> optionalExistingUsers = userRepository.findByEmail(email);

        if (optionalExistingUsers.isPresent()) {
            Users savedUser = optionalExistingUsers.get();

            // 이미 탈퇴한 회원일 경우 회원 탈퇴 불가
            if (savedUser.isWithdraw()) {
                throw new ApiException(ExceptionEnum.USER_ALREADY_WITHDRAWN_EXCEPTION);
            }
            else {
                return true;
            }
        }
        return false;
    }

    // 해당 이메일을 가진 회원의 비밀번호 검증
    public void verifyPassword(String email, PasswordDto passwordDto) {
        String password = passwordDto.getPassword();

        // 1. email을 기반으로 비밀번호를 가져온다
        UserDto savedUserDto = this.getUserByEmail(email);
        String savedPassword = savedUserDto.getPassword();

        boolean isValid = passwordEncoder.matches(password, savedPassword);
        if (!isValid) {
            throw new ApiException(ExceptionEnum.PASSWORD_MISMATCH_EXCEPTION);
        }
    }

    // 비밀번호 재설정을 위한 인증 코드 발송
    public void sendCodeForPasswordReset(EmailDto emailDto) throws MessagingException {
        String email = emailDto.getEmail();

        // 1. DB에 아이디가 email인 회원이 있는지 검증
        boolean exist = this.existUserWithEmail(email);

        if (!exist) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }

        // 2. 있다면, 인증 코드를 만들어 Redis에 저장
        String code = this.createRandomNumber();

        EmailCode emailCode = EmailCode.builder()
                .email(email)
                .code(code)
                .ttl(EMAIL_CODE_EXPIRATION_TIME)
                .build();
        emailCodeRepository.save(emailCode);

        // 3. 해당 이메일로 인증 코드 발송
        String subject = "[GIVU&TAKE] 비밀번호 재설정 요청 메일입니다.";
        String htmlContent = "<h1>비밀번호 재설정을 요청하셨습니다.</h1><br>" +
                "<p>안녕하세요.</p>" +
                "<p>GIVU&TAKE 계정의 비밀번호 재설정을 요청하셨습니다.</p><br>" +
                "<p>비밀번호를 재설정하시려면 아래 인증코드를 입력해주세요.</p>" +
                "<p><b>인증코드는 30분 후에 만료됩니다.</b></p><br>" +
                "<div style=\"" +
                "display: flex; align-items: center; justify-content: center;" +
                "margin: 2em 0; padding: 1em; border: solid 2px #A5A19A;" +
                "background-image: repeating-linear-gradient(-45deg, #f2f3f7 0, #f2f3f7 3px, transparent 3px, transparent 6px);" +
                "width: fit-content; height: fit-content; min-width: 120px; min-height: 60px;" +
                "text-align: center; font-size: 1.2em;" +
                "position: relative; left: 0;" +
                "\"><b>" +
                code +
                "</b></div>";

        mailService.sendEmail(email, subject, htmlContent);
    }

    // 비밀번호 재설정을 위한 인증 코드 생성
    private String createRandomNumber() {
        Random rand = new Random();
        StringBuilder randomNum = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            String random = Integer.toString(rand.nextInt(10));
            randomNum.append(random);
        }

        return randomNum.toString();
    }

    // 비밀번호 인증 코드 검증
    public void verifyCodeForPasswordReset(EmailCodeDto emailCodeDto) {
        String email = emailCodeDto.getEmail();
        String code = emailCodeDto.getCode();

        // 아이디가 email인 회원이 없는 경우
        if (!this.existUserWithEmail(email)) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }

        // 1. redis에서 email을 id 값으로 가지고 있는 emailCode 가져오기
        Optional<EmailCode> existingEmailCodeOptional = emailCodeRepository.findByEmail(email);

        // 2. 있다면 emailToken값 가져오기
        if (existingEmailCodeOptional.isPresent()) {
            String savedCode = existingEmailCodeOptional.get().getCode();

            // redis에 저장되어 있는 토큰과 들어온 토큰이 같지 않은 경우
            if (!savedCode.equals(code)) {
                throw new ApiException(ExceptionEnum.EMAILCODE_MISMATCH_EXCEPTION);
            }
        }
        else {
            // 3. redis에 토큰이 존재하지 않은 경우
            throw new ApiException(ExceptionEnum.NOT_FOUND_EMAILCODE_EXCEPTION);
        }
    }

    // 비밀번호 재설정
    public void resetPassword(PasswordResetDto passwordResetDto) {
        String password = passwordResetDto.getPassword();
        String emailCode = passwordResetDto.getCode();
        String email = passwordResetDto.getEmail();

        // 1.emailCode값이 맞는지 다시 한번 검증(틀리면 오류 발생)
        EmailCodeDto emailCodeDto = EmailCodeDto.builder()
                .email(email)
                .code(emailCode)
                .build();
        this.verifyCodeForPasswordReset(emailCodeDto);

        // 2. emailCode 값이 검증되었다면, password 변경
        String newPassword = this.encode(password);

        // 3. DB에 업데이트
        userRepository.updatePasswordByEmail(email, newPassword);

        // 4. 변경되었다면, redis에서 이메일 코드 삭제
        emailCodeRepository.deleteByEmail(email);
    }
}
