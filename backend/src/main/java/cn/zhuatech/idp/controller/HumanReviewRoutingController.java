/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.idp.controller;

import cn.zhuatech.idp.common.ApiResponse;
import cn.zhuatech.idp.service.HumanReviewRoutingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/idp/insights")
public class HumanReviewRoutingController {
    private final HumanReviewRoutingService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public HumanReviewRoutingController(HumanReviewRoutingService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/human-review-routing")
    public ApiResponse<HumanReviewRoutingService.Result> route(
        @Valid @RequestBody HumanReviewRoutingService.Request request) {
        return ApiResponse.ok(service.route(request));
    }
}
