
# --- !Ups

insert into email_template (created_at, code, subject, body) values (
	CURRENT_TIMESTAMP, 'owner', '[:blogit]owner temporary registration', 'temporaryPassword: :temporaryPassword'
), (
	CURRENT_TIMESTAMP, 'activation', '[:blogit]activation', 'The register of the account completed.'
);


# --- !Downs

delete from email_template where code in (
	'owner'
	, 'activation'
);
