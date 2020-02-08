package com.pl.indexserver.untils;

import com.pl.indexserver.model.SpeechcraftTagDto;
import org.springframework.util.StringUtils;

import java.util.List;

public class TagUtils {

    public static boolean isLabel(String str) throws Exception {
        String zz = "^\\{\\{[a-zA-Z0-9_]*}}$";
        return str.matches(zz);
    }

    public static String getLabel(String str, List<SpeechcraftTagDto> speechcraftTags) throws Exception {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        for (SpeechcraftTagDto speechcraftTag : speechcraftTags) {
            if (str.matches("^\\{\\{" + speechcraftTag.getTagKey() + "}}$")) {
                return speechcraftTag.getTagName() + "(插入数据)";
            }
        }
        return str;
    }

    public static String disposeContent(String str, List<SpeechcraftTagDto> speechcraftTags) throws Exception {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        str = str.replace("&", "");
        for (SpeechcraftTagDto speechcraftTag : speechcraftTags) {
            str = str.replace("{{" + speechcraftTag.getTagKey() + "}}", "{{" + speechcraftTag.getTagName() + "}}");
        }
        return str;
    }

    public static String disposeResponseContent(String str, List<SpeechcraftTagDto> speechcraftTags) throws Exception {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        for (SpeechcraftTagDto speechcraftTag : speechcraftTags) {
            str = str.replace("{{" + speechcraftTag.getTagKey() + "}}", "{{" + speechcraftTag.getTagName() + "}}");
        }
        return str;
    }

}
