
# --- !Ups

create table applicant (
  email_address                 varchar(255) not null,
  password                      varchar(64) not null,
  applied_at                    timestamp not null,
  constraint pk_applicant primary key (email_address)
);


# --- !Downs

drop table if exists applicant;

