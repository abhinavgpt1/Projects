drop database if exists milkmandb;
use milkmandb;

create table customerentry(
	sname varchar(50),
	mobile varchar(15), 
	address varchar(100), 
	cq float,
	cprice float,
	bq float,
	bprice float,
	dos varchar(50),
	imgpath varchar(200)	
);
create table billpanel(
	id int primary key identity,
	sname varchar(50),
	dos varchar(50),
	doe varchar(50),
	amount float,
	cqty float,
	bqty float,
	status varchar(10)
);
create table variationconsole(
	sname varchar(50),
	cdate varchar(15),
	cq float,
	bq float
);

create table idpwd(
	id varchar(36) primary key,
	password varchar(30) not null
);