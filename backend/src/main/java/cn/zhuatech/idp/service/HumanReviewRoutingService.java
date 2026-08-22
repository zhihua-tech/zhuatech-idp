/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.idp.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class HumanReviewRoutingService {
    public Result route(Request request) {
        int riskScore = (int) Math.round((1 - request.extractionConfidence()) * 50);
        riskScore += Math.min(30, request.missingCriticalFields() * 15);
        if (request.handwritten()) riskScore += 15;
        if (request.documentValue().compareTo(new BigDecimal("100000")) >= 0) riskScore += 20;
        if (!request.trustedSource()) riskScore += 10;
        riskScore = Math.min(100, riskScore);
        String decision = request.duplicateDocument() ? "REJECT_DUPLICATE"
            : riskScore >= 50 ? "HIGH_PRIORITY_REVIEW"
            : riskScore >= 20 ? "STANDARD_REVIEW" : "AUTO_POST";

        List<String> actions = new ArrayList<>();
        if (request.missingCriticalFields() > 0) actions.add("补录并双人复核缺失的关键字段");
        if (request.handwritten()) actions.add("打开原图并进行手写内容人工核验");
        if (request.duplicateDocument()) actions.add("关联已有单据并阻止重复入账");
        if ("AUTO_POST".equals(decision)) actions.add("自动入账并保留模型版本与字段证据");
        if (actions.isEmpty()) actions.add("进入标准复核队列并按金额优先级排序");
        return new Result(request.documentNo(), riskScore, decision,
            queue(decision), actions);
    }

    private String queue(String decision) {
        return switch (decision) {
            case "HIGH_PRIORITY_REVIEW" -> "SENIOR_REVIEW";
            case "STANDARD_REVIEW" -> "GENERAL_REVIEW";
            case "REJECT_DUPLICATE" -> "EXCEPTION";
            default -> "STRAIGHT_THROUGH";
        };
    }

    public record Request(@NotBlank String documentNo,
                          @DecimalMin("0") @DecimalMax("1") double extractionConfidence,
                          @Min(0) int missingCriticalFields,
                          @DecimalMin("0") BigDecimal documentValue,
                          boolean handwritten, boolean duplicateDocument,
                          @Min(1) int pageCount, boolean trustedSource) {}

    public record Result(String documentNo, int riskScore, String decision,
                         String reviewQueue, List<String> actions) {}
}
