/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.idp.workbench;

import static cn.zhuatech.idp.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);

    keys(r, "minimumConfidence", "required", "fields");
    var threshold = decimal(r, "minimumConfidence", 0, 1);
    var required = strings(r, "required", 0, 100);
    if (new HashSet<>(required).size() != required.size())
      throw new IllegalArgumentException("必填字段重复");
    Set<String> ids = new HashSet<>();
    List<Map<String, Object>> out = new ArrayList<>(), issues = new ArrayList<>();
    for (var f : rows(r, "fields", 0, 100)) {
      keys(f, "id", "type", "value", "confidence");
      String id = id(f, "id");
      unique(ids, id);
      String type = text(f, "type", 10);
      if (!Set.of("TEXT", "DATE", "DECIMAL").contains(type))
        throw new IllegalArgumentException("字段类型错误");
      Object raw = f.get("value");
      if (!(raw instanceof String s) || s.length() > 4000)
        throw new IllegalArgumentException("字段值须为4000字符内文本");
      var confidence = decimal(f, "confidence", 0, 1);
      String normalized = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFKC).strip();
      Object value = normalized;
      boolean valid = !normalized.isEmpty();
      if (valid)
        try {
          if (type.equals("DATE")) value = date(normalized).toString();
          else if (type.equals("DECIMAL")) {
            if (!normalized.matches("[+-]?(?:[0-9]+|[0-9]{1,3}(?:,[0-9]{3})+)(?:[.][0-9]+)?"))
              throw new IllegalArgumentException("数值格式错误");
            var n = new BigDecimal(normalized.replace(",", ""));
            if (n.abs().compareTo(new BigDecimal("1000000000000")) > 0
                || Math.max(0, n.stripTrailingZeros().scale()) > 6)
              throw new IllegalArgumentException("数值范围或精度错误");
            value = n;
          }
        } catch (IllegalArgumentException ex) {
          valid = false;
          issues.add(Map.of("field", id, "type", "INVALID_VALUE"));
        }
      if (!valid && normalized.isEmpty()) issues.add(Map.of("field", id, "type", "EMPTY_VALUE"));
      if (confidence.compareTo(threshold) < 0)
        issues.add(Map.of("field", id, "type", "LOW_CONFIDENCE"));
      out.add(
          Map.of("field", id, "type", type, "normalizedValue", valid ? value : "", "valid", valid));
    }
    for (String id : required)
      if (!ids.contains(id)) issues.add(Map.of("field", id, "type", "MISSING_FIELD"));
    return Map.of(
        "fields",
        out,
        "issues",
        issues,
        "reviewRequired",
        !issues.isEmpty(),
        "ocrPerformed",
        false);
  }
}
