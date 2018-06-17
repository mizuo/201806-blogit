
# --- !Ups

create table account_session (
  uuid                          uuid not null,
  ip_address                    varchar(39) not null,
  individual_id                 bigint not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_account_session primary key (uuid)
);

create index ix_account_session_individual_id on account_session (individual_id);


# --- !Downs

drop index if exists ix_account_session_individual_id;

drop table if exists account_session;
