package com.govacode.designpatterns.structural.composite.reference;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) {
        String input = "//l 0 FwlLIST : msgTag=COMM value={\n" +
                "    class=DICT\n" +
                "    {\n" +
                "        class=ASSOC\n" +
                "        {class=A1 \"passdownAttrs\"}\n" +
                "        {\n" +
                "            class=ORDERED\n" +
                "            {\n" +
                "                class=ASSOC{class=A1 \"PARAMETER\"}{class=A1 \"VALUE\"}\n" +
                "                {\"FTPUSER\" \"mesuat\"}\n" +
                "                {\"FTPPWD\" \"mesuat123\"}\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    {\"reasonCode\" {class=A1 \"\"}}\n" +
                "    {\"ruleReply\" \"PASS\"}\n" +
                "}";

        Map<String, String> resultMap = parseString(input);
        resultMap.forEach((key, value) -> System.out.println(key + " -> " + value));
    }

    public static Map<String, String> parseString(String input) {
        Map<String, String> resultMap = new HashMap<>();
        Deque<Character> stack = new ArrayDeque<>();
        StringBuilder currentKeyValue = new StringBuilder();
        boolean insideBraces = false;

        // 移除多余的空格和 class=XXX 的部分
        input = input.replaceAll("\\s+", " ")
                .replaceAll("class=\\s+", "");

        for (char ch : input.toCharArray()) {
            if (ch == '{') {
                stack.push('{');
                insideBraces = true;
            } else if (ch == '}') {
                if (!stack.isEmpty() && stack.peek() == '{') {
                    stack.pop();
                    String segment = currentKeyValue.toString().trim();
                    if (insideBraces && !segment.isEmpty()) {
                        parseAndStore(segment, resultMap);
                    }
                    currentKeyValue.setLength(0);
                    insideBraces = false;
                }
            } else {
                if (insideBraces) {
                    currentKeyValue.append(ch);
                }
            }
        }
        return resultMap;
    }

    private static void parseAndStore(String segment, Map<String, String> resultMap) {
        if (segment.contains("\"")) { // 尝试拆分成 key-value
            // "key
            // key"
            // "key"
            // "key" "value"
            //
            String[] parts = segment.split("\"");
            if (parts.length == 2) {
                resultMap.put(parts[1].trim(), "");
            } else if (parts.length == 4) {
                resultMap.put(parts[1].trim(), parts[3].trim());
            }
        } else {
            // 只有一个字符串的情况
            resultMap.put(segment, "");
        }
    }
}
