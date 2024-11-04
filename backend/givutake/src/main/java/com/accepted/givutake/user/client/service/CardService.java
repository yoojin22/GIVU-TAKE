package com.accepted.givutake.user.client.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.user.client.entity.Cards;
import com.accepted.givutake.user.client.model.CardDto;
import com.accepted.givutake.user.client.model.CardViewDto;
import com.accepted.givutake.user.client.repository.CardsRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardsRepository cardsRepository;
    private final UsersRepository usersRepository;

    public List<CardViewDto> getCardListByEmail(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION));
        List<Cards> cards = cardsRepository.findByUsersAndIsDeletedFalse(user);
        return cards.stream()
                .map(CardViewDto::toDto)
                .collect(Collectors.toList());
    }

    // cardIdx에 해당하는 카드 조회
    public CardDto getCardByCardIdx(int cardIdx) {
        Optional<Cards> optionalCards = cardsRepository.findByCardIdx(cardIdx);

        if (optionalCards.isPresent()) {
            Cards savedCards = optionalCards.get();

            // 이미 삭제된 카드라면 조회 불가
            if (savedCards.isDeleted()) {
                throw new ApiException(ExceptionEnum.CARD_ALREADY_DELETED_EXCEPTION);
            }

            return CardDto.toDto(savedCards);
        }

        // 카드 정보가 없다면
        throw new ApiException(ExceptionEnum.NOT_FOUND_CARD_EXCEPTION);
    }

    // 카드 번호가 동일한 카드가 존재하는지 확인(삭제한 카드는 비교 대상에서 제외)
    public boolean isExistCardByCardNumber(String cardNumber) {
        return cardsRepository.existsByCardNumberAndIsDeletedFalse(cardNumber);
    }

    // 이전의 대표 카드를 false 처리
    public void updateRepresentativeCardFalse(Users users) {
        Optional<Cards> optionalRepresentativeCard = cardsRepository.findByUsersAndIsDeletedFalseAndIsRepresentativeTrue(users);

        if (optionalRepresentativeCard.isPresent()) {
            Cards representativeCard = optionalRepresentativeCard.get();
            representativeCard.setRepresentative(false);
            cardsRepository.save(representativeCard);
        }
    }

    // DB에 카드 등록
    public CardDto saveCard(Cards cards) {
        return CardDto.toDto(cardsRepository.save(cards));
    }

    // 카드 삭제
    public void deleteCardByCardIdx(int cardIdx) {
        int cnt = cardsRepository.updateIsDeletedTrueByCardIdx(cardIdx);

        if (cnt == 0) {
            throw new ApiException(ExceptionEnum.FAILED_CARD_DELETION_EXCEPTION);
        }
    }
}
