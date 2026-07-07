create table if not exists  "users" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"name" varchar(50) not null,
	"password" varchar(50) not null,
	"status" varchar(50)
);

create table if not exists "role" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"name" varchar(50)
);

create table if not exists "user_role" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"user_id" int not null,
	"role_id" int not null,
	foreign key ("user_id") references users("id"),
	foreign key ("role_id") references role("id"),
	constraint "user_role_key" unique ("user_id", "role_id")
);

create table if not exists "category" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"name" varchar(50),
	"parent" int
);

create table if not exists "version" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"name" varchar(50)
);

create table if not exists "file" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"link" text
);

create table if not exists "photo" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"link" text
);

create table if not exists "mod" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"name" varchar(50),
	"description" text,
	"category" int,
	"views" bigint,
	foreign key ("category") references category("id")
);

create table if not exists "mod_version" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"mod_id" int not null,
	"version_id" int not null,
	foreign key ("mod_id") references mod("id"),
	foreign key ("version_id") references version("id"),
	constraint "mod_version_key" unique ("mod_id", "version_id")
);

create table if not exists "mod_photo" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"mod_id" int not null,
	"photo_id" int not null,
	foreign key ("mod_id") references mod("id"),
	foreign key ("photo_id") references photo("id"),
	constraint "mod_photo_key" unique ("mod_id", "photo_id")
);

create table if not exists "mod_file" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"mod_id" int not null,
	"file_id" int not null,
	foreign key ("mod_id") references mod("id"),
	foreign key ("file_id") references file("id"),
	constraint "mod_file_key" unique ("mod_id", "file_id")
);

create table if not exists "file_version" (
	"id" serial primary key,
	"created" timestamp default now(),
	"updated" timestamp default now(),
	"file_id" int not null,
	"version_id" int not null,
	foreign key ("file_id") references file("id"),
	foreign key ("version_id") references version("id"),
	constraint "file_version_key" unique ("file_id", "version_id")
);

