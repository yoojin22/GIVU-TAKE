package com.accepted.givutake.payment.service;

import com.accepted.givutake.funding.entity.Fundings;
import com.accepted.givutake.funding.repository.FundingRepository;
import com.accepted.givutake.gift.entity.Gifts;
import com.accepted.givutake.gift.repository.GiftRepository;
import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.accepted.givutake.payment.model.ApproveResponse;
import com.accepted.givutake.payment.model.CreateOrderDto;
import com.accepted.givutake.payment.model.CreateParticipateDto;
import com.accepted.givutake.payment.model.ReadyResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KaKaoPayService {

    private final GiftRepository giftRepository;
    private final FundingRepository fundingRepository;

    @Value("${kakao.pay.secret-key}")
    private String secretKey;

    @Value("${kakao.url}")
    private String Url;

    public ReadyResponse payGiftReady(String email, long orderIdx, CreateOrderDto request){

        Gifts gift = giftRepository.findById(request.getGiftIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_GIFT_EXCEPTION));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", "TC0ONETIME");                                    // 가맹점 코드(테스트용)
        parameters.put("partner_order_id", "Gift" + orderIdx);                       // 주문번호
        parameters.put("partner_user_id", email);                          // 회원 아이디
        parameters.put("item_name", gift.getGiftName());                                      // 상품명
        parameters.put("quantity", String.valueOf(request.getAmount()));                                        // 상품 수량
        parameters.put("total_amount", String.valueOf(request.getAmount()*gift.getPrice()));             // 상품 총액
        parameters.put("tax_free_amount", "0");                                 // 상품 비과세 금액
        parameters.put("approval_url", Url  + "/completed?orderIdx=" + orderIdx + "&email=" + email + "&type=Gift") ; // 결제 성공 시 URL
        parameters.put("cancel_url", Url  + "/cancel?orderIdx=" + orderIdx + "&email=" + email + "&type=Gift");      // 결제 취소 시 URL
        parameters.put("fail_url", Url  + "/fail?orderIdx=" + orderIdx + "&email=" + email + "&type=Gift");          // 결제 실패 시 URL

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
        ResponseEntity<ReadyResponse> responseEntity = template.postForEntity(url, requestEntity, ReadyResponse.class);

        return responseEntity.getBody();
    }

    public ReadyResponse payFundingReady(String email, long participantIdx, CreateParticipateDto request){

        Fundings funding = fundingRepository.findByFundingIdx(request.getFundingIdx()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FUNDING_WITH_IDX_EXCEPTION));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", "TC0ONETIME");                                    // 가맹점 코드(테스트용)
        parameters.put("partner_order_id", "Funding" + participantIdx);                       // 주문번호
        parameters.put("partner_user_id", email);                          // 회원 아이디
        parameters.put("item_name", funding.getFundingTitle());                                      // 상품명
        parameters.put("quantity", String.valueOf(1));                                        // 상품 수량
        parameters.put("total_amount", String.valueOf(request.getPrice()));             // 상품 총액
        parameters.put("tax_free_amount", "0");                                 // 상품 비과세 금액
        parameters.put("approval_url", Url  + "/completed?orderIdx=" + participantIdx + "&email=" + email + "&type=Funding") ; // 결제 성공 시 URL
        parameters.put("cancel_url", Url  + "/cancel?orderIdx=" + participantIdx + "&email=" + email + "&type=Funding");      // 결제 취소 시 URL
        parameters.put("fail_url", Url  + "/fail?orderIdx=" + participantIdx + "&email=" + email + "&type=Funding");          // 결제 실패 시 URL

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/ready";
        ResponseEntity<ReadyResponse> responseEntity = template.postForEntity(url, requestEntity, ReadyResponse.class);

        return responseEntity.getBody();
    }

    public ApproveResponse payApprove(String email, long idx, String tid, String pgToken, String type){

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", "TC0ONETIME");              // 가맹점 코드(테스트용)
        parameters.put("tid", tid);                       // 결제 고유번호
        parameters.put("partner_order_id", type + idx); // 주문번호
        parameters.put("partner_user_id", email);    // 회원 아이디
        parameters.put("pg_token", pgToken);              // 결제승인 요청을 인증하는 토큰

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
        String url = "https://open-api.kakaopay.com/online/v1/payment/approve";
        ApproveResponse approveResponse = template.postForObject(url, requestEntity, ApproveResponse.class);
        log.info("결제승인 응답객체: {}", approveResponse);

        return approveResponse;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", secretKey);
        headers.set("Content-type", "application/json");

        return headers;
    }
}
