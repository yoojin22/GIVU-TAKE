package com.accepted.givutake.pdf;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PdfService {

    public byte[] generateDonationReceipt(DonationReceiptFormDto donationReceiptFormDto) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // 용지 및 여백 설정
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try{
            PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
            writer.setInitialLeading(12.5f);

            document.open(); //생성된 파일을 오픈
            XMLWorkerHelper helper = XMLWorkerHelper.getInstance();

            // 사용할 CSS 준비
            CSSResolver cssResolver = new StyleAttrCSSResolver();
            CssFile cssFile = null;
            try {
                ClassPathResource cssResource = new ClassPathResource("pdf.css");
                cssFile = XMLWorkerHelper.getCSS(cssResource.getInputStream());
            } catch (FileNotFoundException e) {
                log.error("PdfService - donationReceiptGenerate의 CSS 파일 읽어 들이기 실패: {}", e.getMessage());
                throw new ApiException(ExceptionEnum.FAILED_DONATION_RECEIPT_GENERATE_EXCEPTION);
            }
            cssResolver.addCss(cssFile);

            // HTML 과 폰트준비
            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
            try {
                ClassPathResource cssResource = new ClassPathResource("malgun.ttf");
                fontProvider.register(cssResource.getPath(), "MalgunGothic"); // 'MalgunGothic'은 폰트 별칭
            } catch (Exception e) {
                log.error("PdfService - 폰트 파일 로드 실패: {}", e.getMessage());
                throw new ApiException(ExceptionEnum.FAILED_DONATION_RECEIPT_GENERATE_EXCEPTION);
            }

            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);

            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser xmlParser = new XMLParser(worker, StandardCharsets.UTF_8);

            StringBuilder htmlStrBuilder = new StringBuilder();
            htmlStrBuilder.append("<!DOCTYPE html>\n");
            htmlStrBuilder.append("<html lang=\"ko\">\n");
            htmlStrBuilder.append("<head>\n");
            htmlStrBuilder.append("    <meta charset=\"UTF-8\"/>\n");
            htmlStrBuilder.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n");
            htmlStrBuilder.append("    <title>기부금 영수증</title>\n");
            htmlStrBuilder.append("</head>\n");
            htmlStrBuilder.append("<body style='font-family: MalgunGothic;'>\n");
            htmlStrBuilder.append("\n");
            htmlStrBuilder.append("<h1>기부금 영수증</h1>\n");
            htmlStrBuilder.append("<hr />\n");
            htmlStrBuilder.append("\n");
            htmlStrBuilder.append("<h2>1. 기부자</h2>\n");
            htmlStrBuilder.append("<table>\n");
            htmlStrBuilder.append("    <tr>\n");
            htmlStrBuilder.append("        <th>성 명</th>\n");
            htmlStrBuilder.append("        <td>").append(donationReceiptFormDto.getUserName()).append("</td>\n");
            htmlStrBuilder.append("        <th>전화번호</th>\n");
            htmlStrBuilder.append("        <td>").append(donationReceiptFormDto.getUserPhone()).append("</td>\n");
            htmlStrBuilder.append("    </tr>\n");
            htmlStrBuilder.append("    <tr>\n");
            htmlStrBuilder.append("        <th>주 소</th>\n");
            htmlStrBuilder.append("        <td colspan=\"3\">").append(donationReceiptFormDto.getUserAddress()).append("</td>\n");
            htmlStrBuilder.append("    </tr>\n");
            htmlStrBuilder.append("</table>\n");
            htmlStrBuilder.append("\n");
            htmlStrBuilder.append("<h2>2. 기부내용</h2>\n");
            htmlStrBuilder.append("<table>\n");
            htmlStrBuilder.append("    <tr>\n");
            htmlStrBuilder.append("        <th>유 형</th>\n");
            htmlStrBuilder.append("        <th>기부금 모집처</th>\n");
            htmlStrBuilder.append("        <th>날짜</th>\n");
            htmlStrBuilder.append("        <th>적요</th>\n");
            htmlStrBuilder.append("        <th colspan=\"2\">금 액</th>\n");
            htmlStrBuilder.append("    </tr>\n");
            List<DonationParticipantsDto> donationParticipantsDtoList = donationReceiptFormDto.getDonationParticipantsDtoList();
            long totalPrice = 0;
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            for (DonationParticipantsDto donationParticipantsDto : donationParticipantsDtoList ) {
                htmlStrBuilder.append("    <tr>\n");
                htmlStrBuilder.append("        <td>").append(donationParticipantsDto.getType()).append("</td>\n");
                htmlStrBuilder.append("        <td>").append(donationParticipantsDto.getName()).append("</td>\n");
                htmlStrBuilder.append("        <td>").append(donationParticipantsDto.getDate()).append("</td>\n");
                htmlStrBuilder.append("        <td>").append(donationParticipantsDto.getRef()).append("</td>\n");
                htmlStrBuilder.append("        <td colspan=\"2\">").append(decimalFormat.format(donationParticipantsDto.getPrice())).append("</td>\n");
                htmlStrBuilder.append("    </tr>\n");
                totalPrice += donationParticipantsDto.getPrice();
            }
            htmlStrBuilder.append("    <tr>\n");
            htmlStrBuilder.append("        <th colspan=\"4\">합계 내역</th>\n");
            htmlStrBuilder.append("        <td colspan=\"2\">").append(decimalFormat.format(totalPrice)).append("</td>\n");
            htmlStrBuilder.append("    </tr>\n");
            htmlStrBuilder.append("</table>\n");
            htmlStrBuilder.append("\n");
            htmlStrBuilder.append("<div class=\"footer\">\n");
            htmlStrBuilder.append("    <p>위와 같이 기부금을 기부하였음을 증명합니다.</p>\n");
            htmlStrBuilder.append("    <div class=\"footer-date\">").append(LocalDate.now().getYear()).append("년 ").append(LocalDate.now().getMonthValue()).append("월 ").append(LocalDate.now().getDayOfMonth()).append("일</div>\n");
            htmlStrBuilder.append("</div>\n");
            htmlStrBuilder.append("\n");
            htmlStrBuilder.append("</body>\n");
            htmlStrBuilder.append("</html>\n");
            String htmlStr = htmlStrBuilder.toString();

            StringReader strReader = new StringReader(htmlStr);
            xmlParser.parse(strReader);
            document.close();
            writer.close();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException(ExceptionEnum.FAILED_DONATION_RECEIPT_GENERATE_EXCEPTION);
        }

        return byteArrayOutputStream.toByteArray(); // PDF 데이터를 바이트 배열로 반환
    }
}
