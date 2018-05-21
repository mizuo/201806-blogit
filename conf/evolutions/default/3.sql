
# --- !Ups

create table email_template (
  code                          varchar(255) not null,
  subject                       varchar(255) not null,
  body                          varchar(255) not null,
  registered                    timestamp not null,
  constraint pk_email_template primary key (code)
);


# --- !Downs

drop table if exists email_template;

