create table springbatch.test_data
(
    id             bigint auto_increment comment '관리번호'
        primary key,
    original_text  varchar(200)                         not null comment '원본 문자열',
    processed_text varchar(200)                         null comment '가공된 문자열',
    created_at     datetime default current_timestamp() not null comment '생성일시',
    updated_at     datetime                             null
)
    comment 'Spring batch 테스트용 테이블';

