package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsQuestionRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsQuestionService;
import com.ht.bnu_tiku_backend.mapper.*;
import com.ht.bnu_tiku_backend.model.domain.*;
import com.ht.bnu_tiku_backend.model.domain.ComplexityType;
import com.ht.bnu_tiku_backend.model.domain.CoreCompetency;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.mongodb.model.*;
import com.ht.bnu_tiku_backend.mongodb.model.Image;
import com.ht.bnu_tiku_backend.utils.ResponseResult.Result;
import com.ht.bnu_tiku_backend.utils.ResultCode;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.request.CorrectTags;
import com.ht.bnu_tiku_backend.utils.request.QuestionCorrectRequest;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;
import com.latextoword.Latex_Word;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EsQuestionServiceImpl implements EsQuestionService {
    public static final Map<String, String> nameToId;
    public static final Map<String, String> idToName;
    private static final String NS =
            "http://schemas.openxmlformats.org/officeDocument/2006/math";
    private static final String NS_W = 
            "http://schemas.openxmlformats.org/wordprocessingml/2006/main";
    static {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "KnowledgeTree/xkb_node_to_id.json";
        try (
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)
        ) {
            if (is == null) {
                throw new RuntimeException("字典文件找不到：" + path + "，请确认已放到resources目录下");
            }
            nameToId = objectMapper.readValue(is, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        idToName = nameToId.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
    @Resource
    private CoreCompetencyMapper coreCompetencyMapper;
    @Resource
    private ComplexityTypeMapper complexityTypeMapper;
    @Resource
    private SourceMapper sourceMapper;
    @Resource
    private GradeMapper gradeMapper;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private EsQuestionRepository esQuestionRepository;
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionRevisionLogMapper questionRevisionLogMapper;

    @Override
    public void saveQuestion(Question question) {
        esQuestionRepository.save(question);
    }

    @Override
    public Result<PageQueryQuestionResult> queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) {
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();

        if (knowledgePointNames.isEmpty()) {
            return Result.ok(new PageQueryQuestionResult());
        }
        Pageable pageable = PageRequest.of(Math.toIntExact(pageNumber - 1), Math.toIntExact(pageSize));
        List<Question> allQuestions = new ArrayList<>();
        if (knowledgePointNames.contains("beforeMount")) {
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .withTrackTotalHits(Boolean.TRUE)
                    .build();
            SearchHits<Question> allPages = elasticsearchTemplate.search(query, Question.class);
            allQuestions.addAll(allPages.stream().map(SearchHit::getContent).toList());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalHits());
        } else {
            List<Long> knowledgePointIdList = knowledgePointNames.stream().map(key -> {
                String knowledgePointId = nameToId.get(key);
                if (StringUtils.isEmpty(knowledgePointId)) {
                    return -1L;
                }
                return Long.valueOf(knowledgePointId);
            }).toList();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .terms(t -> t
                                    .field("knowledge_point_ids")
                                    .terms(ts -> ts.value(
                                            knowledgePointIdList.stream().map(FieldValue::of).toList()
                                    ))
                            )
                    )
                    .withPageable(pageable)
                    .withTrackTotalHits(Boolean.TRUE)
                    .build();

            SearchHits<Question> allPages = elasticsearchTemplate.search(query, Question.class);

            allQuestions.addAll(allPages.stream().map(SearchHit::getContent).toList());
//            System.out.println(allPages.getContent());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalHits());
            if (allQuestions.isEmpty()) {
                return Result.ok(new PageQueryQuestionResult());
            }
        }

        List<Map<String, String>> queryResult = questionContentAssemble(allQuestions);
        pageQueryQuestionResult.setPageSize(pageSize);
        pageQueryQuestionResult.setPageNo(pageNumber);
        pageQueryQuestionResult.setQuestions(queryResult);
        return Result.ok(pageQueryQuestionResult);
    }

    private List<Map<String, String>> questionContentAssemble(List<Question> allQuestions) {
        // 1. 首先批量查询试题标签信息
        // 1.1 查询核心素养
        long startTime = System.currentTimeMillis();
        List<Long> coreCompetencyIds = allQuestions.stream().map(Question::getCoreCompetencyId).toList();
        Map<Long, CoreCompetency> coreCompetencyMap = coreCompetencyMapper.selectBatchIds(coreCompetencyIds)
                .stream()
                .collect(Collectors.toMap(CoreCompetency::getId, c -> c));
        // 1.2 查询综合类型
        List<Long> complexityTypeIds = allQuestions.stream().map(Question::getComplexityId).toList();
        Map<Long, ComplexityType> complexityTypeMap = complexityTypeMapper.selectBatchIds(complexityTypeIds)
                .stream()
                .collect(Collectors.toMap(ComplexityType::getId, c -> c));
        // 1.3 查询来源
        List<Long> sourceIds = allQuestions.stream().map(Question::getSourceId).toList();
        Map<Long, Source> sourceMap = sourceMapper.selectBatchIds(sourceIds)
                .stream()
                .collect(Collectors.toMap(Source::getId, c -> c));
        // 1.4 查询年级
        List<Integer> gradeIds = allQuestions.stream().map(Question::getGradeId).toList();
        Map<Long, Grade> gradeMap = gradeMapper.selectBatchIds(gradeIds)
                .stream()
                .collect(Collectors.toMap(Grade::getId, g -> g));
        // 1.5 查询知识点
        Map<Long, List<Long>> knowledgePointIdsMap = allQuestions
                .stream()
                .collect(Collectors.toMap(Question::getQuestionId, question -> {
                    if (question.getParentId() == null) {
                        return question.getKnowledgePointIds();
                    }
                    return List.of();
                }));
        // 1.6 查询核心素养
        Map<Long, Long> maxKnowledgePointIdMap = knowledgePointIdsMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    if (e.getValue() == null || e.getValue().isEmpty()) {
                        return 0L;
                    }
                    return Collections.max(e.getValue());
                }));
        long endTime1 = System.currentTimeMillis();
        log.info("查询标签时间：{}ms", endTime1 - startTime);
        // 2. 组装习题文本
        List<Map<String, String>> queryResult = new ArrayList<>();
        allQuestions.forEach(question -> {
            if (question.getParentId() != null) {
                return;
            }
            HashMap<String, String> questionMap = new HashMap<>();
            insertTagsIntoResult(question,
                    false,
                    questionMap,
                    complexityTypeMap,
                    coreCompetencyMap,
                    gradeMap,
                    sourceMap,
                    maxKnowledgePointIdMap,
                    knowledgePointIdsMap);
            StemBlock stemBlock = question.getStemBlock();
            StringBuilder stemText = new StringBuilder(question.getStemBlock().getText());
            insertImageUrlIntoText(stemBlock, stemText);
            if (question.getQuestionType().equals(0)) {
                questionMap.put("stem", stemText.toString());
                insertBlockTextIntoResult(question, questionMap);
            } else {
                questionMap.put("composite_question_stem", stemText.toString());
                ArrayList<HashMap<String, String>> subQuestionMaps = new ArrayList<>();
                esQuestionRepository.findByParentId(question.getQuestionId()).forEach(subQuestion -> {
                    HashMap<String, String> subQuestionMap = new HashMap<>();
                    StemBlock subQuestionStemBlock = subQuestion.getStemBlock();
                    StringBuilder subQuestionStemText = new StringBuilder(subQuestionStemBlock.getText());
                    insertImageUrlIntoText(subQuestionStemBlock, subQuestionStemText);
                    subQuestionMap.put("stem", subQuestionStemText.toString());
                    insertTagsIntoResult(subQuestion,
                            true, subQuestionMap,
                            null,
                            null,
                            null,
                            null,
                            null,
                            knowledgePointIdsMap);
                    insertBlockTextIntoResult(subQuestion, subQuestionMap);
                    subQuestionMaps.add(subQuestionMap);
                });
                StringBuilder subQuestionString = new StringBuilder();
                try {
                    subQuestionString.append(objectMapper.writeValueAsString(subQuestionMaps));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                questionMap.put("sub_questions", subQuestionString.toString());
            }
            queryResult.add(questionMap);
        });
        long endTime2 = System.currentTimeMillis();
        log.info("组装习题内容时间：{}ms", endTime2 - endTime1);
        return queryResult;
    }

    @Override
    public File generatePdf(List<Long> ids) {
        return null;
    }

    @Override
    public PageQueryQuestionResult queryQuestionsByKeyword(String keyword, Long pageNumber, Long pageSize) throws IOException {
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();
        Pageable pageable = PageRequest.of(Math.toIntExact(pageNumber - 1), Math.toIntExact(pageSize));
        List<Question> allQuestions = new ArrayList<>();

        if (keyword.isBlank()) {
            Page<Question> allPages = esQuestionRepository.findAll(pageable);
            allQuestions.addAll(allPages.getContent());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalElements());
        } else {
            List<HighlightField> fields = List.of(
                    new HighlightField("stem_block.text")
                    , new HighlightField("explanation_block.explanation.text")
                    , new HighlightField("answer_block.text")
            );

            HighlightParameters highlightParams = new HighlightParameters.HighlightParametersBuilder()
                    .withPreTags("<em style=\"color: red\">")
                    .withPostTags("</em>")
                    .withFragmentSize(0)          // 0 = 关闭分片
                    .withNumberOfFragments(0)     // 0 = 只返回一个片段，即完整字段
                    .withType("unified")          // 推荐 unified，高亮质量更好
                    .build();

            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.multiMatch(mm -> mm
                            .query(keyword)
                            .fields("stem_block.text",
                                    "explanation_block.explanation.text",
                                    "answer_block.text")))
                    .withHighlightQuery(new HighlightQuery(
                            new Highlight(highlightParams, fields), Question.class))
                    .withPageable(pageable)
                    .build();

            SearchHits<Question> searchHits = elasticsearchTemplate.search(query, Question.class);

            allQuestions.addAll(searchHits.getSearchHits().stream()
                    .map(searchHit -> {
//                        System.out.println(searchHit.getContent().getExplanationBlock().getExplanation().getText());
//                        System.out.println(searchHit.getContent().getExplanationBlock().getExplanation().getText());
                        List<String> stemText = searchHit.getHighlightField("stem_block.text");
//                        System.out.println(searchHit.getContent().getStemBlock().getText());
                        List<String> explanationText = searchHit.getHighlightField("explanation_block.explanation.text");
                        List<String> answerText = searchHit.getHighlightField("answer_block.text");
//                        System.out.println(STR."""
//                                \{String.join("", explanationText)}
//                                """);
                        Question q = searchHit.getContent();
                        if (!stemText.isEmpty()) {
                            StemBlock stemBlock = q.getStemBlock();
                            stemBlock.setText(String.join("", stemText));
                            q.setStemBlock(stemBlock);
                        }
                        if (!explanationText.isEmpty()) {
                            Explanation explanation = q.getExplanationBlock().getExplanation();
                            explanation.setText(String.join("", explanationText));
                            ExplanationBlock explanationBlock = q.getExplanationBlock();
                            explanationBlock.setExplanation(explanation);
                            q.setExplanationBlock(explanationBlock);
                        }
                        if (!answerText.isEmpty()) {
                            AnswerBlock answerBlock = q.getAnswerBlock();
                            answerBlock.setText(String.join("", answerText));
                            q.setAnswerBlock(answerBlock);
                        }
                        return q;
                    })
                    .toList());
            //System.out.println(allQuestions.getFirst());
            pageQueryQuestionResult.setTotalCount(searchHits.getTotalHits());
        }

        if (allQuestions.isEmpty()) {
            return new PageQueryQuestionResult();
        }

        List<Map<String, String>> queryResult = questionContentAssemble(allQuestions);
        pageQueryQuestionResult.setPageSize(pageSize);
        pageQueryQuestionResult.setPageNo(pageNumber);
        pageQueryQuestionResult.setQuestions(queryResult);
        return pageQueryQuestionResult;
    }

    @Override
    public File generateDocx(List<Long> ids) {
        // 1. 查询试题列表
        List<Question> questions = esQuestionRepository.findByQuestionIdIn(ids);
        List<Map<String, String>> allQuestions = questionContentAssemble(questions);
        XWPFDocument doc = new XWPFDocument();
        int i = 1;
        for (Map<String, String> q : allQuestions) {
            String stem = q.get("stem");
            List<Object> parts = splitContent(stem);      // 富文本拆分
            XWPFParagraph para = doc.createParagraph();
            para.setAlignment(ParagraphAlignment.LEFT);
            StringBuilder ommlBuilder = new StringBuilder();
            // ① 起一个 <m:oMathPara> 容器（带 namespace）

            final boolean[] open = {false};                         // 追踪 MathPara 是否已经打开

            Consumer<Void> ensureOpen = v -> {
                if (!open[0]) {
                    open[0] = true;
                }
            };
            XWPFRun runQuestionId = para.createRun();
            runQuestionId.setText("题【" + i + "】 ");
            runQuestionId.setFontSize(12);
            runQuestionId.setFontFamily("宋体");
            i = i + 1;
            for (Object part : parts) {

                /* ---------- 普通文字 ---------- */
                if (part instanceof String txt) {
                    // flush 之前的公式段
                    if (open[0]) {
                        flushOmmlToParagraph(para, ommlBuilder);
                        ommlBuilder.setLength(0);
                        open[0] = false;
                    }
                    XWPFRun run = para.createRun();
                    run.setText(txt);
                    run.setFontFamily("宋体");
                    run.setFontSize(12);
                    continue;
                }

                /* ---------- LaTeX 公式 ---------- */
                if (part instanceof OmmlWrapper ow) {
                    ensureOpen.accept(null);               
                    String omml = StringEscapeUtils.unescapeXml(ow.getOmml())
                            .replaceAll("<m:oMathPara[^>]*>|</m:oMathPara>", "");
                    if (!omml.contains("xmlns:m"))
                        omml = omml.replaceFirst("<m:oMath",
                                "<m:oMath xmlns:m=\"" + NS + "\" xmlns:w=\"" + NS_W + "\"");
                    ommlBuilder.append(omml);
                    continue;
                }

                /* ---------- 普通图片 ---------- */
                if (part instanceof ImageWrapper iw) {
//                    flushOmmlToParagraph(para, ommlBuilder);
                    if (open[0]) {                              // 把公式段落先落地
                        flushOmmlToParagraph(para, ommlBuilder);
                        ommlBuilder.setLength(0);
                        open[0] = false;
                    }
                    // 插图逻辑保持
                    BufferedImage img = iw.getImg();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(img, "png", baos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        para.createRun().addPicture(
                                new ByteArrayInputStream(baos.toByteArray()),
                                XWPFDocument.PICTURE_TYPE_PNG,
                                "img",
                                Units.toEMU(iw.getWidth()),
                                Units.toEMU(iw.getHeight()));
                    } catch (InvalidFormatException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            /* 循环完后别忘 flush */
            if (open[0]) {
                flushOmmlToParagraph(para, ommlBuilder);
            }
            // ③ 空行分段
            doc.createParagraph();// 空行分隔
        }

        File out;
        try {
            out = File.createTempFile("export-", ".docx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream fos = new FileOutputStream(out)) {
            doc.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private static void flushOmmlToParagraph(XWPFParagraph para, StringBuilder sb) {
        System.out.println(sb.toString());
        if (sb.isEmpty()) return;

        try {
            XmlObject mathPara = XmlObject.Factory.parse(sb.toString());
            para.getCTP().addNewOMathPara().set(mathPara);
        } catch (Exception e) {
            throw new RuntimeException("OMML 解析失败", e);
        }
    }

    public List<Object> splitContent(String htmlLike) {
        List<Object> list = new ArrayList<>();
        Pattern p = Pattern.compile(
                "(\\$\\$[\\s\\S]*?\\$\\$)"                    // Latex 公式
                        + "|(<img[^>]*?src=[\"']([^\"']+)[\"'][^>]*?>)", // <img … src="URL" …>
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlLike);
        ;
        int last = 0;
        while (m.find()) {
            if (last < m.start()) list.add(htmlLike.substring(last, m.start())); // 文本

            if (m.group(1) != null) {                 // LaTeX 公式
                String latex = m.group(1).replace("$$", "$");
                String cleanLatex = latex.replaceAll("[<>]", "&gt;");
                String omml = Latex_Word.latexToWord(cleanLatex);   // ← 关键替换
                list.add(new OmmlWrapper(omml));
            } else if (m.group(2) != null) {          // 普通图片
                BufferedImage img = null;
                String imgUrl = m.group(3);
                String imgTag = m.group(2);// src
                Pattern w = Pattern.compile(
                        "width\\s*=\\s*[\"']\\s*(\\d+)\\s*(?:px)?\\s*[\"']",
                        Pattern.CASE_INSENSITIVE);

                Pattern h = Pattern.compile(
                        "height\\s*=\\s*[\"']\\s*(\\d+)\\s*(?:px)?\\s*[\"']",
                        Pattern.CASE_INSENSITIVE);
                // 提取显式宽高（像素）
                Matcher mw = w.matcher(imgTag);
                Matcher mh = h.matcher(imgTag);
                Integer wPx = mw.find() ? Integer.valueOf(mw.group(1)) : null;
                Integer hPx = mh.find() ? Integer.valueOf(mh.group(1)) : null;

                try {
                    if (imgUrl.endsWith(".svg")) {
                        img = convertSvgToPng(imgUrl);
                    } else {
                        img = ImageIO.read(new URL(imgUrl));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if ((wPx == null) && (hPx != null)) {
                    wPx = hPx;
                } else if ((wPx != null) && (hPx == null)) {
                    hPx = wPx;
                } else if (wPx == null) {
                    wPx = img.getWidth();
                    hPx = img.getHeight();
                }

                ImageWrapper imageWrapper = new ImageWrapper(img, wPx, hPx);
                list.add(imageWrapper);
            }
            last = m.end();
        }
        if (last < htmlLike.length()) list.add(htmlLike.substring(last));
        return list;
    }

    public static BufferedImage convertSvgToPng(String svgUrl) throws Exception {
        URL url = new URL(svgUrl);
        InputStream inputStream = url.openStream();
        TranscoderInput input = new TranscoderInput(inputStream);
        //  PNG 转化对象
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);
        // svg转png
        transcoder.transcode(input, output);
        // png字节数据
        byte[] pngBytes = outputStream.toByteArray();
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(pngBytes));
        outputStream.close();
        inputStream.close();
        return image;
    }

    @Override
    public Result<PageQueryQuestionResult> searchQuestionByCombination(QuestionSearchRequest questionSearchRequest) {
        long startTime = System.currentTimeMillis();
        Pageable pageable = PageRequest.of(
                questionSearchRequest.getPageNumber().intValue() - 1,
                questionSearchRequest.getPageSize().intValue());
        // === 高亮公共设置（如已有） ===
        HighlightParameters hp = new HighlightParameters
                .HighlightParametersBuilder()
                .withPreTags("<em style='color:red'>")
                .withPostTags("</em>")
                .withFragmentSize(0)
                .withNumberOfFragments(0)
                .build();
        List<HighlightField> hlFields = List.of(
                new HighlightField("stem_block.text"),
                new HighlightField("explanation_block.explanation.text"),
                new HighlightField("answer_block.text")
        );
        // === 1. 构造 bool 查询 ===
        NativeQueryBuilder builder = NativeQuery.builder();
        builder.withQuery(q -> q.bool(bool -> {
            // 关键词（multiMatch）—— 可为空
            if (StringUtils.isNotBlank(questionSearchRequest.getKeyword())) {
                bool.must(m -> m.multiMatch(mm -> mm
                        .query(questionSearchRequest.getKeyword())
                        .fields(
                                "stem_block.text",
                                "explanation_block.explanation.text",
                                "answer_block.text")));
            } else {
                bool.filter(f -> f.matchAll(m -> m));
            }
            // 知识点 id —— 可为空
            if (StringUtils.isNotBlank(questionSearchRequest.getKnowledgePointName())
                    && !StringUtils.equals(questionSearchRequest.getKnowledgePointName(), "beforeMount")) {
                bool.filter(f -> f.term(t -> t
                        .field("knowledge_point_ids")
                        .value(nameToId.get(questionSearchRequest.getKnowledgePointName())))
                );
            }
            // 年级 —— 必填
            if (questionSearchRequest.getGradeId() != -1) {
                bool.filter(f -> f.term(t -> t
                        .field("grade_id")
                        .value(questionSearchRequest.getGradeId())));
            }
            // 题型 —— 必填
            if (questionSearchRequest.getSimpleQuestionType() != -1) {
                bool.filter(f -> f.term(t -> t
                        .field("simple_question_type")
                        .value(questionSearchRequest.getSimpleQuestionType())));
            }
            /* 难度区间（double 0-1） */
            bool.filter(f -> f.range(buildDifficultyRange(questionSearchRequest.getDifficulty())));

            return bool;
        }));

        /* 分页 & 高亮（如需） */
        builder.withPageable(pageable)
                .withTrackTotalHits(true);

        if (StringUtils.isNotBlank(questionSearchRequest.getKeyword())) {
            builder.withHighlightQuery(new HighlightQuery(
                    new Highlight(hp, hlFields), Question.class));
        }

        NativeQuery query = builder.build();
        List<Question> allQuestions = new ArrayList<>();
        SearchHits<Question> allPages = elasticsearchTemplate.search(query, Question.class);
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();
        long endTime1 = System.currentTimeMillis();
        log.info("es组合查询时间：{}ms", endTime1 - startTime);
        if (StringUtils.isNotBlank(questionSearchRequest.getKeyword())) {
            allQuestions.addAll(allPages.getSearchHits().stream()
                    .map(searchHit -> {
                        List<String> stemText = searchHit.getHighlightField("stem_block.text");
                        List<String> explanationText = searchHit.getHighlightField("explanation_block.explanation.text");
                        List<String> answerText = searchHit.getHighlightField("answer_block.text");
                        Question q = searchHit.getContent();
                        if (!stemText.isEmpty()) {
                            StemBlock stemBlock = q.getStemBlock();
                            stemBlock.setText(String.join("", stemText));
                            q.setStemBlock(stemBlock);
                        }
                        if (!explanationText.isEmpty()) {
                            Explanation explanation = q.getExplanationBlock().getExplanation();
                            explanation.setText(String.join("", explanationText));
                            ExplanationBlock explanationBlock = q.getExplanationBlock();
                            explanationBlock.setExplanation(explanation);
                            q.setExplanationBlock(explanationBlock);
                        }
                        if (!answerText.isEmpty()) {
                            AnswerBlock answerBlock = q.getAnswerBlock();
                            answerBlock.setText(String.join("", answerText));
                            q.setAnswerBlock(answerBlock);
                        }
                        return q;
                    })
                    .toList());
        } else {
            allQuestions.addAll(allPages.stream().map(SearchHit::getContent).toList());
        }
        if (allQuestions.isEmpty()) {
            return Result.ok(new PageQueryQuestionResult());
        }
        List<Map<String, String>> queryResult = questionContentAssemble(allQuestions);
        pageQueryQuestionResult.setQuestions(queryResult);
        pageQueryQuestionResult.setTotalCount(allPages.getTotalHits());
        long endTime2 = System.currentTimeMillis();
        log.info("组装题目时间花销：{}ms", endTime2 - endTime1);
        return Result.ok(pageQueryQuestionResult);
    }

    @Override
    public Result<String> questionCorrect(QuestionCorrectRequest questionCorrectRequest) {
        Question question = esQuestionRepository.findByQuestionId(questionCorrectRequest.getQuestionId());
        CorrectTags correctTags = questionCorrectRequest.getCorrectTags();
        ObjectMapper objectMapper = new ObjectMapper();
        QuestionRevisionLog questionRevisionLog = new QuestionRevisionLog();
        questionRevisionLog.setQuestionId(question.getQuestionId());
        if (StringUtils.equals(questionCorrectRequest.getCorrectType(), "tags")) {
            questionRevisionLog.setModifiedField(0);
            try {
                questionRevisionLog.setOldValue(objectMapper.writeValueAsString(question));
                questionRevisionLog.setNewValue(objectMapper.writeValueAsString(correctTags));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            questionRevisionLog.setModifiedBy(Long.valueOf(questionCorrectRequest.getUserId()));
            questionRevisionLogMapper.insert(questionRevisionLog);
        } else {
            if (questionCorrectRequest.getCorrectType().equals("stem")) {
                questionRevisionLog.setModifiedField(1);
                questionRevisionLog.setOldValue(question.getStemBlock().getText());
            }
            if (questionCorrectRequest.getCorrectType().equals("explanation")) {
                questionRevisionLog.setModifiedField(2);
                questionRevisionLog.setOldValue(question.getExplanationBlock().getExplanation().getText());
            }
            if (questionCorrectRequest.getCorrectType().equals("answer")) {
                questionRevisionLog.setModifiedField(3);
                questionRevisionLog.setOldValue(question.getAnswerBlock().getText());
            }
            questionRevisionLog.setNewValue(questionCorrectRequest.getCorrection());
            questionRevisionLog.setModifiedBy(Long.valueOf(questionCorrectRequest.getUserId()));
            questionRevisionLogMapper.insert(questionRevisionLog);
        }
        return Result.ok("1");
    }

    private RangeQuery buildDifficultyRange(String diffKey) {
        return RangeQuery.of(rq -> rq.number(n -> {
            n.field("difficulty");
            return switch (diffKey) {
                case "difficult" -> n.from(0.0).to(0.45);
                case "relatively-difficult" -> n.from(0.45).to(0.6);
                case "medium" -> n.from(0.6).to(0.8);
                case "simple" -> n.from(0.8).to(0.9);
                case "easy" -> n.from(0.9).to(1.0);
                case "all" -> n.from(0.0).to(1.0);
                default -> throw new IllegalArgumentException("unknown difficulty enum");
            };
        }));
    }

    @Data
    public static class OmmlWrapper {        // 小包装，便于类型区分
        private final String omml;

        OmmlWrapper(String omml) {
            this.omml = omml;
        }
    }

    @Data
    public static class ImageWrapper {        // 小包装，便于类型区分
        private final BufferedImage img;
        private final Integer width;
        private final Integer height;

        public ImageWrapper(BufferedImage img, Integer width, Integer height) {
            this.img = img;
            this.width = width;
            this.height = height;
        }
    }

    public static void appendOmmlToParagraph(XWPFParagraph para, String omml) throws Exception {
        // 1. 解析 OMML 字符串
        String ommlXml = Latex_Word.latexToWord(omml);
        if (!ommlXml.contains("xmlns:m=")) {
            ommlXml = ommlXml.replaceFirst(
                    "<m:oMath(.*?)>",
                    "<m:oMath$1 xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\">"
            );
        }
        XmlObject xmlObj = XmlObject.Factory.parse(ommlXml);
        // ↓ 这里 cast 成 CTOMath
        para.getCTP().addNewOMath().set(xmlObj);
    }

    public void insertTagsIntoResult(Question question,
                                              boolean isSubQuestion,
                                              HashMap<String, String> questionMap,
                                              Map<Long, ComplexityType> complexityTypeMap,
                                              Map<Long, CoreCompetency> coreCompetencyMap,
                                              Map<Long, Grade> gradeMap, Map<Long, Source> sourceMap,
                                              Map<Long, Long> maxKnowledgePointIdMap,
                                              Map<Long, List<Long>> knowledgePointIdsMap) {
        if (!isSubQuestion) {
            questionMap.put("complexity_type", complexityTypeMap.get(question.getComplexityId()).getTypeName());
            questionMap.put("core_competency", coreCompetencyMap.get(question.getCoreCompetencyId()).getCompetencyName());
            questionMap.put("difficulty", question.getDifficulty().toString());
            questionMap.put("grade", gradeMap.get(Long.valueOf(question.getGradeId())).getName());
            questionMap.put("question_source", sourceMap.get(question.getSourceId()).getName());
            questionMap.put("knowledge_point", idToName.get(
                    maxKnowledgePointIdMap
                            .get(question.getQuestionId()).toString()
            ));

            Set<String> knowledgePointNames = knowledgePointIdsMap.get(question.getQuestionId())
                    .stream()
                    .map(knowledgePointId -> idToName.get(knowledgePointId.toString()))
                    .collect(Collectors.toSet());

            try {
                questionMap.put("knowledge_points", objectMapper.writeValueAsString(knowledgePointNames));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            StringBuilder knowledgePointIds = new StringBuilder();
            try {
                knowledgePointIds.append(objectMapper.writeValueAsString(question.getKnowledgePointIds()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            questionMap.put("knowledge_point_list", knowledgePointIds.toString());
            questionMap.put("question_type", String.valueOf(question.getQuestionType()));
        }

        questionMap.put("question_id", String.valueOf(question.getQuestionId()));
        questionMap.put("simple_question_type", String.valueOf(question.getSimpleQuestionType()));
    }

    public void insertBlockTextIntoResult(Question question, HashMap<String, String> questionMap) {
        AnswerBlock answerBlock = question.getAnswerBlock();
        StringBuilder answerText = new StringBuilder();
        if (answerBlock != null) {
            answerText.append(answerBlock.getText());
            insertImageUrlIntoText(answerBlock, answerText);
        }
        questionMap.put("question_answer", answerText.toString());

        Explanation explanation = question.getExplanationBlock().getExplanation();
        StringBuilder explanationText = new StringBuilder();
        if (explanation != null) {
            explanationText.append(explanation.getText());
            insertImageUrlIntoText(explanation, explanationText);
        }

        Analysis analysis = question.getExplanationBlock().getAnalysis();
        StringBuilder analysisText = new StringBuilder();
        if (analysis != null) {
            analysisText.append(analysis.getText());
            insertImageUrlIntoText(analysis, analysisText);
        }

        FinishingTouch finishingTouch = question.getExplanationBlock().getFinishingTouch();
        StringBuilder finishingTouchText = new StringBuilder();
        if (finishingTouch != null) {
            finishingTouchText.append(finishingTouch.getText());
            insertImageUrlIntoText(finishingTouch, finishingTouchText);
        }

        questionMap.put("question_explanation", explanationText
                .append(analysisText)
                .append(finishingTouchText).toString()
        );
    }

    public void insertImageUrlIntoText(QuestionBlock block, StringBuilder text) {
        if (text.isEmpty()) {
            return;
        }
        List<Image> images = block.getImages();
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                text.insert(Math.toIntExact(image.getPosition()), image.getUrl());
            });
        }
    }
}
