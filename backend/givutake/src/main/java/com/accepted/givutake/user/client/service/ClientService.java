package com.accepted.givutake.user.client.service;

import com.accepted.givutake.funding.service.FundingParticipantService;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.payment.service.OrderService;
import com.accepted.givutake.pdf.DonationParticipantsDto;
import com.accepted.givutake.pdf.DonationReceiptFormDto;
import com.accepted.givutake.pdf.PdfService;
import com.accepted.givutake.region.service.RegionService;
import com.accepted.givutake.user.client.entity.Addresses;
import com.accepted.givutake.user.client.entity.Cards;
import com.accepted.givutake.user.client.model.*;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.MailService;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AddressService addressService;
    private final UserService userService;
    private final RegionService regionService;
    private final OrderService orderService;
    private final CardService cardService;
    private final PdfService pdfService;
    private final FundingParticipantService fundingParticipantService;

    // 아이디가 email인 사용자의 조건에 맞는 모든 주소 조회
    public List<Addresses> getAddressesByEmail(String email, Boolean isRepresentative) {
        // 1. email로 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 아이디가 email인 회원의 모든 주소록 가져오기(삭제 처리된 주소록 제외)
        return addressService.getAddressesByUsers(savedUsers, isRepresentative);
    }

    // 아이디가 email인 사용자의 특정 주소 상세 조회
    public AddressDetailViewDto getAddressDetailByEmail(String email, int addressIdx) {
        // 1. email로 부터 userIdx값 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        int userIdx = savedUserDto.getUserIdx();

        // 2. addressIdx에 해당하는 주소 가져오기
        Addresses savedAddresses = addressService.getAddressByAddressIdx(addressIdx);

        // 3. userIdx값이 일치하지 않는 경우 조회 불가
        if (userIdx != savedAddresses.getUsers().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        return AddressDetailViewDto.toDto(savedAddresses);
    }

    // 아이디가 email인 사용자의 주소 추가
    public Addresses addAddressByEmail(String email, AddressAddDto addressAddDto) {
        // 1. email로 부터 userIdx값 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 지역 코드 넣기
        String sido = addressAddDto.getSido();
        String sigungu = addressAddDto.getSigungu();
        int regionIdx = regionService.getRegionIdxBySidoAndSigungu(sido, sigungu);

        // 3. 대표 주소로 설정한다면, 이전의 대표 주소는 false 처리
        if (addressAddDto.getIsRepresentative()) {
            addressService.updateRepresentativeAddressFalse(savedUsers);
        }

        // 4. DB에 주소 추가
        Addresses addresses = addressAddDto.toEntity(savedUsers, regionIdx);
        return addressService.saveAddresses(addresses);
    }

    // 아이디가 email인 사용자의 주소 수정
    public AddressDetailViewDto modifyAddressByEmail(String email, int addressIdx, AddressModifyDto addressModifyDto) {
        // 1. email로 부터 userIdx값 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();
        int userIdx = savedUserDto.getUserIdx();

        // 2. DB에서 주소 조회
        Addresses savedAddresses = addressService.getAddressByAddressIdx(addressIdx);

        // 3. userIdx값이 일치하지 않는 경우 수정 불가
        if (userIdx != savedAddresses.getUsers().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 5. 대표 주소로 설정한다면
        boolean isRepresentative = addressModifyDto.getIsRepresentative();
        if (isRepresentative) {
            // 이미 대표 주소로 설정되어 있다면 수정하지 않고 그대로 return
            if (savedAddresses.isRepresentative()) {
                return AddressDetailViewDto.toDto(savedAddresses);
            }
            // 대표 주소로 설정되어 있지 않다면 이전의 대표 주소는 false 처리
            else {
                addressService.updateRepresentativeAddressFalse(savedAddresses.getUsers());
            }
        }
        // 6. 대표 주소로 설정하지 않는다면
        else {
            // 이미 대표 주소로 설정되어 있지 않다면 수정하지 않고 그대로 return
            if (!savedAddresses.isRepresentative()) {
                return AddressDetailViewDto.toDto(savedAddresses);
            }
            // 대표 주소로 설정되어 있다면
            else {
                // 회원당 대표주소 1개는 꼭 필수이므로 대표 주소값이 있는지 확인
                if (addressService.countByUsersAndIsRepresentativeTrue(savedUsers) <= 1) {
                    throw new ApiException(ExceptionEnum.NOT_ALLOWED_LAST_ADDRESS_ISREPRESENTATIVE_EXCEPTION);
                }
            }

        }

        // 5. 수정
        savedAddresses.setRepresentative(isRepresentative);
//        savedAddresses.setLatitude(addressUpdateDto.getLatitude());
//        savedAddresses.setLongitude(addressUpdateDto.getLongitude());

        // 6. DB에 저장
        return AddressDetailViewDto.toDto(addressService.saveAddresses(savedAddresses));
    }

    // 아이디가 email인 사용자의 특정 주소 삭제
    public Addresses deleteAddressByEmail(String email, int addressIdx) {
        // 1. 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 해당 회원의 주소가 총 1개라면 삭제 불가
        long cnt = addressService.countByUsers(savedUsers);
        if (cnt <= 1) {
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_LAST_ADDRESS_DELETION_EXCEPTION);
        }

        // 2. addressIdx에 해당하는 주소 가져오기
        Addresses savedAddresses = addressService.getAddressByAddressIdx(addressIdx);

        // 3. userIdx값이 일치하지 않는 경우 삭제 불가
        if (savedUsers.getUserIdx() != savedAddresses.getUsers().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 4. 삭제
        return addressService.deleteAddressByAddressIdx(savedAddresses);
    }

    // 이메일로 기부금 영수증 보내기
    public void sendEmailDonationReceipt(String email) throws MessagingException {
        // 1. DB에서 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 기부금 영수증 생성
        byte [] pdfByte = this.generateDonationReceipt(savedUsers);

        // 3. 메일로 전송
        int nowYear =  LocalDate.now().getYear();
        String subject = "[GIVU&TAKE] " + nowYear + " 기부금 영수증 발급 메일";
        String fileName = nowYear + "년도_기부금_영수증_" + savedUsers.getName() + ".pdf";
        String htmlContent = "<h1>" +
                nowYear +
                "년도 기부금 영수증 발급 안내</h1><br>" +
                "<p>힘든 한 해 동안에도 GIVE&TAKE와 함께 해주셔서 진심으로 감사드립니다.</p>" +
                "<p>덕분에 사라져가는 많은 지역들이 힘차게 도약하여 새롭고 희망찬 미래를 만들어갈 수 있는 힘을 얻게 되었습니다.</p><br>" +
                "<p>후원자님의 소중한 후원금에 대한 " +
                nowYear +
                "년도분 기부금 영수증을 발급해드립니다.</p>";

        mailService.sendMultipleMessage(email, fileName, subject, htmlContent, pdfByte);
    }

    public List<DonationParticipantsDto> generateDonationYear(String email){

        // 1. 사용자의 펀딩 내역 가져오기(현재 연도 기록만)
        int nowYear = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(nowYear, 1, 1);
        LocalDate endDate = LocalDate.of(nowYear, 12, 31);
        List<DonationParticipantsDto> fundingDonationParticipantsDtoList = fundingParticipantService.getFundingParticipantsListByEmail(email, startDate, endDate)
                .stream()
                .map(participant -> DonationParticipantsDto.fundingPariticipantsToDto(participant, ""))
                .toList();

        // 2. 답례품 구매 내역 가져오기(현재 연도 기록만)
        List<DonationParticipantsDto> orderDonationParticipantsDtoList = orderService.getOrdersCreatedDateBetweenByEmail(email, startDate, endDate)
                .stream()
                .map(orders -> DonationParticipantsDto.ordersToDto(orders, ""))
                .toList();

        // 3. 두 리스트 합치기
        List<DonationParticipantsDto> combinedList = new ArrayList<>(fundingDonationParticipantsDtoList);
        combinedList.addAll(orderDonationParticipantsDtoList);

        // 4. 최신 순으로 정렬
        Collections.sort(combinedList);

        return combinedList;
    }

    // 기부금 영수증 생성
    public byte[] generateDonationReceipt(Users users) {
        String email = users.getEmail();

        // 1. 사용자의 펀딩 내역 가져오기(현재 연도 기록만)
        int nowYear = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(nowYear, 1, 1);
        LocalDate endDate = LocalDate.of(nowYear, 12, 31);
        List<DonationParticipantsDto> fundingDonationParticipantsDtoList = fundingParticipantService.getFundingParticipantsListByEmail(email, startDate, endDate)
                .stream()
                .map(participant -> DonationParticipantsDto.fundingPariticipantsToDto(participant, ""))
                .toList();

        // 2. 답례품 구매 내역 가져오기(현재 연도 기록만)
        List<DonationParticipantsDto> orderDonationParticipantsDtoList = orderService.getOrdersCreatedDateBetweenByEmail(email, startDate, endDate)
                .stream()
                .map(orders -> DonationParticipantsDto.ordersToDto(orders, ""))
                .toList();

        // 3. 두 리스트 합치기
        List<DonationParticipantsDto> combinedList = new ArrayList<>(fundingDonationParticipantsDtoList);
        combinedList.addAll(orderDonationParticipantsDtoList);

        // 4. 최신 순으로 정렬
        Collections.sort(combinedList);

        // 5. 대표 주소 가져오기
        Addresses savedAddresses = addressService.getRepresentativeAddressesByUsers(users);

        // 6. PDF 파일로 생성
        DonationReceiptFormDto donationReceiptFormDto = DonationReceiptFormDto.builder()
                .userName(users.getName())
                .userAddress(savedAddresses.getRoadAddress() + savedAddresses.getDetailAddress())
                .userPhone(users.getMobilePhone())
                .donationParticipantsDtoList(combinedList)
                .build();

        return pdfService.generateDonationReceipt(donationReceiptFormDto);
    }

    // 나의 기부금 총액 조회
    public long calculateTotalFundingFeeByEmail(String email) {
        // 1. 사용자가 참여한 모든 펀딩의 기부금 조회
        long fundingPrice = fundingParticipantService.calculateTotalFundingFeeByEmail(email);

        // 2. 사용자의 총 답례품 금액 조회
        long giftPrice = orderService.calculateTotalOrderPriceByEmail(email);

        return fundingPrice + giftPrice;
    }

    // 카드 등록하기
    public CardDto addCardByEmail(String email, AddCardDto addCardDto) {
        // 1. DB에서 사용자 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 카드 중복 등록 여부 확인
        boolean isExist = cardService.isExistCardByCardNumber(addCardDto.getCardNumber());
        if (isExist) {
            throw new ApiException(ExceptionEnum.DUPLICATED_CARD_EXCEPTION);
        }

        // 3. 대표 카드로 설정한다면, 이전의 대표 카드는 false 처리
        if (addCardDto.getIsRepresentative()) {
            cardService.updateRepresentativeCardFalse(savedUsers);
        }

        // 4. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(addCardDto.getCardPassword());
        addCardDto.setCardPassword(encodedPassword);

        // 3. DB에 카드 추가
        Cards cards = addCardDto.toEntity(savedUsers);
        return cardService.saveCard(cards);
    }

    // 카드 삭제하기
    public CardDto deleteCardByCardIdx(String email, int cardIdx) {
        // 1. 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. cardIdx에 해당하는 카드 가져오기
        CardDto savedCardDto = cardService.getCardByCardIdx(cardIdx);

        // 3. userIdx값이 일치하지 않는 경우 삭제 불가
        if (savedUsers.getUserIdx() != savedCardDto.getUsers().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 4. 삭제
        cardService.deleteCardByCardIdx(cardIdx);

        return savedCardDto;
    }

    // 카드 수정
    public CardDto modifyCardByCardIdx(String email, int cardIdx, CardModifyDto cardModifyDto) {
        // 1. email로 부터 userIdx값 가져오기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();
        int userIdx = savedUserDto.getUserIdx();

        // 2. DB에서 카드 조회
        CardDto savedCardDto = cardService.getCardByCardIdx(cardIdx);

        // 3. userIdx값이 일치하지 않는 경우 수정 불가
        if (userIdx != savedCardDto.getUsers().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 5. 대표 카드로 설정한다면
        boolean isRepresentative = cardModifyDto.getIsRepresentative();
        if (isRepresentative) {
            // 이미 대표 카드로 설정되어 있다면 수정하지 않고 그대로 return
            if (savedCardDto.isRepresentative()) {
                return savedCardDto;
            }
            // 대표 카드로 설정되어 있지 않다면 이전의 대표 주소는 false 처리
            else {
                cardService.updateRepresentativeCardFalse(savedUsers);
            }
        }
        // 6. 대표 카드로 설정하지 않는다면
        else {
            // 이미 대표 카드로 설정되어 있지 않다면 수정하지 않고 그대로 return
            if (!savedCardDto.isRepresentative()) {
                return savedCardDto;
            }
        }

        // 5. 수정
        Cards cards = savedCardDto.toEntity();
        cards.setRepresentative(isRepresentative);

        // 6. DB에 저장
        return cardService.saveCard(cards);
    }
}
