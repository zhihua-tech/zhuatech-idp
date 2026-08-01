/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.idp.service;

import cn.zhuatech.idp.common.BusinessException;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 对 OCR、必填字段、签章和表格抽取质量进行统一门禁。 */
@Service
public class DocumentQualityService {
    public QualityResult evaluate(QualityRequest request) {
        if (request.extractedRequiredFields() > request.requiredFields()) {
            throw new BusinessException("已抽取必填字段数不能超过必填字段总数");
        }
        int missingFields = request.requiredFields() - request.extractedRequiredFields();
        List<String> issues = new ArrayList<>();
        if (request.ocrConfidence() < 0.85) issues.add("OCR 置信度低于 0.85");
        if (missingFields > 0) issues.add("缺少 " + missingFields + " 个必填字段");
        if (request.signatureRequired() && !request.signaturePresent()) issues.add("缺少签章");
        if (request.tableConfidence() < 0.8) issues.add("表格结构抽取置信度偏低");
        int qualityScore = Math.max(0, (int) Math.round(request.ocrConfidence() * 40 + request.tableConfidence() * 25 + (1 - missingFields * 1.0 / request.requiredFields()) * 25 + (!request.signatureRequired() || request.signaturePresent() ? 10 : 0)));
        String decision = request.signatureRequired() && !request.signaturePresent() ? "REJECT" : issues.isEmpty() ? "PASS" : "MANUAL_REVIEW";
        return new QualityResult(decision, qualityScore, missingFields, List.copyOf(issues), "PASS".equals(decision) ? "写入业务系统" : "REJECT".equals(decision) ? "退回补充签章" : "进入人工复核队列");
    }

    public record QualityRequest(
        @NotBlank(message = "请输入文档类型") String documentType,
        @DecimalMin("0.0") @DecimalMax("1.0") double ocrConfidence,
        @Positive int requiredFields,
        @PositiveOrZero int extractedRequiredFields,
        boolean signatureRequired,
        boolean signaturePresent,
        @DecimalMin("0.0") @DecimalMax("1.0") double tableConfidence
    ) {}

    public record QualityResult(String decision, int qualityScore, int missingFields, List<String> issues, String nextAction) {}
}
