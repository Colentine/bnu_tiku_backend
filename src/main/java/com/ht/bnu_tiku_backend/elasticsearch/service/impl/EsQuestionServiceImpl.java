package com.ht.bnu_tiku_backend.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.bnu_tiku_backend.elasticsearch.model.Question;
import com.ht.bnu_tiku_backend.elasticsearch.repository.EsQuestionRepository;
import com.ht.bnu_tiku_backend.elasticsearch.service.EsQuestionService;
import com.ht.bnu_tiku_backend.mapper.ComplexityTypeMapper;
import com.ht.bnu_tiku_backend.mapper.CoreCompetencyMapper;
import com.ht.bnu_tiku_backend.mapper.GradeMapper;
import com.ht.bnu_tiku_backend.mapper.SourceMapper;
import com.ht.bnu_tiku_backend.model.domain.ComplexityType;
import com.ht.bnu_tiku_backend.model.domain.CoreCompetency;
import com.ht.bnu_tiku_backend.model.domain.Grade;
import com.ht.bnu_tiku_backend.model.domain.Source;
import com.ht.bnu_tiku_backend.mongodb.model.*;
import com.ht.bnu_tiku_backend.mongodb.model.Image;
import com.ht.bnu_tiku_backend.mongodb.repository.QuestionRepository;
import com.ht.bnu_tiku_backend.utils.page.PageQueryQuestionResult;
import com.ht.bnu_tiku_backend.utils.request.QuestionSearchRequest;
import com.latextoword.Latex_Word;
import jakarta.annotation.Resource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlObject;
import org.docx4j.XmlUtils;
import org.docx4j.math.CTOMathPara;
import org.docx4j.math.ObjectFactory;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.ht.bnu_tiku_backend.mongodb.service.impl.MongoMongoQuestionServiceImpl.*;

