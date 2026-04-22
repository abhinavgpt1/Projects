drop database if exists MILK_SUBSCRIPTION;
create database MILK_SUBSCRIPTION;
use MILK_SUBSCRIPTION;

-- customer details we require are
-- * name, a unique id for identification, mobile, address to deliver milk, photoURL
-- * type of milk(currently cow, buffalo), their price as per customer contract
-- * start_date and end_date of subscription
-- **future idea - enable trial subscription thus forming isTrial datatype = bit
-- **future idea - enable a portable desktop app or maybe launch on playstore
-- **future idea - use AWS for image storing
create table customer_details(
	cust_id uniqueidentifier primary key,
	cust_name varchar(50) not null,
	cust_mobile varchar(15), 
	cust_address varchar(200), 
	cow_milk_qty float,
	cow_milk_price float,
	buffalo_milk_qty float,
	buffalo_milk_price float,
	start_date datetime not null,
	end_date datetime,
	cust_imgpath varchar(300)	
);