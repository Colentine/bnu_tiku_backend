CREATE SCHEMA IF NOT EXISTS question_bank;

create table if not exists question_bank.complexity_type
(
    id          bigint unsigned auto_increment
        primary key,
    type_name   varchar(255) not null comment '综合类型名称',
    description text         null comment '类型描述'
)
    comment '综合类型';

create table if not exists question_bank.core_competency
(
    id              bigint unsigned auto_increment
        primary key,
    competency_name varchar(255) not null comment '核心素养名称',
    description     text         null comment '核心素养描述'
)
    comment '核心素养';


create table if not exists question_bank.grade
(
    id          bigint unsigned auto_increment
        primary key,
    name        varchar(50) not null comment '年级名称',
    description text        null comment '年级描述'
)
    comment '年级';

create table if not exists question_bank.image_file
(
    id          bigint unsigned auto_increment comment '图像表主键ID，唯一标识一张图像'
        primary key,
    image_url   text                               not null comment '图片链接或本地路径',
    uploaded_by bigint unsigned                    null comment '关联用户表的ID，表示上传该图像的用户',
    uploaded_at datetime default CURRENT_TIMESTAMP null comment '图像上传时间，默认为当前时间'
)
    comment '图像表';

create table if not exists question_bank.knowledge_point
(
    id          bigint unsigned                    comment '知识点表主键'
        primary key,
    name        varchar(255)                       not null comment '知识点名称',
    parent_id   bigint unsigned                    null comment '父知识点ID，支持树状结构',
    description text                               null comment '知识点的描述信息',
    created_at  datetime default CURRENT_TIMESTAMP null comment '知识点创建时间，默认为当前时间',
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '知识点最后更新时间，自动更新为当前时间'
)
    comment '知识点';

