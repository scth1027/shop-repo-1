insert into Kunde (art,id,email,nachname,password,seit,vorname,firmenname) values ('F', 1, 'Amail@gmail.com', 'Arnold', 'password1', '1.09.2013', 'Arnold', 'Afirma');
insert into Kunde (art,id,email,nachname,password,seit,vorname,firmenname) values ('F', 2, 'Bmail@gmail.com', 'Bauer', 'password2', '1.09.2013', 'Carlo', 'Bfirma');
insert into Kunde (art,id,email,nachname,password,seit,vorname,firmenname) values ('F', 3, 'Cmail@gmail.com', 'Carlotti', 'password3', '1.09.2013', 'Bernd', 'Cfirma');
insert into Kunde (art,id,email,nachname,password,seit,vorname,firmenname) values ('F', 4, 'Dmail@gmail.com', 'Dumke', 'password4', '1.09.2013', 'David', 'Dfirma');

-------------------------------
----------- Adresse -----------
-------------------------------

insert into adresse (hausnummer, id, kunde_fk, ort, plz, strasse) values(11, 1, 1, 'Aort', 11111, 'Astrasse' );
insert into adresse (hausnummer, id, kunde_fk, ort, plz, strasse) values(22, 2, 2, 'Bort', 22222, 'Bstrasse' );
insert into adresse (hausnummer, id, kunde_fk, ort, plz, strasse) values(33, 3, 3, 'Cort', 33333, 'Cstrasse' );
insert into adresse (hausnummer, id, kunde_fk, ort, plz, strasse) values(44, 4, 4, 'Dort', 44444, 'Dstrasse' );