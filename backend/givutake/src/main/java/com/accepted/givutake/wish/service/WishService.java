package com.accepted.givutake.wish.service;

import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.repository.GiftRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import com.accepted.givutake.wish.entity.Wish;
import com.accepted.givutake.wish.model.CreateWishDto;
import com.accepted.givutake.wish.model.WishDto;
import com.accepted.givutake.wish.repository.WishRepository;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishService {

    private final WishRepository wishRepository;
    private final GiftRepository giftRepository;
    private final UserService userService;

    public void createWish(String email , CreateWishDto request) { // 찜 추가
        Gifts gift = giftRepository.findById(request.getGiftIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        UserDto userDto = userService.getUserByEmail(email);
        Users user = userDto.toEntity();
        if(isWish(email,gift.getGiftIdx())){
            throw new ApiException(ExceptionEnum.NOT_ALLOWED_WISH_INSERTION_EXCEPTION);
        }
        Wish newWish = Wish.builder()
                .gift(gift)
                .users(user)
                .build();
        wishRepository.save(newWish);
    }

    public List<WishDto> getWishList(String email, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        UserDto userDto = userService.getUserByEmail(email);
        Users user = userDto.toEntity();

        Specification<Wish> spec = (root, query, cb) -> {
            Join<Wish, Gifts> giftJoin = root.join("gift");
            return cb.and(
                    cb.equal(root.get("users"), user),
                    cb.equal(giftJoin.get("isDelete"), false)
            );
        };

        Page<Wish> wishList = wishRepository.findAll(spec, pageable);

        return wishList.map(wish -> WishDto.builder()
                .wishIdx(wish.getWishIdx())
                .giftIdx(wish.getGift().getGiftIdx())
                .giftName(wish.getGift().getGiftName())
                .giftThumbnail(wish.getGift().getGiftThumbnail())
                .userIdx(wish.getUsers().getUserIdx())
                .build()
        ).toList();
    }

    public void deleteWish(String email,int wishIdx) {
        Wish wish = wishRepository.findById(wishIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_WISH_EXCEPTION));
        if(!wish.getUsers().getEmail().equals(email)) {
            throw new ApiException(ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
        wishRepository.delete(wish);
    }

    public boolean isWish(String email, int giftIdx) {
        UserDto userDto = userService.getUserByEmail(email);
        Users user = userDto.toEntity();

        Gifts gift = giftRepository.findById(giftIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));
        Optional<Wish> wish = wishRepository.findByUsersAndGift(user,gift);
        return wish.isPresent();
    }
}
