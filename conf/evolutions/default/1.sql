# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  login_id                      varchar(255) not null,
  password                      varchar(64) not null,
  individual_id                 bigint not null,
  constraint uq_account_individual_id unique (individual_id),
  constraint pk_account primary key (login_id)
);

create table applicant (
  email_address                 varchar(255) not null,
  password                      varchar(64) not null,
  applied                       timestamp not null,
  constraint pk_applicant primary key (email_address)
);

create table individual (
  id                            bigint auto_increment not null,
  email_address                 varchar(255) not null,
  applied                       timestamp not null,
  joined                        timestamp not null,
  constraint pk_individual primary key (id)
);

alter table account add constraint fk_account_individual_id foreign key (individual_id) references individual (id) on delete restrict on update restrict;


# --- !Downs

alter table account drop constraint if exists fk_account_individual_id;

drop table if exists account;

drop table if exists applicant;

drop table if exists individual;

