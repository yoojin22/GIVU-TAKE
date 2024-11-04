package com.accepted.givutake.funding.service;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.model.FundingAddDto;
import com.accepted.givutake.funding.model.FundingViewDto;
import com.accepted.givutake.funding.repository.FundingRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.service.S3Service;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FundingService {

    private final FundingRepository fundingRepository;
    private final UserService userService;
    private final S3Service s3Service;

    // 종료가 임박한 순서대로 펀딩 10개 조회
    public List<FundingViewDto> getDeadlineImminentFundings() {
        return fundingRepository.findTop10ByStateOrderByEndDate((byte) 1)
                .stream()
                .map(FundingViewDto::toDto)
                .collect(Collectors.toList());
    }

    // 자신이 작성한 모든 펀딩 조회
    public List<Fundings> getMyFundingList(String email, Byte state, Character type, int pageNo, int pageSize) {
        // 1. DB에서 유저 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // 람다식으로 동적 쿼리 설정
        Specification<Fundings> spec = (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            // 유저 조건
            predicates.add(criteriaBuilder.equal(root.get("corporation"), savedUsers));

            // 상태 조건 (state가 null이 아닌 경우에만 추가)
            if (state != null) {
                predicates.add(criteriaBuilder.equal(root.get("state"), state));
            }

            // 상태 조건 (type이 null이 아닌 경우에만 추가)
            if(type != null) {
                predicates.add(criteriaBuilder.equal(root.get("fundingType"), type));
            }

            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Fundings> fundingsPage = fundingRepository.findAll(spec, pageable);
        return fundingsPage.getContent();
    }

    // 조건에 해당하는 모든 펀딩 조회(삭제된 펀딩은 조회 불가)
    public List<Fundings> getFundingByTypeAndState(char fundingType, byte state) {
        return fundingRepository.findByFundingTypeAndStateAndIsDeletedFalse(fundingType, state);
    }

    // fundingIdx에 해당하는 펀딩 조회
    public Fundings getFundingByFundingIdx(int fundingIdx) {
        Optional<Fundings> optionalExistingFundings =  fundingRepository.findByFundingIdx(fundingIdx);

        if (optionalExistingFundings.isPresent()) {
            Fundings savedFundings = optionalExistingFundings.get();

            // 이미 삭제된 펀딩일 경우 조회 불가
            if (savedFundings.isDeleted()) {
                throw new ApiException(ExceptionEnum.FUNDING_ALREADY_DELETED_EXCEPTION);
            }

            return savedFundings;
        }
        else {
            throw new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION);
        }
    }

    // email 사용자의 펀딩 추가
    public Fundings addFundingByEmail(String email, FundingAddDto fundingAddDto, MultipartFile fundingThumbnail, MultipartFile contentImage) {
        // 1. DB에서 user 조회하기
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. state 값 지정
        byte state = this.calculateState(fundingAddDto.getStartDate());

        // 3. s3에 funding thumbnail image 업로드
        String publicThumbnailImageUrl = null;

        if (fundingThumbnail != null && !fundingThumbnail.isEmpty()) {
            try {
                publicThumbnailImageUrl = s3Service.uploadThumbnailImage(fundingThumbnail);
            } catch (IOException e) {
                throw new ApiException(ExceptionEnum.ILLEGAL_FUNDING_THUMBNAIL_IMAGE_EXCEPTION);
            }
        }

        // 4. s3에 funding content image 업로드
        String publicContentImageUrl = null;

        if (contentImage != null && !contentImage.isEmpty()) {
            try {
                publicContentImageUrl = s3Service.uploadContentImage(contentImage);
            } catch (IOException e) {
                throw new ApiException(ExceptionEnum.ILLEGAL_FUNDING_CONTENT_IMAGE_EXCEPTION);
            }
        }

        // 5. DB에 저장
        return fundingRepository.save(fundingAddDto.toEntity(savedUsers, state, publicThumbnailImageUrl, publicContentImageUrl));
    }

    // 현재 시간과 모금 시작일을 비교하여 상태값 반환
    public byte calculateState(LocalDate startDate) {
        LocalDate curDate = LocalDate.now();
        if (startDate.isAfter(curDate)) {
            return 0; // 대기 상태
        }
        return 1; // 모금 진행 중 상태
    }

    // fundingIdx에 해당하는 펀딩 수정
    public Fundings modifyFundingByFundingIdx(String email, int fundingIdx, FundingAddDto fundingAddDto, MultipartFile fundingThumbnail, MultipartFile contentImage) {
        // 1. user 정보 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 펀딩 정보 조회
        Fundings savedFundings = this.getFundingByFundingIdx(fundingIdx);

        // 3. 펀딩을 등록한 사람과 펀딩을 수정하려는 사람이 일치하는지 확인
        if (savedUsers.getUserIdx() != savedFundings.getCorporation().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 4. 수정하려는 펀딩이 모금 종료되었거나 진행 중이라면 수정 불가
        if (savedFundings.getState() == (byte) 2) {
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_DONE_FUNDING_MODIFICATION_EXCEPTION);
        }

        if (savedFundings.getState() == (byte) 1) {
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_PROGRESS_MODIFICATION_EXCEPTION);
        }

        // 수정할 썸네일 사진이 있을 경우, 썸네일 사진 변경
        if (fundingThumbnail != null && !fundingThumbnail.isEmpty()) {

            // 기존의 썸네일 사진 삭제
            String thumbnailImageUrl = savedFundings.getFundingThumbnail();
            if (thumbnailImageUrl != null) {
                String objectKey = s3Service.parseObjectKeyFromCloudfrontUrl(thumbnailImageUrl);
                s3Service.deleteThumbnailImage(objectKey);
            }

            // 새로운 썸네일 사진 업로드
            try {
                String modifiedThumbnailImageUrl = s3Service.uploadThumbnailImage(fundingThumbnail);
                savedFundings.setFundingThumbnail(modifiedThumbnailImageUrl);
            } catch (IOException e) {
                throw new ApiException(ExceptionEnum.ILLEGAL_FUNDING_THUMBNAIL_IMAGE_EXCEPTION);
            }
        }

        // 수정할 컨텐츠 사진이 있을 경우, 컨텐츠 사진 변경
        if (contentImage != null && !contentImage.isEmpty()) {

            // 기존의 컨텐츠 사진 삭제
            String contentImageUrl = savedFundings.getFundingContentImage();
            if (contentImageUrl != null) {
                String objectKey = s3Service.parseObjectKeyFromCloudfrontUrl(contentImageUrl);
                s3Service.deleteContentImage(objectKey);
            }

            // 새로운 컨텐츠 사진 업로드
            try {
                String modifiedContentImageUrl = s3Service.uploadContentImage(contentImage);
                savedFundings.setFundingContentImage(modifiedContentImageUrl);
            } catch (IOException e) {
                throw new ApiException(ExceptionEnum.ILLEGAL_FUNDING_CONTENT_IMAGE_EXCEPTION);
            }
        }

        // 5. 펀딩 정보 수정
        // state 값 지정
        byte state = this.calculateState(fundingAddDto.getStartDate());

        savedFundings.setState(state);
        savedFundings.setFundingTitle(fundingAddDto.getFundingTitle());
        savedFundings.setFundingContent(fundingAddDto.getFundingContent());
        savedFundings.setGoalMoney(fundingAddDto.getGoalMoney());
        savedFundings.setStartDate(fundingAddDto.getStartDate());
        savedFundings.setEndDate(fundingAddDto.getEndDate());
        savedFundings.setFundingType(fundingAddDto.getFundingType());

        // 6. DB에 저장
        return fundingRepository.save(savedFundings);
    }

    // fundingIdx에 해당하는 펀딩 삭제
    public Fundings deleteFundingByFundingIdx(String email, int fundingIdx) {
        // 1. user 정보 조회
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users savedUsers = savedUserDto.toEntity();

        // 2. 펀딩 정보 조회
        Fundings savedFundings = this.getFundingByFundingIdx(fundingIdx);

        // 3. 펀딩을 등록한 사람과 펀딩을 삭제하려는 사람이 일치하는지 확인
        if (savedUsers.getUserIdx() != savedFundings.getCorporation().getUserIdx()) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        // 4. 삭제하려는 펀딩이 모금 종료되었거나 진행 중이라면 삭제 불가
        if (savedFundings.getState() == (byte) 2) {
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_DONE_FUNDING_DELETION_EXCEPTION);
        }

        if (savedFundings.getState() == (byte) 1) {
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_FUNDING_IN_PROGRESS_DELETION_EXCEPTION);
        }

        // 5. 삭제
        int cnt = fundingRepository.updateIsDeletedTrueByFundingIdx(fundingIdx);

        if (cnt == 0) {
            throw new ApiException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }

        savedFundings.setDeleted(true);
        return savedFundings;
    }



}
