package com.accepted.givutake.funding;

import com.accepted.givutake.funding.model.FundingAddDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidFundingAddDtoDates, FundingAddDto> {

    @Override
    public boolean isValid(FundingAddDto fundingAddDto, ConstraintValidatorContext context) {
        if (fundingAddDto.getStartDate() == null || fundingAddDto.getEndDate() == null) {
            return true; // null 값은 다른 @NotNull 등에서 처리하므로 여기선 패스
        }

        return !fundingAddDto.getEndDate().isBefore(fundingAddDto.getStartDate());
    }
}
