
# --- !Ups

create table email_template (
  id                            bigint auto_increment not null,
  created_at                    timestamp not null,
  updated_at                    timestamp,
  code                          varchar(255) not null,
  subject                       varchar(255) not null,
  body                          varchar(255) not null,
  constraint uq_email_template_code unique (code),
  constraint pk_email_template primary key (id)
);


# --- !Downs

drop table if exists email_template;

