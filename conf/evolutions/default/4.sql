
# --- !Ups

insert into email_template (code, subject, body, created_at, updated_at) values (
	'owner', '[:blogit]owner temporary registration', 'temporaryPassword: :temporaryPassword', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
), (
	'activation', '[:blogit]activation', 'The register of the account completed.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);


# --- !Downs

delete from email_template where code in (
	'owner'
	, 'activation'
);
