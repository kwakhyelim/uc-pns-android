-- drop 
drop table if exists sz_msg;
-- drop 
drop table if exists sz_log;
-- push data  
create table sz_msg (
	_id integer primary key autoincrement, -- 키 (V)
	msg_id varchar(20) not null, 
	msg_type varchar(1), -- 긴급, 투투 (V)
	logging_yn varchar(1), -- 서버에 저장된 메시지인지 여부 
	confirm_yn varchar(1), -- 사용자가 확인 처리 필요한 여부 
	msg text null, -- 메시지 내용 ( html ) (V)
	msg_dtl varchar(1), -- 상세 메시지 유형 
	create_id varchar(100),
	create_nm varchar(100), -- 보낸 사람 이름 (V)
	recv_time varchar(14),	-- 수신 시간 ( 년월일시분초 )  (V)
	read_yn varchar(1) default 'N', -- 메시지 확인 여부  (V)  수정 
	read_time varchar(14),	-- 읽은 시간 (V) 수정 
	title text null, -- 제목 
	start_dt varchar(14), 	-- 시작일자 
	end_dt varchar(14),	-- 종료일자 
	loc varchar(100)	--  장소 
);

-- log table
create table sz_log(
	log_time varchar(14) not null, -- 로그 기록 시간 
	msg_code varchar(20) not null,
	msg text	-- 로그 메시지 
);