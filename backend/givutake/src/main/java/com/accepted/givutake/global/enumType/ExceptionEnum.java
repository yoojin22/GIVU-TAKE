package com.accepted.givutake.global.enumType;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
    // System Exception
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0001", "실행 중 오류가 발생했습니다."),
    ACCESS_DENIED_EXCEPTION(HttpStatus.FORBIDDEN, "ES0002"),
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0003"),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0004"),
    SECURITY_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0005", "보안 위반으로 인해 토큰 처리에 실패했습니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0006", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0007", "지원하지 않는 토큰입니다."),
    MALFORMED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0008", "잘못된 토큰 형식입니다."),
    INVALID_SIGNATURE_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0009", "유효하지 않은 토큰 서명입니다."),
    INVALID_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "ES0010", "유효하지 않은 토큰입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.UNAUTHORIZED, "ES0011", "서버에 문제가 발생했습니다."),
    MESSAGING_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0012", "이메일 전송에 실패했습니다."),
    HTTP_MESSAGE_NOT_READABLE_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0013", "요청 본문을 처리할 수 없습니다."),
    BAD_CREDENTIALS_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0014", "아이디나 비밀번호가 일치하지 않습니다."),
    INTERNAL_AUTHENTICATION_SERVICE_EXCEPTION(HttpStatus.BAD_REQUEST, "ES0015"),

    // Custom Exception
    ILLEGAL_ISMALE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0001", "성별 정보가 유효하지 않습니다."),
    ILLEGAL_BIRTH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0002", "생년월일 정보가 유효하지 않습니다."),
    ILLEGAL_STATUS_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0003", "상태 정보가 유효하지 않습니다"),
    ILLEGAL_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0004", "이메일 형식이 올바르지 않습니다."),
    ILLEGAL_ISAPPROVED_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0010", "isApproved는 'Y'이거나 'N'이어야 합니다."),
    ILLEGAL_PROFILE_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0011", "잘못된 프로필 이미지 입니다."),
    ILLEGAL_FUNDING_THUMBNAIL_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "EF0001", "잘못된 썸네일 이미지 입니다."),
    ILLEGAL_FUNDING_CONTENT_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "EF0002", "잘못된 컨텐츠 이미지 입니다."),
    ILLEGAL_BUSINESS_REGISTRATION_CERTIFICATE_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0013", "잘못된 사업자 등록증 입니다."),
    ILLEGAL_GIFT_THUMBNAIL_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST,"EG0011", "잘못된 썸네일 이미지 입니다."),
    ILLEGAL_GIFT_CONTENT_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST,"EG_0012", "잘못된 컨텐츠 이미지 입니다."),
    ILLEGAL_GIFT_REVIEW_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST,"EG_0013","잘못된 리뷰 이미지 입니다."),
    ILLEGAL_CLOUDFRONT_URL_EXCEPTION(HttpStatus.BAD_REQUEST, "EU0020", "잘못된 cloudfront url 입니다."),
    ILLEGAL_REPRESENTATIVE_ADDRESS_EXCEPTION(HttpStatus.BAD_REQUEST, "EA0001", "대표 주소 정보가 유효하지 않습니다."),
    ILLEGAL_REPRESENTATIVE_CARD_EXCEPTION(HttpStatus.BAD_REQUEST, "EC0001", "대표 카드 정보가 유효하지 않습니다."),
    ILLEGAL_FUNDINGTYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "", "펀딩 종류는 'R' 또는 'D'만 허용됩니다."),

    DUPLICATED_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, "EU1004", "이미 사용 중인 이메일입니다."),
    DUPLICATED_CARD_EXCEPTION(HttpStatus.BAD_REQUEST, "EC1000", "이미 동일한 카드가 등록 되어 있습니다."),

    UNEXPECTED_SIDO_EXCEPTION(HttpStatus.BAD_REQUEST, "ER2001", "시/도 정보는 포함할 수 없습니다."),
    UNEXPECTED_SIGUNGU_EXCEPTION(HttpStatus.BAD_REQUEST, "ER2002", "시/군/구 정보는 포함할 수 없습니다."),
    UNEXPECTED_REPRESENTATIVE_ADDRESS_EXCEPTION(HttpStatus.BAD_REQUEST, "EA2001", "대표 주소 정보는 포함할 수 없습니다."),
    UNEXPECTED_ISMALE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU2001", "성별 정보는 포함할 수 없습니다."),
    UNEXPECTED_BIRTH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU2002", "생년월일 정보는 포함할 수 없습니다."),
    UNEXPECTED_STATUS_EXCEPTION(HttpStatus.BAD_REQUEST, "EU2003", "상태 정보는 포함할 수 없습니다."),
    UNEXPECTED_LANDLINE_PHONE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU2005", "유선 전화 정보는 포함할 수 없습니다."),
    UNEXPECTED_MOBILE_PHONE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU2006", "휴대폰 번호 정보는 포함할 수 없습니다."),

    MISSING_SIDO_EXCEPTION(HttpStatus.BAD_REQUEST,"ER3001", "시/도명은 필수 입력 값 입니다."),
    MISSING_SIGUNGU_EXCEPTION(HttpStatus.BAD_REQUEST, "ER3002", "시/군/구명은 필수 입력 값 입니다."),
    MISSING_ISMALE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU3001", "성별은 필수 입력 값 입니다."),
    MISSING_BIRTH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU3002", "생년월일은 필수 입력 값 입니다."),
    MISSING_REPRESENTATIVE_ADDRESS_EXCEPTION(HttpStatus.BAD_REQUEST, "EA3001", "대표 주소는 필수 입력 값 입니다."),
    MISSING_BUSINESS_REGISTRATION_CERTIFICATE_IMAGE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU3013", "사업자 등록증은 필수 입력 값입니다."),
    MISSING_GIFT_CONTENT_EXCEPTION(HttpStatus.BAD_REQUEST,"EG3001","답례품 이미지나 설명을 등록해주세요"),

    NOT_FOUND_USER_WITH_EMAIL_EXCEPTION(HttpStatus.NOT_FOUND, "EU4000", "해당 이메일을 가진 사용자를 찾을 수 없습니다."),
    NOT_FOUND_REFRESHTOKEN_EXCEPTION(HttpStatus.NOT_FOUND, "EU4020", "토큰 정보를 찾을 수 없습니다."),
    NOT_FOUND_EMAILCODE_EXCEPTION(HttpStatus.NOT_FOUND, "EU4030", "인증 코드 정보를 찾을 수 없습니다."),
    NOT_FOUND_ADDRESSES_EXCEPTION(HttpStatus.NOT_FOUND, "EA4000", "주소 정보를 찾을 수 없습니다."),
    NOT_FOUND_REPRESENTATIVE_ADDRESS_EXCEPTION(HttpStatus.NOT_FOUND, "EA4001", "대표 주소 정보를 찾을 수 없습니다."),
    NOT_FOUND_FUNDING_REVIEW_EXCEPTION(HttpStatus.NOT_FOUND, "EF4010", "펀딩 후기 정보를 찾을 수 없습니다."),
    NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION(HttpStatus.NOT_FOUND, "EF4000", "펀딩 정보를 찾을 수 없습니다."),
    NOT_FOUND_FUNDING_PARTICIPATE_EXCEPTION(HttpStatus.NOT_FOUND, "EF4020","펀딩 참여 정보를 찾을수 없습니다"),
    NOT_FOUND_CHEER_COMMENT_EXCEPTION(HttpStatus.NOT_FOUND, "EF4020", "댓글 정보를 찾을 수 없습니다."),
    NOT_FOUND_REGION_EXCEPTION(HttpStatus.NOT_FOUND, "ER4000", "지역 정보를 찾을 수 없습니다."),
    NOT_FOUND_CARD_EXCEPTION(HttpStatus.NOT_FOUND, "EC4000", "카드 정보를 찾을 수 없습니다."),

    USER_ALREADY_WITHDRAWN_EXCEPTION(HttpStatus.NOT_FOUND, "EU5000", "이미 탈퇴한 회원입니다."),
    ADDRESSES_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "EA5000", "이미 삭제된 주소입니다."),
    FUNDING_REVIEWS_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "EF5010", "이미 삭제된 펀딩 후기 입니다."),
    FUNDING_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "EF5000", "이미 삭제된 펀딩 입니다."),
    CHEER_COMMENT_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "EF5020", "이미 삭제된 댓글 입니다."),
    GIFT_REVIEW_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND,"EG5001","이미 삭제된 후기 입니다."),
    CARD_ALREADY_DELETED_EXCEPTION(HttpStatus.NOT_FOUND, "EC5000", "이미 삭제된 카드입니다."),

    PASSWORD_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU6007", "비밀번호가 일치하지 않습니다."),
    REFRESHTOKEN_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU6020", "토큰 정보가 일치하지 않습니다."),
    EMAILCODE_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU6030", "인증 코드가 일치하지 않습니다."),
    ADMING_SIGNUP_CODE_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "EU6050", "관리자 회원가입을 위한 확인 코드가 일치하지 않습니다."),

    NOT_ALLOWED_LAST_ADDRESS_DELETION_EXCEPTION(HttpStatus.BAD_REQUEST, "EA7002", "마지막 주소 정보는 삭제할 수 없습니다."),
    NOT_ALLOWED_FUNDING_REVIEW_INSERTION_EXCEPTION(HttpStatus.BAD_REQUEST, "EF7010", "이미 펀딩 후기가 작성 되어 있습니다."),
    NOT_ALLOWED_DONE_FUNDING_DELETION_EXCEPTION(HttpStatus.BAD_REQUEST, "EF7002", "모금이 종료된 펀딩은 삭제할 수 없습니다."),
    NOT_ALLOWED_FUNDING_IN_PROGRESS_DELETION_EXCEPTION(HttpStatus.BAD_REQUEST, "EF7003", "모금이 진행 중인 펀딩은 삭제할 수 없습니다."),
    NOT_ALLOWED_DONE_FUNDING_MODIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "EF7102", "모금이 종료된 펀딩은 수정할 수 없습니다."),
    NOT_ALLOWED_FUNDING_IN_PROGRESS_MODIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "EF7103", "모금이 진행 중인 펀딩은 수정할 수 없습니다."),
    NOT_ALLOWED_FUNDING_IN_BEFORE_STATISTICS_EXCEPTION(HttpStatus.BAD_REQUEST,"EF7104","모금이 시작 전인 펀딩은 통계를 볼 수 없습니다."),
    NOT_ALLOWED_FUNDING_IN_PROCESS_STATISTICS_EXCEPTION(HttpStatus.BAD_REQUEST,"EF7105","모금이 진행 중인 펀딩은 통계를 볼 수 없습니다."),
    NOT_ALLOWED_LAST_ADDRESS_ISREPRESENTATIVE_EXCEPTION(HttpStatus.BAD_REQUEST, "EA7001", "회원 당 대표 주소 1개는 필수 입니다."),
    NOT_ALLOWED_WISH_INSERTION_EXCEPTION(HttpStatus.BAD_REQUEST, "EW7010","이미 찜목록에 추가 되어 있습니다."),
    NOT_ALLOWED_LIKED_INSERTION_EXCEPTION(HttpStatus.BAD_REQUEST,"EG7010","이미 좋아요를 추가하였습니다."),
    NOT_ALLOWED_LIKED_DELETION_EXCEPTION(HttpStatus.BAD_REQUEST,"EG7020","해당 좋아요를 삭제할 수 없습니다."),
    NOT_ALLOWED_OPERATION_ON_DELETED_REVIEW_EXCEPTION(HttpStatus.BAD_REQUEST,"EG7030", "해당 리뷰는 삭제된 리뷰입니다."),
    NOT_ALLOWED_GIFT_REVIEW_INSERTION_EXCEPTION(HttpStatus.BAD_REQUEST,"EG7040","해당 주문의 리뷰는 이미 작성하셨습니다."),
    NOT_ALLOWED_UPDATE_CORPORATION_EXCEPTION(HttpStatus.BAD_REQUEST, "ES7080", "이미 수혜자 자격이 승인된 사용자입니다."),
    NOT_ALLOWED_UPDATE_CORPORATIONYET_EXCEPTION(HttpStatus.BAD_REQUEST, "ES7081", "이미 수혜자 자격이 승인되지 않은 사용자입니다."),

    FAILED_CARD_DELETION_EXCEPTION(HttpStatus.OK, "EC8001", "카드 정보 삭제에 실패했습니다."),
    FAILED_DONATION_RECEIPT_GENERATE_EXCEPTION(HttpStatus.BAD_REQUEST, "EU8009", "기부금 영수증 생성에 실패했습니다."),

    NOT_FOUND_CATEGORY_EXCEPTION(HttpStatus.NOT_FOUND, "EC0001", "해당 카테고리를 찾을 수 없습니다."),
    NOT_FOUND_GIFT_EXCEPTION(HttpStatus.NOT_FOUND,"EG0001", "해당 답례품을 찾을 수 없습니다."),
    NOT_FOUND_GIFT_REVIEW_EXCEPTION(HttpStatus.NOT_FOUND,"EG1001","해당 답례품 후기를 찾을 수 없습니다."),
    NOT_FOUND_GIFT_REVIEW_LIKED_EXCEPTION(HttpStatus.NOT_FOUND,"EG2001", "해당 좋아요를 찾을 수 없습니다."),
    NOT_FOUND_WISH_EXCEPTION(HttpStatus.NOT_FOUND,"EW0001", "해당 찜을 찾을 수 없습니다."),
    NOT_FOUND_SHOPPING_CART_EXCEPTION(HttpStatus.NOT_FOUND,"EP0001", "해당 장바구니를 찾을 수 없습니다."),
    NOT_FOUND_ORDER_EXCEPTION(HttpStatus.NOT_FOUND, "EO0001", "해당 주문을 찾을 수 없습니다."),
    NOT_FOUND_QNA_EXCEPTION(HttpStatus.NOT_FOUND,"EN0001", "해당 Q&A를 찾을 수 없습니다."),
    NOT_FOUND_QNA_ANSWER_EXCEPTION(HttpStatus.NOT_FOUND,"EN0002", "해당 Q&A의 답변을 찾을 수 없습니다"),

    UNEXPECTED_DATA_EXCEPTION(HttpStatus.FORBIDDEN, "EQ0001", "예상치 못한 값이 들어왔습니다.");

    private final HttpStatus status;
    private final String code;
    private String message;

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}