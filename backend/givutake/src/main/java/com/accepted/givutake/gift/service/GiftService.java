package com.accepted.givutake.gift.service;

import com.accepted.givutake.gift.entity.GiftReviewLiked;
import com.accepted.givutake.gift.entity.GiftReviews;
import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.model.*;
import com.accepted.givutake.gift.repository.GiftRepository;
import com.accepted.givutake.gift.repository.GiftReviewLikedRepository;
import com.accepted.givutake.gift.repository.GiftReviewRepository;
import com.accepted.givutake.global.entity.Categories;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.global.repository.CategoryRepository;
import com.accepted.givutake.global.service.S3Service;
import com.accepted.givutake.payment.entity.Orders;
import com.accepted.givutake.payment.repository.OrderRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.repository.UsersRepository;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GiftService {

    private final GiftRepository giftRepository;
    private final GiftReviewRepository giftReviewRepository;
    private final GiftReviewLikedRepository giftReviewLikedRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository userRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final S3Service s3Service;

    public Gifts createGift(String email, CreateGiftDto request, MultipartFile thumbnailImage, MultipartFile contentImage) {
        Categories category = categoryRepository.findById(request.getCategoryIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_CATEGORY_EXCEPTION));
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users corporation = savedUserDto.toEntity();
        String thumbnailImageUrl = null;
        String contentImageUrl = null;

        if(thumbnailImage != null && !thumbnailImage.isEmpty()){
            try{
                thumbnailImageUrl = s3Service.uploadProfileImage(thumbnailImage);
            } catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_THUMBNAIL_IMAGE_EXCEPTION);
            }
        }

        if(contentImage != null && !contentImage.isEmpty()){
            try{
                contentImageUrl = s3Service.uploadContentImage(contentImage);
            } catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_CONTENT_IMAGE_EXCEPTION);
            }
        }

        if((contentImage == null || contentImage.isEmpty()) && (request.getGiftContent() == null||request.getGiftContent().isEmpty()))throw new ApiException(ExceptionEnum.MISSING_GIFT_CONTENT_EXCEPTION);

        Gifts newGift = Gifts.builder()
                .giftName(request.getGiftName())
                .corporations(corporation)
                .category(category)
                .giftThumbnail(thumbnailImageUrl)
                .giftContentImage(contentImageUrl)
                .giftContent(request.getGiftContent())
                .price(request.getPrice())
                .build();
        return giftRepository.save(newGift);
    }

    public List<GiftDto> getGifts(String corporationEmail, String search, Integer categoryIdx ,int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        Specification<Gifts> spec = Specification.where((root, query, cb) -> cb.equal(root.get("isDelete"), false)); // 동적 쿼리 생성

        if(corporationEmail != null){
            Optional<Users> corporation = userRepository.findByEmail(corporationEmail);
            if (corporation.isPresent()) { // 특정 사용자가 등록한 물품
                spec = spec.and((root, query, cb) -> cb.equal(root.get("corporations"), corporation.get()));
            }
        }
        if(categoryIdx != null) {
            Optional<Categories> category = categoryRepository.findById(categoryIdx);
            if (category.isPresent()) { // 카테고리별 분류
                spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category.get()));
            }
        }

        if (search != null) { // 검색어 필터링
            spec = spec.and((root, query, cb) -> cb.like(root.get("giftName"), "%" + search + "%"));
        }

        Page<Gifts> giftList = giftRepository.findAll(spec, pageable);

        return giftList.map(gift -> GiftDto.builder()
                .giftIdx(gift.getGiftIdx())
                .giftName(gift.getGiftName())
                .giftThumbnail(gift.getGiftThumbnail())
                .corporationIdx(gift.getCorporations().getUserIdx())
                .corporationName(gift.getCorporations().getName())
                .corporationSido(gift.getCorporations().getRegion().getSido())
                .corporationSigungu(gift.getCorporations().getRegion().getSigungu())
                .categoryIdx(gift.getCategory().getCategoryIdx())
                .categoryName(gift.getCategory().getCategoryName())
                .price(gift.getPrice())
                .build()
        ).toList();
    }

    public GiftDto getGift(int giftIdx) {
        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        return GiftDto.builder()
                .giftIdx(gift.getGiftIdx())
                .giftName(gift.getGiftName())
                .giftThumbnail(gift.getGiftThumbnail())
                .giftContentImage(gift.getGiftContentImage())
                .giftContent(gift.getGiftContent())
                .corporationIdx(gift.getCorporations().getUserIdx())
                .corporationName(gift.getCorporations().getName())
                .corporationSido(gift.getCorporations().getRegion().getSido())
                .corporationSigungu(gift.getCorporations().getRegion().getSigungu())
                .categoryIdx(gift.getCategory().getCategoryIdx())
                .categoryName(gift.getCategory().getCategoryName())
                .price(gift.getPrice())
                .createdDate(gift.getCreatedDate())
                .modifiedDate(gift.getModifiedDate())
                .build();
    }

    public Gifts updateGift(String email, int giftIdx, UpdateGiftDto request, MultipartFile thumbnailImage, MultipartFile contentImage) {
        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        if(!gift.getCorporations().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        String thumbnailImageUrl = gift.getGiftThumbnail();
        String contentImageUrl = gift.getGiftContentImage();

        if(thumbnailImage != null && !thumbnailImage.isEmpty()){
            try{
                if(thumbnailImageUrl!=null)s3Service.deleteThumbnailImage(thumbnailImageUrl);
                thumbnailImageUrl = s3Service.uploadProfileImage(thumbnailImage);
            } catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_THUMBNAIL_IMAGE_EXCEPTION);
            }
        }

        if(contentImage != null && !contentImage.isEmpty()){
            try{
                if(contentImageUrl!=null)s3Service.deleteContentImage(contentImageUrl);
                contentImageUrl = s3Service.uploadContentImage(contentImage);
            } catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_CONTENT_IMAGE_EXCEPTION);
            }
        }

        if((contentImage == null||contentImage.isEmpty())&&(request.getGiftContent() == null||request.getGiftContent().isEmpty()))throw new ApiException(ExceptionEnum.MISSING_GIFT_CONTENT_EXCEPTION);

        gift.setGiftName(request.getGiftName());
        gift.setGiftThumbnail(thumbnailImageUrl);
        gift.setGiftContentImage(contentImageUrl);
        gift.setGiftContent(request.getGiftContent());
        gift.setCategory(categoryRepository.findById(request.getCategoryIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_CATEGORY_EXCEPTION)));
        gift.setPrice(request.getPrice());
        return giftRepository.save(gift);
    }

    public Gifts deleteGift(String authority, String email, int giftIdx) {
        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        if(!(gift.getCorporations().getEmail().equals(email)||authority.equals("ROLE_ADMIN"))){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        gift.setDelete(true);
        return giftRepository.save(gift);
    }

    public boolean IsWriteGiftReview(String email, Long orderIdx){
        Orders order = orderRepository.findById(orderIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_ORDER_EXCEPTION));
        if(!order.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        return giftReviewRepository.existsByOrdersAndIsDeleteFalse(order);
    }

    public void createGiftReview(String email, CreateGiftReviewDto request, MultipartFile reviewImage) {
        Gifts gift = giftRepository.findById(request.getGiftIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        Orders order = orderRepository.findById(request.getOrderIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_ORDER_EXCEPTION));

        if(IsWriteGiftReview(email, request.getOrderIdx())){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_GIFT_REVIEW_INSERTION_EXCEPTION);
        }

        String reviewImageUrl = null;

        if(reviewImage != null && !reviewImage.isEmpty()){
            try {
                reviewImageUrl = s3Service.uploadReviewImage(reviewImage);
            }catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_REVIEW_IMAGE_EXCEPTION);
            }
        }

        GiftReviews giftReviews = GiftReviews.builder()
                .reviewImage(reviewImageUrl)
                .reviewContent(request.getReviewContent())
                .gifts(gift)
                .users(user)
                .orders(order)
                .build();
        giftReviewRepository.save(giftReviews);
    }

    public List<GiftReviewDto> getGiftReviews(int giftIdx, boolean isOrderLiked, int pageNo, int pageSize) {

        Pageable pageable;

        if(isOrderLiked){
            pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "likedCount"));
        }else {
            pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        }
        Gifts gifts = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));

        Specification<GiftReviews> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("gifts"), gifts),
                cb.equal(root.get("isDelete"), false)
        );

        Page<GiftReviews> reviewList = giftReviewRepository.findAll(spec, pageable);

        return reviewList.map(review -> GiftReviewDto.builder()
                .reviewIdx(review.getReviewIdx())
                .reviewImage(review.getReviewImage())
                .reviewContent(review.getReviewContent())
                .giftIdx(review.getGifts().getGiftIdx())
                .giftName(review.getGifts().getGiftName())
                .giftThumbnail(review.getGifts().getGiftThumbnail())
                .corporationName(review.getGifts().getCorporations().getName())
                .userIdx(review.getUsers().getUserIdx())
                .userName(review.getUsers().getName())
                .userProfileImage(review.getUsers().getProfileImageUrl())
                .orderIdx(review.getOrders().getOrderIdx())
                .likedCount(review.getLikedCount())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build()
        ).toList();
    }

    public List<GiftReviewDto> getUserReviews(String email, boolean isOrderLiked, int pageNo, int pageSize) {
        Pageable pageable;

        if(isOrderLiked){
            pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "likedCount"));
        }else {
            pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        }

        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();

        Specification<GiftReviews> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("users"), user),
                cb.equal(root.get("isDelete"), false)
        );

        Page<GiftReviews> reviewList = giftReviewRepository.findAll(spec, pageable);

        return reviewList.map(review -> GiftReviewDto.builder()
                .reviewIdx(review.getReviewIdx())
                .reviewImage(review.getReviewImage())
                .reviewContent(review.getReviewContent())
                .giftIdx(review.getGifts().getGiftIdx())
                .giftName(review.getGifts().getGiftName())
                .giftThumbnail(review.getGifts().getGiftThumbnail())
                .corporationName(review.getGifts().getCorporations().getName())
                .userIdx(review.getUsers().getUserIdx())
                .userName(review.getUsers().getName())
                .userProfileImage(review.getUsers().getProfileImageUrl())
                .orderIdx(review.getOrders().getOrderIdx())
                .likedCount(review.getLikedCount())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build()
        ).toList();
    }

    public GiftReviewDto getUserReview(String email, int reviewIdx){
        GiftReviews review = giftReviewRepository.findById(reviewIdx).orElseThrow(()->new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_EXCEPTION));
        if(!review.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        if(review.isDelete()){
            throw new ApiException(ExceptionEnum.GIFT_REVIEW_ALREADY_DELETED_EXCEPTION);
        }
        return GiftReviewDto.builder()
                .reviewIdx(reviewIdx)
                .reviewImage(review.getReviewImage())
                .reviewContent(review.getReviewContent())
                .giftIdx(review.getGifts().getGiftIdx())
                .giftName(review.getGifts().getGiftName())
                .giftThumbnail(review.getGifts().getGiftThumbnail())
                .corporationName(review.getGifts().getCorporations().getName())
                .userIdx(review.getUsers().getUserIdx())
                .userName(review.getUsers().getName())
                .userProfileImage(review.getUsers().getProfileImageUrl())
                .orderIdx(review.getOrders().getOrderIdx())
                .createdDate(review.getCreatedDate())
                .modifiedDate(review.getModifiedDate())
                .build();
    }

    public void updateGiftReviews(String email, int reviewIdx, UpdateGiftReviewDto request, MultipartFile reviewImage) {
        GiftReviews review = giftReviewRepository.findById(reviewIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_EXCEPTION));
        if(!review.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }

        String reviewImgUrl = review.getReviewImage();

        if(reviewImage != null && !reviewImage.isEmpty()){
            try{
                if(reviewImgUrl!=null)s3Service.deleteThumbnailImage(reviewImgUrl);
                reviewImgUrl = s3Service.uploadProfileImage(reviewImage);
            } catch(IOException e){
                throw new ApiException(ExceptionEnum.ILLEGAL_GIFT_THUMBNAIL_IMAGE_EXCEPTION);
            }
        }

        review.setReviewImage(reviewImgUrl);
        review.setReviewContent(request.getReviewContent());
        giftReviewRepository.save(review);
    }
    public void deleteGiftReviews(String authority, String email, int reviewIdx) {
        GiftReviews review = giftReviewRepository.findById(reviewIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_EXCEPTION));
        if(!(review.getUsers().getEmail().equals(email)||authority.equals("ROLE_ADMIN"))){
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        review.setDelete(true);
        giftReviewRepository.save(review);
    }

    public boolean isLiked(String email, int reviewIdx) {
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        return giftReviewLikedRepository.existsByUserAndGiftReviews_ReviewIdx(user, reviewIdx);
    }


    public void createLiked(String email, int reviewIdx) {
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        GiftReviews review = giftReviewRepository.findById(reviewIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_EXCEPTION));

        if(review.isDelete()){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_OPERATION_ON_DELETED_REVIEW_EXCEPTION);
        }

        if(isLiked(email,reviewIdx)){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_LIKED_INSERTION_EXCEPTION);
        }
        GiftReviewLiked newLiked = GiftReviewLiked.builder()
                .user(user)
                .giftReviews(review)
                .build();
        giftReviewLikedRepository.save(newLiked);
        review.setLikedCount(review.getLikedCount()+1);
        giftReviewRepository.save(review);
    }

    public void deleteLiked(String email, int reviewIdx) {
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        GiftReviews review = giftReviewRepository.findById(reviewIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_EXCEPTION));

        if(review.isDelete()){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_OPERATION_ON_DELETED_REVIEW_EXCEPTION);
        }

        if(!isLiked(email,reviewIdx)){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_LIKED_DELETION_EXCEPTION);
        }
        GiftReviewLiked liked = giftReviewLikedRepository.findByUserAndGiftReviews(user, review)
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_REVIEW_LIKED_EXCEPTION));
        giftReviewLikedRepository.delete(liked);
        review.setLikedCount(review.getLikedCount()-1);
        giftReviewRepository.save(review);
    }

    public GiftYearStatisticsDto getGiftYearStatistics(String email, Integer giftIdx) {
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        int year = LocalDate.now().getYear();
        int[] arr = new int[13];
        List<Object[]> monthlyAmounts = orderRepository.findMonthlyOrderAmounts(user.getUserIdx(), year, giftIdx);

        GiftYearStatisticsDto statistics = new GiftYearStatisticsDto();

        for (Object[] result : monthlyAmounts) {
            int month = ((Number) result[0]).intValue();
            int amount = ((Number) result[1]).intValue();

            arr[month] = amount;

        }
        statistics.setArr(arr);

        return statistics;
    }

    public GiftPurchaserDto getGiftPurchaser(String email, Integer giftIdx) {
        UserDto savedUserDto = userService.getUserByEmail(email);
        Users user = savedUserDto.toEntity();
        List<Object[]> purchaserData = orderRepository.findPurchasersByGiftIdx(giftIdx, user.getUserIdx());

        List<GiftPurchaserDto.Purchaser> purchasers = purchaserData.stream()
                .map(data -> new GiftPurchaserDto.Purchaser(
                        (String) data[0],  // name
                        ((Number) data[1]).intValue()  // totalPrice
                ))
                .sorted(Comparator.comparingInt(GiftPurchaserDto.Purchaser::getPrice).reversed())
                .collect(Collectors.toList());

        return new GiftPurchaserDto(purchasers);
    }

    public List<GiftDto> getTop10Gifts() {
        List<Gifts> top10Gifts = giftRepository.findTop10ByIsDeleteFalseOrderByAmountDesc();
        return top10Gifts.stream()
                .map(gift -> GiftDto.builder()
                        .giftIdx(gift.getGiftIdx())
                        .giftName(gift.getGiftName())
                        .corporationIdx(gift.getCorporations().getUserIdx())
                        .corporationName(gift.getCorporations().getName())
                        .corporationSido(gift.getCorporations().getRegion().getSido())
                        .corporationSigungu(gift.getCorporations().getRegion().getSigungu())
                        .categoryIdx(gift.getCategory().getCategoryIdx())
                        .categoryName(gift.getCategory().getCategoryName())
                        .giftThumbnail(gift.getGiftThumbnail())
                        .price(gift.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public List<GiftDto> getRecentGifts(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));
        Optional<Orders> order = orderRepository.findTopByUsersOrderByCreatedDateDesc(user);

        if(order.isPresent()){
            Users corporation = order.get().getGift().getCorporations();
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
            Specification<Gifts> spec = Specification.where((root, query, cb) -> cb.equal(root.get("isDelete"), false));
            spec = spec.and((root, query, cb) -> cb.equal(root.get("corporations"), corporation));
            Page<Gifts> giftList = giftRepository.findAll(spec, pageable);
            return giftList.map(gift -> GiftDto.builder()
                    .giftIdx(gift.getGiftIdx())
                    .giftName(gift.getGiftName())
                    .giftThumbnail(gift.getGiftThumbnail())
                    .corporationIdx(gift.getCorporations().getUserIdx())
                    .corporationName(gift.getCorporations().getName())
                    .corporationSido(gift.getCorporations().getRegion().getSido())
                    .corporationSigungu(gift.getCorporations().getRegion().getSigungu())
                    .categoryIdx(gift.getCategory().getCategoryIdx())
                    .categoryName(gift.getCategory().getCategoryName())
                    .price(gift.getPrice())
                    .build()
            ).toList();
        }else return null;

    }
}
