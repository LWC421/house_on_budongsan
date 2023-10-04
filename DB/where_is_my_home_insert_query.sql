use home_origin;

-- 시도 코드 넣는 부분 --

insert into housedb.sidos(sidoCode, sidoName)
select cast(LEFT(dongCode, 2) as UNSIGNED) AS `sidoCode`, sidoName
from home_origin.dongcode
where gugunName IS NULL;

# 세종시의 경우 dongcode가 이상하게 되어있어서 따로 처리
insert into housedb.sidos(sidoCode, sidoName)
values(36, "세종특별자치시");

-- 구군 코드 넣는 부분 --
insert into housedb.guguns(gugunCode, gugunName, sidoCode)
select cast(LEFT(dongCode, 5) as UNSIGNED) AS `gugunCode`, gugunName, (select sidoCode from housedb.sidos as h where cast(LEFT(o.dongCode, 2) as UNSIGNED) = h.sidoCode) `sidoCode`
from home_origin.dongcode o
where dongName IS NULL
	and gugunName IS NOT NULL;

-- select *
-- from housedb.guguns;

-- 동 코드 넣는 부분 --
insert into housedb.dongs(dongCode, dongName, sidoCode, gugunCode)
select cast(dongCode as UNSIGNED) AS `dongCode`, 
	dongName, 
    (select sidoCode from housedb.sidos as h where cast(LEFT(o.dongCode, 2) as UNSIGNED) = h.sidoCode) `sidoCode`, 
    (select gugunCode from housedb.guguns as b where cast(LEFT(dongCode, 5) as UNSIGNED) = b.gugunCode) `gugunCode`
from home_origin.dongcode o
where dongName IS NOT NULL;

-- select *
-- from housedb.dongs;

-- select *
-- from housedb.dongs
-- join housedb.sidos using(sidoCode)
-- join housedb.guguns using(gugunCode)
-- where dongName = "파호동";

-- 아파트 정보 넣는 부분 --
desc housedb.houseInfos;
desc home_origin.houseinfo;

insert into housedb.houseInfos(houseInfoCode, buildYear, roadName, roadNameBonbun, roadNameSeq, roadNameBasementCode, roadNameCode, dong, bonbun, bubun,
	landCode, apartmentName, jibun, lng, lat, `point`, dongcode, gugunCode, sidoCode)
select CAST(aptCode AS UNSIGNED)AS `houseInfoCode`, buildYear, roadName, roadNameBonbun, roadNameSeq, roadNameBasementCode, roadNameCode, dong, bonbun, bubun,
	landCode, apartmentName, jibun, CAST(lng as DECIMAL(15, 12)) AS `lng`, CAST(lat as DECIMAL(15, 12)) as `lat`, point(CAST(lng as DECIMAL(15, 12)), CAST(lat as DECIMAL(15, 12))), dongCode,
    (select h.gugunCode from housedb.guguns h where cast(LEFT(dongCode, 5) as UNSIGNED) = h.gugunCode) as `gugunCode`,
    (select h.sidoCode from housedb.sidos h where cast(LEFT(dongCode, 2) as UNSIGNED) = h.sidoCode) as `sidoCode`
from home_origin.houseinfo;

-- select *
-- from housedb.houseInfos;

-- select *
-- from housedb.houseInfos h
-- where h.dongCode = 1111011500;

-- select *
-- from housedb.houseInfos h
-- where h.gugunCode = 1111000000;

-- 아파트 거래 정보 넣는 부분
desc housedb.houseDeals;
desc home_origin.housedeal;

# aptCode가 있는 houseDeal정보만 넣어주자
delete from home_origin.housedeal h
where h.aptCode not in(
	select aptCode
	from home_origin.houseinfo);

insert into housedb.houseDeals(houseDealCode, dealAmount, dealYear, dealMonth, dealDay, area, floor, houseInfoCode)
select no as `houseDealCode`, CAST(replace(dealAmount, ',', '') AS UNSIGNED) as `dealAmount`, 
	dealYear, dealMonth, dealDay, area, floor, aptCode as `houseInfoCode`
from home_origin.housedeal d;

select count(houseDealCode)
from housedb.houseDeals;