@Service
public class EsQuestionServiceImpl implements EsQuestionService {
    public static final Map<String, String> nameToId;
    public static final Map<String, String> idToName;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "KnowledgeTree/xkb_node_to_id.json";
        try (
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)
        ) {
            if (is == null) {
                throw new RuntimeException("字典文件找不到：" + path + "，请确认已放到resources目录下");
            }
            nameToId = objectMapper.readValue(is, new TypeReference<Map<String, String>>() {});
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
    private String htmlLike;
    private RangeQuery.Builder range;

    @Override
    public void saveQuestion(Question question) {
        esQuestionRepository.save(question);
    }

    @Override
    public PageQueryQuestionResult queryQuestionsByKnowledgePointNames(List<String> knowledgePointNames, Long pageNumber, Long pageSize) {
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();

        if(knowledgePointNames.isEmpty()){
            return new PageQueryQuestionResult();
        }
        Pageable pageable = PageRequest.of(Math.toIntExact(pageNumber-1), Math.toIntExact(pageSize));
        List<Question> allQuestions = new ArrayList<>();
        if(knowledgePointNames.contains("beforeMount")){
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q.matchAll(m -> m))
                    .withPageable(pageable)
                    .withTrackTotalHits(Boolean.TRUE)
                    .build();
            SearchHits<Question> allPages = elasticsearchTemplate.search(query, Question.class);
            allQuestions.addAll(allPages.stream().map(SearchHit::getContent).toList());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalHits());
        }else {
            List<Long> knowledgePointIdList = knowledgePointNames.stream().map(key -> {
                String knowledgePointId = nameToId.get(key);
                if (StringUtils.isEmpty(knowledgePointId)) {
                    return -1L;
                }
                return Long.valueOf(knowledgePointId);
            }).toList();

//            System.out.println(knowledgePointIdlist);
            //List<Question> byKnowledgePointIdsIn = questionRepository.findByKnowledgePointIdsIn(knowledgePointIdlist);
            //System.out.println(byKnowledgePointIdsIn);

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
                return new PageQueryQuestionResult();
            }
        }

        List<Map<String, String>> queryResult = queryQuestionsByIds(allQuestions);
        pageQueryQuestionResult.setPageSize(pageSize);
        pageQueryQuestionResult.setPageNo(pageNumber);
        pageQueryQuestionResult.setQuestions(queryResult);
        return pageQueryQuestionResult;
    }

    private List<Map<String, String>> queryQuestionsByIds(List<Question> allQuestions) {
        List<Long> coreCompetencyIds = allQuestions.stream().map(Question::getCoreCompetencyId).toList();
        Map<Long, CoreCompetency> coreCompetencyMap = coreCompetencyMapper.selectBatchIds(coreCompetencyIds)
                .stream()
                .collect(Collectors.toMap(CoreCompetency::getId, c -> c));

        List<Long> complexityTypeIds = allQuestions.stream().map(Question::getComplexityId).toList();
        Map<Long, ComplexityType> complexityTypeMap = complexityTypeMapper.selectBatchIds(complexityTypeIds)
                .stream()
                .collect(Collectors.toMap(ComplexityType::getId, c -> c));

        List<Long> sourceIds = allQuestions.stream().map(Question::getSourceId).toList();
        Map<Long, Source> sourceMap = sourceMapper.selectBatchIds(sourceIds)
                .stream()
                .collect(Collectors.toMap(Source::getId, c -> c));
//        System.out.println(sourceMap);
//        System.out.println(sourceIds);
        List<Integer> gradeIds = allQuestions.stream().map(Question::getGradeId).toList();
        Map<Long, Grade> gradeMap = gradeMapper.selectBatchIds(gradeIds)
                .stream()
                .collect(Collectors.toMap(Grade::getId, g -> g));

        Map<Long, List<Long>> knowledgePointIdsMap = allQuestions
                .stream()
                .collect(Collectors.toMap(Question::getQuestionId, question -> {
                    if(question.getParentId() == null){
                        return question.getKnowledgePointIds();
                    }
                    return List.of();
                }));
        Map<Long, Long> maxKnowledgePointIdMap = knowledgePointIdsMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e ->{
                    if(e.getValue() == null || e.getValue().isEmpty()){
                        return 0L;
                    }
                    return Collections.max(e.getValue());
                }));

        List<Map<String, String>> queryResult = new ArrayList<>();
        allQuestions.forEach(question -> {
            if(question.getParentId() != null){
                return;
            }
            HashMap<String, String> questionMap = new HashMap<>();
            StringBuilder stemText = insertTagsIntoResult(question,
                    false,
                    questionMap,
                    complexityTypeMap,
                    coreCompetencyMap,
                    gradeMap,
                    sourceMap,
                    maxKnowledgePointIdMap,
                    knowledgePointIdsMap);
            if(question.getQuestionType().equals(0)) {
                questionMap.put("stem", stemText.toString());
                insertBlockTextIntoResult(question, questionMap);
            }else{
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
        return queryResult;
    }

    @Override
    public PageQueryQuestionResult queryQuestionsByKeyword(String keyword, Long pageNumber, Long pageSize) throws IOException {
        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();

        Pageable pageable = PageRequest.of(Math.toIntExact(pageNumber-1), Math.toIntExact(pageSize));
        List<Question> allQuestions = new ArrayList<>();
        if(keyword.isBlank()){
            Page<Question> allPages = esQuestionRepository.findAll(pageable);
            allQuestions.addAll(allPages.getContent());
            pageQueryQuestionResult.setTotalCount(allPages.getTotalElements());
        }else {
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
                        System.out.println(STR."""
                                \{String.join("", explanationText)}
                                """);
                        Question q = searchHit.getContent();
                        if(!stemText.isEmpty()) {
                            StemBlock stemBlock = q.getStemBlock();
                            stemBlock.setText(String.join("", stemText));
                            q.setStemBlock(stemBlock);
                        }
                        if(!explanationText.isEmpty()) {
                            Explanation explanation = q.getExplanationBlock().getExplanation();
                            explanation.setText(String.join("", explanationText));
                            ExplanationBlock explanationBlock = q.getExplanationBlock();
                            explanationBlock.setExplanation(explanation);
                            q.setExplanationBlock(explanationBlock);
                        }

                        if(!answerText.isEmpty()) {
                            AnswerBlock answerBlock = q.getAnswerBlock();
                            answerBlock.setText(String.join("", answerText));
                            q.setAnswerBlock(answerBlock);
                        }
                        return q;
                    })
                    .toList());
            //System.out.println(allQuestions.getFirst());
            pageQueryQuestionResult.setTotalCount(searchHits.getTotalHits());
            if (allQuestions.isEmpty()) {
                return new PageQueryQuestionResult();
            }
        }

        if(allQuestions.isEmpty()){
            return new PageQueryQuestionResult();
        }

        List<Map<String, String>> queryResult = queryQuestionsByIds(allQuestions);
        pageQueryQuestionResult.setPageSize(pageSize);
        pageQueryQuestionResult.setPageNo(pageNumber);
        pageQueryQuestionResult.setQuestions(queryResult);
        return pageQueryQuestionResult;
    }

    @Override
    public File generateDocx(List<Long> ids) {
        // 1. 查询试题列表
        List<Question> questions = esQuestionRepository.findByQuestionIdIn(ids);
        List<Map<String, String>> allQuestions = queryQuestionsByIds(questions);
        XWPFDocument doc = new XWPFDocument();

        for (Map<String, String> q : allQuestions) {
            String stem = q.get("stem");          // 示例：含 $$...$$ 的题干
            List<Object> parts = null;  // 见下方改版 splitContent
            try {
                parts = splitContent(stem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            XWPFParagraph para = doc.createParagraph();

            for (Object part : parts) {
                if (part instanceof String) {           // 纯文本
                    XWPFRun run = para.createRun();
                    run.setText((String) part);
                } else if (part instanceof BufferedImage) { // 普通图片
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        ImageIO.write((BufferedImage) part, "png", baos);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    XWPFRun run = para.createRun();
                    try {
                        run.addPicture(new ByteArrayInputStream(baos.toByteArray()),
                                XWPFDocument.PICTURE_TYPE_PNG, "img", Units.toEMU(100), Units.toEMU(40));
                    } catch (InvalidFormatException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (part instanceof OmmlWrapper) {
                    // OMML 公式
                    try {
                        appendOmmlToParagraph(para, ((OmmlWrapper) part).getOmml());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            doc.createParagraph(); // 空行分隔
        }

        File out = null;
        try {
            out = File.createTempFile("export-", ".docx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream fos = new FileOutputStream(out)) {
            doc.write(fos);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public File generatePdf(List<Long> ids) {
        return null;
    }

    @Override
    public PageQueryQuestionResult searchQuestionByCombination(QuestionSearchRequest questionSearchRequest) {
        Pageable pageable = PageRequest.of(
                questionSearchRequest.getPageNumber().intValue(),
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
            }else{
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
            if(questionSearchRequest.getGradeId() != -1) {
                bool.filter(f -> f.term(t -> t
                        .field("grade_id")
                        .value(questionSearchRequest.getGradeId())));
            }
            // 题型 —— 必填

            if(questionSearchRequest.getSimpleQuestionType() != -1) {
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

        if(StringUtils.isNotBlank(questionSearchRequest.getKeyword())) {
            builder.withHighlightQuery(new HighlightQuery(
                    new Highlight(hp, hlFields), Question.class));
        }

        NativeQuery query =  builder.build();

        List<Question> allQuestions = new ArrayList<>();

        SearchHits<Question> allPages = elasticsearchTemplate.search(query, Question.class);

        PageQueryQuestionResult pageQueryQuestionResult = new PageQueryQuestionResult();


        allQuestions.addAll(allPages.stream().map(SearchHit::getContent).toList());

        System.out.println(allQuestions.size());

        if(allQuestions.isEmpty()){
            return new PageQueryQuestionResult();
        }

        List<Map<String, String>> queryResult = queryQuestionsByIds(allQuestions);

        pageQueryQuestionResult.setQuestions(queryResult);

        pageQueryQuestionResult.setTotalCount(allPages.getTotalHits());

        System.out.println(pageQueryQuestionResult);

        return pageQueryQuestionResult;
    }

    private RangeQuery buildDifficultyRange(String diffKey) {
        return RangeQuery.of(rq -> rq.number(n -> {
            n.field("difficulty");
            return switch (diffKey) {
                case "difficult"   -> n.from(0.0).to(0.45);
                case "relatively-difficult" -> n.from(0.45).to(0.6);
                case "medium" -> n.from(0.6).to(0.8);
                case "simple"   -> n.from(0.8).to(0.9);
                case "easy"   -> n.from(0.9).to(1.0);
                case "all"    -> n.from(0.0).to(1.0);
                default       -> throw new IllegalArgumentException("unknown difficulty enum");
            };
        }));
    }

    public static class OmmlWrapper {        // 小包装，便于类型区分
        private final String omml;
        OmmlWrapper(String omml) { this.omml = omml; }
        String getOmml() { return omml; }
    }

    public List<Object> splitContent(String htmlLike) {
        this.htmlLike = htmlLike;
        List<Object> list = new ArrayList<>();
        Pattern p = Pattern.compile("(\\$\\$[\\s\\S]*?\\$\\$)|(<img.*?src=[\"'](.*?)[\"'].*?>)");
        Matcher m = p.matcher(htmlLike);
        int last = 0;
        while (m.find()) {
            if (last < m.start()) list.add(htmlLike.substring(last, m.start())); // 文本

            if (m.group(1) != null) {                 // LaTeX 公式
                String latex = m.group(1).replace("$$", "$");
                String omml = Latex_Word.latexToWord(latex);   // ← 关键替换
                list.add(new OmmlWrapper(omml));
            } else if (m.group(2) != null) {          // 普通图片
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new URL(m.group(3)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                list.add(img);
            }
            last = m.end();
        }
        if (last < htmlLike.length()) list.add(htmlLike.substring(last));
        return list;
    }

    public static void appendOmmlToParagraph(XWPFParagraph para, String omml) throws Exception {
        // 1. 解析 OMML 字符串

        String ommlXml = Latex_Word.latexToWord(omml);
        XmlObject xmlObj = XmlObject.Factory.parse(ommlXml);
        // 2. 将其导入到段落底层的 CTP
        para.getCTP().addNewOMathPara().set(xmlObj);
    }

    public StringBuilder insertTagsIntoResult(Question question,
                                               boolean isSubQuestion,
                                               HashMap<String, String> questionMap,
                                               Map<Long, ComplexityType> complexityTypeMap,
                                               Map<Long, CoreCompetency> coreCompetencyMap,
                                               Map<Long, Grade> gradeMap, Map<Long, Source> sourceMap,
                                               Map<Long, Long> maxKnowledgePointIdMap,
                                               Map<Long, List<Long>> knowledgePointIdsMap ) {
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
        StemBlock stemBlock = question.getStemBlock();
        StringBuilder stemText = new StringBuilder(question.getStemBlock().getText());
        insertImageUrlIntoText(stemBlock, stemText);
        return stemText;
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
        if(analysis != null) {
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
        if(text.isEmpty()){
            return;
        }
        List<Image> images = block.getImages();
        if(images != null && !images.isEmpty()){
            images.forEach(image -> {
                text.insert(Math.toIntExact(image.getPosition()), image.getUrl());
            });
        }
    }
}
