-- ==========================================
-- 题库系统数据库表结构，建表语句（DDL）
-- 不使用外键，所有关联由应用逻辑维护
-- ==========================================

-- ==========================================
-- 插入用户数据sql
-- ==========================================
INSERT INTO user (user_account, user_name, user_real_name, user_password, role, school_id, email)
values ('00003', 'asdwad1', '张小花', '1235123', '0', 2, 'xxxxx@qq.com'),
       ('00004', 'asdwad2', '张大花', 'qwdadw3', '1', 1, 'xxxxx@qq.com'),
       ('00005', 'asdwad3', '王明', '123awda5123', '1', 3, 'xxxxx@qq.com'),
       ('00006', 'asdwad4', '李明', '12351aswae23', '1', 4, 'xxxxx@qq.com'),
       ('00007', 'asdwad5', 'xxx', '123qwd5123', '1', 5, 'xxxxx@qq.com'),
       ('00008', 'asdwad6', 'zzzz', '1qwdq235123', '1', 5, 'xxxxx@qq.com'),
       ('00009', 'asdwad7', 'xxx', '12qwedq35123', '1', 3, 'xxxxx@qq.com');

-- ==========================================
-- 插入学校数据sql
-- ==========================================
INSERT INTO school (school_name) VALUES ('北京师范大学附属实验中学'),
                                        ('中国人民大学附属中学'),
                                        ('北京市第四中学'),
                                        ('北京市第八中学'),
                                        ('北京市第十一中学');

-- ==========================================
-- 插入来源数据sql
-- ==========================================
INSERT INTO source (name, description) VALUES
                                           ('学科网', '来自学科网的优质题目'),
                                           ('北师大附中校本练习', '附中自编资料'),
                                           ('全国中考试题汇编', '各地中考真题'),
                                           ('区教研试题', '来自海淀、朝阳等区的试卷'),
                                           ('名校模考', '来自清北附中等学校的模拟题'),
                                           ('数学竞赛选题', '初中奥数试题'),
                                           ('课本例题拓展', '课本延伸型题目'),
                                           ('同步训练题', '同步作业中精选题'),
                                           ('错题集改编', '教师归纳的易错题型'),
                                           ('教师原创', '附中数学组原创题目');

-- ==========================================
-- 插入年纪数据sql
-- ==========================================
INSERT INTO grade (name, description) VALUES
                                          ('Grade7-FirstTerm', '初一上册'),
                                          ('Grade7-SecondTerm', '初一下册'),
                                          ('Grade8-FirstTerm', '初二上册'),
                                          ('Grade8-SecondTerm', '初二下册'),
                                          ('Grade9-FirstTerm', '初三上册'),
                                          ('Grade9-SecondTerm', '初三下册');

-- ==========================================
-- 插入综合类型数据sql
-- ==========================================
INSERT INTO complexity_type (type_name, description) VALUES
                                                         ('FunctionIntegration', '函数与代数整合'),
                                                         ('GeometryLogic', '几何与逻辑融合'),
                                                         ('StatisticsRealLife', '统计与生活实际结合'),
                                                         ('GraphModeling', '图像与建模应用'),
                                                         ('ProofAndReasoning', '证明与推理'),
                                                         ('InequalityApplication', '不等式的综合应用'),
                                                         ('EquationTransformation', '方程与变换整合'),
                                                         ('SpaceAndShape', '空间与图形的多角度考察'),
                                                         ('MathAndArt', '数学与美术交叉考察'),
                                                         ('CrossDisciplineChallenge', '跨学科挑战性题目');
-- ==========================================
-- 插入核心素养数据sql
-- ==========================================
INSERT INTO core_competency (competency_name, description) VALUES
                                                               ('LogicalThinking', '逻辑思维能力'),
                                                               ('AbstractModeling', '抽象建模能力'),
                                                               ('DataAwareness', '数据意识'),
                                                               ('SpatialImagination', '空间想象力'),
                                                               ('MathematicalExpression', '数学表达能力'),
                                                               ('ProblemSolving', '问题解决能力'),
                                                               ('ComputationalFluency', '计算能力'),
                                                               ('MathematicalAesthetics', '数学美感'),
                                                               ('MathematicalCommunication', '数学交流能力'),
                                                               ('ScientificReasoning', '科学推理能力');

-- ==========================================
-- 插入简单题-选择题,元信息
-- ==========================================
INSERT INTO source (name) values ('福建省泉州晋江市2024-2025学年下学期九年级4月质量测试数学试题');
INSERT INTO question (question_type, simple_question_type, grade_id, source_id, difficulty, complexity_type_id, core_competency_id, created_by)
VALUES (0, 0, 3, 1, 0.94,1,1,1);

-- ==========================================
-- 插入简单题-选择题,题干信息
-- ==========================================

INSERT INTO question_stem_block (question_id, content_type, text_content, image_file_id, position)
VALUES (1, 0, '下列为负数的是（     ）', NULL, 0);

-- ==========================================
-- 插入简单题-选择题,答案信息
-- ==========================================
INSERT INTO question_answer_block (question_id, content_type, image_file_id, interactive_index, answer_text, position)
VALUES (1, 0, NULL, 0, 'C',0);

-- ==========================================
-- 插入简单题-选择题,解析-分析信息
-- ==========================================
INSERT INTO question_explanation_block (question_id, explanation_type, content_type, image_file_id, explanation_text, position)
VALUES (1, 0,0,NULL,'本题考查了对正数、负数的理解，注意0既不是正数也不是负数.
根据正数大于0，负数小于0即可得出的答案.',0);

-- ==========================================
-- 插入简单题-选择题,解析-详解信息
-- ==========================================
INSERT INTO question_explanation_block (question_id, explanation_type, content_type, image_file_id, explanation_text, position)
VALUES (1, 1,0,NULL,'解：A.$0$不是负数，故此选项不符合题意；
B.$3.14>0$,是正数，故此选项不符合题意；
C.$一1<0$，故此选项符合题意；
D.$2>0$是正数，故此选项不符合题意；
故选：C.',0);

-- ==========================================
-- 插入简单题-选择题,选项信息
-- ==========================================
INSERT INTO question_option (question_id, label, content, image_file_ids, image_positions)
VALUES (1, 'A', '0', NULL, '[0]'),
       (1, 'B', '3.14', NULL, '[0]'),
       (1, 'C', '-1', NULL, '[0]'),
       (1, 'D', '\sqrt{2}', NULL, '[0]');

-- ==========================================
-- 插入简单题-选择题,题目-知识点关联数据
-- ==========================================

INSERT INTO question_knowledge (question_id, knowledge_point_id) VALUES (1, 4700)