
# --- !Ups

create table email_template (
  code                          varchar(64) not null,
  subject                       varchar(255) not null,
  body                          varchar(1024) not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_email_template primary key (code)
);


# --- !Downs

drop table if exists email_template;

