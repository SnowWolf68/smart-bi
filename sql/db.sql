-- 用户表
drop table ai_assistant;
drop table chart;
drop table user;
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    user_account  varchar(256)                           not null comment '账号',
    user_password varchar(512)                           not null comment '密码',
    user_name     varchar(256)                           null comment '用户昵称',
    user_avatar   varchar(1024)                          null comment '用户头像',
    user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (user_account)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 图表表
create table if not exists chart
(
    id           bigint auto_increment comment 'id' primary key,
    goal				 text  null comment '分析目标',
    `name`               varchar(128) null comment '图表名称',
    chart_data    text  null comment '图表数据',
    chart_type	   varchar(128) null comment '图表类型',
    gen_chart		 text	 null comment '生成的图表数据',
    gen_result		 text	 null comment '生成的分析结论',
    -- 任务状态字段(排队中wait、执行中running、已完成succeed、失败failed)
    status       varchar(128) not null default 'wait' comment 'wait,running,succeed,failed',
    -- 任务执行信息字段
    exec_message  text   null comment '执行信息',
    user_id       bigint null comment '创建用户 id',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除'
    ) comment '图表信息表' collate = utf8mb4_unicode_ci;


-- AI 问答助手表
create table if not exists ai_assistant
(
    id             bigint auto_increment comment 'id' primary key,
    question_name   varchar(256)                           null comment '问题名称',
    question_goal   text                                   null comment '问题概述',
    question_result text                                   null comment '问答结果',
    question_type   varchar(512)                           null comment '问题类型',
    question_status varchar(128) default 'wait'            not null default 'wait' comment 'wait-等待,running-生成中,succeed-成功生成,failed-生成失败',
    exec_message    text                                   null comment '执行信息',
    user_id         bigint                                 null comment '创建用户 id',
    create_time     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint      default 0                 not null comment '是否删除'
    ) comment 'AI 问答助手信息表' collate = utf8mb4_unicode_ci;
