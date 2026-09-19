/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.idp.ai;
import org.springframework.stereotype.Component; import java.util.Map;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface AiProvider { /**
                               * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                               */
AiResult execute(String prompt,Map<String,String> context); /**
                                                                                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                           */
record AiResult(String provider,String answer,Map<String,Object> evidence){} }
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Component class DemoAiProvider implements AiProvider { /**
                                                         * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                         */
public AiResult execute(String prompt,Map<String,String> context){return new AiResult("demo-document-provider","已生成演示字段抽取结果，关键金额和账户字段须经过规则与人工复核。",Map.of("fields",16,"confidence",0.987,"humanReview",true));} }
