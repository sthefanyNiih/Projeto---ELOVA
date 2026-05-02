create database elova;
use elova;

-- cria a tabela de dados principal e remova caso a mesma tabela já exista.
create table if not exists usuarios (
    id int auto_increment primary key,
    nome varchar(100) not null,
    email varchar(120) not null unique,
    senha varchar(120) not null
);

-- dados inseridos manualmente para teste
insert into usuarios (nome, email, senha)
values ('gabriela','testeemail@123gmail.com', '12345');

-- mostra dentro da tabela quais são os dadoss inseridos dentro da mesma.
select * from usuarios;

-- encontra emails duplicados
select usuarios.email, COUNT(*)
from usuarios
group by email
having COUNT(*) > 1;

-- deleta os emails duplicados e mantem o primeiro
delete u1 from usuarios u1
inner join usuarios u2
where u1.id > u2.id
and u1.email = u2.email;