/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.idp;

import cn.zhuatech.idp.service.HumanReviewRoutingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HumanReviewRoutingServiceTests {
    private final HumanReviewRoutingService service = new HumanReviewRoutingService();

    @Test
    void routesHighValueLowConfidenceDocumentToSeniorReview() {
        var result = service.route(new HumanReviewRoutingService.Request(
            "INV-2026-1001", .68, 1, new BigDecimal("180000"), true,
            false, 3, true));

        assertEquals("HIGH_PRIORITY_REVIEW", result.decision());
        assertEquals("SENIOR_REVIEW", result.reviewQueue());
    }

    @Test
    void autoPostsTrustedHighConfidenceDocument() {
        var result = service.route(new HumanReviewRoutingService.Request(
            "INV-2026-1002", .99, 0, new BigDecimal("5000"), false,
            false, 1, true));

        assertEquals("AUTO_POST", result.decision());
    }
}