create table if not exists question_bank.question
(
    id                   bigint unsigned auto_increment
        primary key,
    question_type        tinyint unsigned                      not null comment '题目类型：简单题（simple）或复合题（composite）',
    simple_question_type tinyint unsigned                      null comment '简单题的具体类型，选择、填空等',
    subject              varchar(50) default '数学'             null comment '学科，默认数学',
    grade_id             bigint unsigned                       not null comment '年级，关联 grade 表',
    source_id            bigint unsigned                       not null comment '来源，关联 source 表',
    difficulty           float unsigned                        not null comment '题目难度系数',
    complexity_type_id   bigint unsigned                       null comment '综合类型，关联 complexity_type 表',
    core_competency_id   bigint unsigned                       null comment '核心素养，关联 core_competency 表',
    created_by           bigint unsigned                       not null comment '创建人/修改人，关联 user 表',
    created_at           datetime    default CURRENT_TIMESTAMP null,
    updated_at           datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '习题表';

create table if not exists question_bank.question_answer_block
(
    id                bigint unsigned auto_increment comment '填空题答案表主键ID，唯一标识一个答案'
        primary key,
    question_id bigint unsigned                        not null comment '关联题目块表的ID，表示该答案所属的题目',
    content_type      tinyint unsigned                       not null comment '内容类型，可选值为文本、图片',
    image_file_id     bigint unsigned                        null comment '图片id',
    interactive_index int unsigned                           not null comment '第几个作答交互，从1开始',
    answer_text       text                                   null comment '预期答案',
    position          int unsigned default '0'               null comment '内容在当前内容块的位置（从上往下）',
    created_at        datetime     default CURRENT_TIMESTAMP null comment '答案创建时间，默认为当前时间',
    updated_at        datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '答案最后更新时间，自动更新为当前时间'
)
    comment '习题答案块表';


create table if not exists question_bank.question_explanation_block
(
    id                bigint unsigned auto_increment comment '解析块表主键ID，唯一标识一个题目解析'
        primary key,
    question_id bigint unsigned                        not null comment '关联题目块表的ID，表示该答案所属的题目',
    explanation_type  tinyint unsigned                       not null comment '解析类型，可选值为分析、详解、点睛',
    content_type      tinyint unsigned                       not null comment '内容类型，可选值为文本、图片',
    image_file_id     bigint unsigned                        null comment '图片id',
    explanation_text  text                                   null comment '分析、详解或点睛的文本',
    position          int unsigned default '0'               null comment '内容在当前内容块的位置（从上往下）',
    created_at        datetime     default CURRENT_TIMESTAMP null comment '答案创建时间，默认为当前时间',
    updated_at        datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '答案最后更新时间，自动更新为当前时间'
)
    comment '习题解析块';

create table if not exists question_bank.question_knowledge
(
    id                 bigint unsigned auto_increment comment '题目与知识点关系表主键ID，唯一标识一条关系'
        primary key,
    question_id        bigint unsigned not null comment '关联题目主表的ID，表示该关系所属的题目',
    knowledge_point_id bigint unsigned not null comment '关联知识点表的ID，表示该关系所属的知识点'
)
    comment '习题知识点关联表';

create table if not exists question_bank.question_option
(
    id             bigint unsigned auto_increment comment '选项表主键ID，唯一标识一个选项'
        primary key,
    question_id    bigint unsigned                      not null comment '关联题目主表的ID，表示该选项所属的题目',
    label          char                                 not null comment '选项标签，如A、B、C、D',
    content        text                                 not null comment '选项的具体内容',
    image_file_ids  JSON                      null comment '关联图像表的ID，表示该选项所含的图像',
    image_positions JSON                         null comment '图像在选项中的位置',
    is_correct     tinyint(1) default 0                 null comment '是否为正确答案，默认为FALSE',
    created_at     datetime   default CURRENT_TIMESTAMP null comment '选项创建时间，默认为当前时间',
    updated_at     datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '选项最后更新时间，自动更新为当前时间'
)
    comment '选择题选项表';

create table if not exists question_bank.question_revision_log
(
    id             bigint unsigned auto_increment comment '修订日志表主键ID，唯一标识一条修订日志'
        primary key,
    question_id    bigint unsigned                    not null comment '关联题目主表的ID，表示该修订日志所属的题目',
    modified_field tinyint unsigned                   not null comment '修改的字段，可选值为题干、答案、解析、知识点、综合类型、难度等级、核心素养',
    old_value      text                               null comment '修改前的值',
    new_value      text                               null comment '修改后的值',
    modified_by    bigint unsigned                    null comment '关联用户表的ID，表示执行本次修改的用户',
    modified_at    datetime default CURRENT_TIMESTAMP null comment '修改时间，默认为当前时间'
)
    comment '习题修订日志';

create table if not exists question_bank.question_stem_block
(
    id                bigint unsigned auto_increment comment '内容块表主键ID，唯一标识一个内容块'
        primary key,
    question_id       bigint unsigned                        not null comment '关联题目主表的ID，表示该内容块所属的题目',
    content_type      tinyint unsigned                       not null comment '内容类型，可选值为文本、图片',
    text_content      text                                   null comment '文本的具体内容',
    image_file_id     bigint unsigned                        null comment '图片id',
    position          int unsigned default '0'               null comment '内容在当前内容块的位置（从上往下）',
    created_at        datetime     default CURRENT_TIMESTAMP null comment '内容块创建时间，默认为当前时间',
    updated_at        datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '内容块最后更新时间，自动更新为当前时间'
)
    comment '题干表';

create table if not exists question_bank.school
(
    id          smallint unsigned auto_increment
        primary key,
    school_name varchar(255) not null comment '学校名'
)
    comment '学校表';

create table if not exists question_bank.source
(
    id          bigint unsigned auto_increment
        primary key,
    name        varchar(255) not null comment '题目来源名称',
    description text         null comment '来源描述'
)
    comment '习题来源表';

create table if not exists question_bank.sub_question
(
    id            bigint unsigned auto_increment
        primary key,
    question_id   bigint unsigned                    not null comment '所属复合题ID，关联 question 表',
    question_type tinyint unsigned                   not null comment '题目类型',
    created_at    datetime default CURRENT_TIMESTAMP null,
    updated_at    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '子问题表';

create table if not exists question_bank.user
(
    id             bigint unsigned auto_increment comment '用户表主键ID，唯一标识一个用户'
        primary key,
    user_account   varchar(255)                               not null comment '用户账号，用于登录和标识用户',
    user_name      varchar(255)                               null comment '用户名',
    user_real_name varchar(10)                                null comment '用户真实姓名',
    user_password  varchar(255)                               null comment '用户密码',
    role           tinyint unsigned default '1'               null comment '用户角色，默认为teacher，可选值为admin、teacher',
    school_id      smallint unsigned                          null comment '用户所属学校',
    email          varchar(255)                               null comment '用户邮箱',
    created_at     datetime         default CURRENT_TIMESTAMP null,
    updated_at     datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    avatar_url     varchar(255)                               null,
    constraint user_pk
        unique (user_account)
)
    comment '用户表';

