package com.ht.bnu_tiku_backend.rag.document.readers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;

import java.util.*;
import java.util.regex.Pattern;

public class QuestionDocumentReader {
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern DOLLAR_PATTERN = Pattern.compile("$+");

    private static String cleanText(String text) {
        return HTML_TAG_PATTERN.matcher(text)
                .replaceAll("")
                .replaceAll("\\s+", " ")
                .replaceAll("\\$+", " ").trim();
    }

    public static Document fromQuestion(Map<String, String> question) {
        Document doc = null;

        // 简单题
        if (Objects.equals(question.get("question_type"), "0")) {
            doc = buildSimpleQuestionDocument(question);
        }
        // 复合题
        if (Objects.equals(question.get("question_type"), "1")) {
            doc = buildCompositeQuestionDocument(question);
        }
        return doc;
    }

    public static Document buildSimpleQuestionDocument(Map<String, String> question) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> metadata = generateQuestionMetaInfo(question, objectMapper);
        String questionText = "【题干】 " + question.get("stem") + "\n" +
                "【答案】 " + question.get("question_answer") + "\n" +
                "【解析】 " + question.get("question_explanation") + "\n";
        return new Document(cleanText(questionText), metadata);
    }

    public static Document buildCompositeQuestionDocument(Map<String, String> question) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> metadata = generateQuestionMetaInfo(question, objectMapper);
        StringBuilder questionText = new StringBuilder("【题干】 " + question.get("composite_question_stem") + "\n");
        List<Map<String, String>> subQuestionMaps;
        StringBuilder subQuestionStemText = new StringBuilder();
        StringBuilder subQuestionAnswerText = new StringBuilder();
        StringBuilder subQuestionExplanationText = new StringBuilder();
        try {
            subQuestionMaps = new ArrayList<>(objectMapper.readValue(question.get("sub_questions")
                    , new TypeReference<List<Map<String, String>>>() {
                    }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (subQuestionMaps.isEmpty()) {
            new Document(String.valueOf(questionText), metadata);
        }

        for (int i = 0; i < subQuestionMaps.size(); i++) {
            subQuestionStemText
                    .append("(").append(i).append(")")
                    .append(subQuestionMaps.get(i).get("stem"));
            subQuestionAnswerText
                    .append("(").append(i).append(")")
                    .append(subQuestionMaps.get(i).get("question_answer"));
            subQuestionExplanationText
                    .append("(").append(i).append(")")
                    .append(subQuestionMaps.get(i).get("question_explanation"));
        }
        questionText.append(subQuestionStemText)
                .append("\n")
                .append("【答案】")
                .append(subQuestionAnswerText).append("\n")
                .append("【解析】")
                .append(subQuestionExplanationText)
                .append("\n");
        return new Document(cleanText(String.valueOf(questionText)), metadata);
    }

    private static Map<String, Object> generateQuestionMetaInfo(Map<String, String> question, ObjectMapper objectMapper) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("question_type", transformQuestionTypeToTag(question.get("simple_question_type")));
        metadata.put("difficulty", transformDifficultyToTag(question.get("difficulty")));
        try {
            metadata.put("knowledge_points", objectMapper.readValue(question.get("knowledge_points")
                    , new TypeReference<List<String>>() {
                    }));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        metadata.put("question_source", question.get("question_source"));
        metadata.put("complexity_type", question.get("complexity_type"));
        metadata.put("core_competency", question.get("core_competency"));
        metadata.put("grade", question.get("grade"));
        return metadata;
    }

    public static String transformDifficultyToTag(String difficulty) {
        try {
            double diff = Double.parseDouble(difficulty);
            if (diff > 1) {
                throw new RuntimeException("abnormal difficulty level");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("abnormal difficulty");
        }

        if (Double.parseDouble(difficulty) < 0.7) {
            return "Slightly difficult";
        } else if (Double.parseDouble(difficulty) < 0.8) {
            return "normal";
        } else if (Double.parseDouble(difficulty) < 0.9) {
            return "simple";
        } else if (Double.parseDouble(difficulty) < 0.95) {
            return "easy";
        }
        return "so easy";
    }

    public static String transformQuestionTypeToTag(String questionType) {
        try {
            if (!(questionType.equals("0") || questionType.equals("1")
                    || questionType.equals("2")
                    || questionType.equals("3") || questionType.equals("4"))) {
                throw new RuntimeException("abnormal question type");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("abnormal question type");
        }

        return switch (questionType) {
            case "0" -> "单选题";
            case "1" -> "多选题";
            case "2" -> "填空题";
            case "3" -> "简单题";
            default -> "判断题";
        };
    }
}
