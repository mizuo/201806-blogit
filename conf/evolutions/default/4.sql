
# --- !Ups

insert into email_template (code, subject, body, registered) values (
	'createOwnerTemporaryRegistration', '[:blogit]owner temporary registration', 'temporaryPassword: :temporaryPassword', CURRENT_TIMESTAMP);


# --- !Downs

delete from email_template where code = 'createOwnerTemporaryRegistration';
