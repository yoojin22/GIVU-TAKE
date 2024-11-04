package com.accepted.givutake.payment.service;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.repository.FundingRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.payment.entity.FundingParticipants;
import com.accepted.givutake.payment.model.CreateParticipateDto;
import com.accepted.givutake.payment.model.ParticipantDto;
import com.accepted.givutake.payment.repository.FundingParticipantsRepository;
import com.accepted.givutake.user.common.entity.Users;
import com.accepted.givutake.user.common.model.UserDto;
import com.accepted.givutake.user.common.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final UserService userService;
    private final FundingParticipantsRepository fundingParticipantsRepository;
    private final FundingRepository fundingRepository;

    public FundingParticipants createParticipants(String email, CreateParticipateDto request){
        UserDto userDto = userService.getUserByEmail(email);
        Users user = userDto.toEntity();
        Fundings funding = fundingRepository.findByFundingIdx(request.getFundingIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));
        FundingParticipants newParticipant = FundingParticipants.builder()
                .fundings(funding)
                .users(user)
                .fundingFee(request.getPrice())
                .paymentMethod(request.getPaymentMethod())
                .cardNumber(request.getCardNumber())
                .build();
        return fundingParticipantsRepository.save(newParticipant);
    }

    public List<ParticipantDto> getParticipants(String email, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));

        UserDto userDto = userService.getUserByEmail(email);
        Users user = userDto.toEntity();

        Page<FundingParticipants> participantList = fundingParticipantsRepository.findByUsers(user, pageable);

        return participantList.map(participant -> ParticipantDto.builder()
                .participantIdx(participant.getParticipantIdx())
                .fundingIdx(participant.getFundings().getFundingIdx())
                .fundingTitle(participant.getFundings().getFundingTitle())
                .fundingThumbnail(participant.getFundings().getFundingThumbnail())
                .fundingType(participant.getFundings().getFundingType())
                .participatedDate(participant.getCreatedDate())
                .price(participant.getFundingFee())
                .build()
        ).toList();
    }

    public void updateFunding(int fundingIdx,int price){
        Fundings funding = fundingRepository.findByFundingIdx(fundingIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));

        funding.setTotalMoney(funding.getTotalMoney()+price);

        fundingRepository.save(funding);
    }

    public void deleteParticipant(String email, long participantIdx){
        FundingParticipants participant = fundingParticipantsRepository.findById(participantIdx).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_PARTICIPATE_EXCEPTION));

        if(! participant.getUsers().getEmail().equals(email)){
            throw new ApiException(ExceptionEnum.NOT_FOUND_USER_WITH_EMAIL_EXCEPTION);
        }

        fundingParticipantsRepository.delete(participant);
    }

}
