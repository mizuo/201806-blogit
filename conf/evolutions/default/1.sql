
# --- !Ups

create table account (
  login_id                      varchar(255) not null,
  password                      varchar(64) not null,
  created_at                    timestamp not null,
  updated_at                    timestamp,
  individual_id                 bigint not null,
  constraint uq_account_individual_id unique (individual_id),
  constraint pk_account primary key (login_id)
);

create table individual (
  id                            bigint auto_increment not null,
  created_at                    timestamp not null,
  updated_at                    timestamp,
  email_address                 varchar(255) not null,
  applied_at                    timestamp not null,
  joined_at                     timestamp not null,
  constraint pk_individual primary key (id)
);

alter table account add constraint fk_account_individual_id foreign key (individual_id) references individual (id) on delete restrict on update restrict;


# --- !Downs

alter table account drop constraint if exists fk_account_individual_id;

drop table if exists account;

drop table if exists individual;

