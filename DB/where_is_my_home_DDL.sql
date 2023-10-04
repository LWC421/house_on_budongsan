-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema housedb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `housedb` ;

-- -----------------------------------------------------
-- Schema housedb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `housedb` DEFAULT CHARACTER SET utf8 ;
USE `housedb` ;

-- -----------------------------------------------------
-- Table `housedb`.`sidos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`sidos` ;

CREATE TABLE IF NOT EXISTS `housedb`.`sidos` (
  `sidoCode` BIGINT NOT NULL,
  `sidoName` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`sidoCode`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`guguns`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`guguns` ;

CREATE TABLE IF NOT EXISTS `housedb`.`guguns` (
  `gugunCode` BIGINT NOT NULL,
  `gugunName` VARCHAR(45) NOT NULL,
  `sidoCode` BIGINT NOT NULL,
  PRIMARY KEY (`gugunCode`),
  INDEX `fk_guguns_sidos_idx` (`sidoCode` ASC) VISIBLE,
  CONSTRAINT `fk_guguns_sidos`
    FOREIGN KEY (`sidoCode`)
    REFERENCES `housedb`.`sidos` (`sidoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`dongs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`dongs` ;

CREATE TABLE IF NOT EXISTS `housedb`.`dongs` (
  `dongCode` BIGINT NOT NULL,
  `dongName` VARCHAR(45) NOT NULL,
  `sidoCode` BIGINT NOT NULL,
  `gugunCode` BIGINT NOT NULL,
  PRIMARY KEY (`dongCode`),
  INDEX `fk_dongs_guguns1_idx` (`gugunCode` ASC) VISIBLE,
  INDEX `fk_dongs_sidos1_idx` (`sidoCode` ASC) VISIBLE,
  CONSTRAINT `fk_dongs_guguns1`
    FOREIGN KEY (`gugunCode`)
    REFERENCES `housedb`.`guguns` (`gugunCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_dongs_sidos1`
    FOREIGN KEY (`sidoCode`)
    REFERENCES `housedb`.`sidos` (`sidoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`houseInfos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`houseInfos` ;

CREATE TABLE IF NOT EXISTS `housedb`.`houseInfos` (
  `houseInfoCode` BIGINT NOT NULL,
  `buildYear` INT NULL,
  `roadName` VARCHAR(45) NULL,
  `roadNameBonbun` VARCHAR(5) NULL,
  `roadNameSeq` VARCHAR(2) NULL,
  `roadNameBasementCode` VARCHAR(1) NULL,
  `roadNameCode` VARCHAR(7) NULL,
  `dong` VARCHAR(40) NULL,
  `bonbun` VARCHAR(4) NULL,
  `bubun` VARCHAR(4) NULL,
  `landCode` VARCHAR(1) NULL,
  `apartmentName` VARCHAR(40) NULL,
  `jibun` VARCHAR(10) NULL,
  `lng` DECIMAL(15,12) NULL,
  `lat` DECIMAL(15,12) NULL,
  `point` POINT NULL,
  `dongCode` BIGINT NOT NULL,
  `gugunCode` BIGINT NOT NULL,
  `sidoCode` BIGINT NOT NULL,
  PRIMARY KEY (`houseInfoCode`),
  INDEX `fk_houseInfos_dongs1_idx` (`dongCode` ASC) VISIBLE,
  INDEX `fk_houseInfos_guguns1_idx` (`gugunCode` ASC) VISIBLE,
  INDEX `fk_houseInfos_sidos1_idx` (`sidoCode` ASC) VISIBLE,
  CONSTRAINT `fk_houseInfos_dongs1`
    FOREIGN KEY (`dongCode`)
    REFERENCES `housedb`.`dongs` (`dongCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_houseInfos_guguns1`
    FOREIGN KEY (`gugunCode`)
    REFERENCES `housedb`.`guguns` (`gugunCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_houseInfos_sidos1`
    FOREIGN KEY (`sidoCode`)
    REFERENCES `housedb`.`sidos` (`sidoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`houseDeals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`houseDeals` ;

CREATE TABLE IF NOT EXISTS `housedb`.`houseDeals` (
  `houseDealCode` BIGINT NOT NULL,
  `dealAmount` INT NULL,
  `dealYear` INT NULL,
  `dealMonth` INT NULL,
  `dealDay` INT NULL,
  `area` DOUBLE NULL,
  `floor` INT NULL,
  `houseInfoCode` BIGINT NULL,
  PRIMARY KEY (`houseDealCode`),
  INDEX `fk_houseDeal_houseInfos1_idx` (`houseInfoCode` ASC) VISIBLE,
  CONSTRAINT `fk_houseDeal_houseInfos1`
    FOREIGN KEY (`houseInfoCode`)
    REFERENCES `housedb`.`houseInfos` (`houseInfoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`members`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`members` ;

CREATE TABLE IF NOT EXISTS `housedb`.`members` (
  `memberCode` BIGINT NOT NULL AUTO_INCREMENT,
  `memberId` VARCHAR(20) NOT NULL,
  `nickname` VARCHAR(45) NOT NULL,
  `password` VARCHAR(110) NOT NULL,
  `accessToken` VARCHAR(1000) NULL DEFAULT NULL,
  `refreshToken` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`memberCode`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`houseLikes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`houseLikes` ;

CREATE TABLE IF NOT EXISTS `housedb`.`houseLikes` (
  `houseInfoCode` BIGINT NOT NULL,
  `memberCode` BIGINT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`houseInfoCode`, `memberCode`),
  INDEX `fk_houseInfos_has_members_members1_idx` (`memberCode` ASC) VISIBLE,
  INDEX `fk_houseInfos_has_members_houseInfos1_idx` (`houseInfoCode` ASC) VISIBLE,
  CONSTRAINT `fk_houseInfos_has_members_houseInfos1`
    FOREIGN KEY (`houseInfoCode`)
    REFERENCES `housedb`.`houseInfos` (`houseInfoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_houseInfos_has_members_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`memberInfos`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`memberInfos` ;

CREATE TABLE IF NOT EXISTS `housedb`.`memberInfos` (
  `memberCode` BIGINT NOT NULL,
  `birthday` DATE NULL,
  `email` VARCHAR(45) NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deletedAt` DATETIME NULL,
  `updatedAt` DATETIME NULL,
  PRIMARY KEY (`memberCode`),
  INDEX `fk_memberInfo_members1_idx` (`memberCode` ASC) VISIBLE,
  CONSTRAINT `fk_memberInfo_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`dongHits`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`dongHits` ;

CREATE TABLE IF NOT EXISTS `housedb`.`dongHits` (
  `dongCode` BIGINT NOT NULL,
  `memberCode` BIGINT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`dongCode`, `memberCode`),
  INDEX `fk_dongs_has_members_members2_idx` (`memberCode` ASC) VISIBLE,
  INDEX `fk_dongs_has_members_dongs2_idx` (`dongCode` ASC) VISIBLE,
  CONSTRAINT `fk_dongs_has_members_dongs2`
    FOREIGN KEY (`dongCode`)
    REFERENCES `housedb`.`dongs` (`dongCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_dongs_has_members_members2`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`gugunHits`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`gugunHits` ;

CREATE TABLE IF NOT EXISTS `housedb`.`gugunHits` (
  `gugunCode` BIGINT NOT NULL,
  `memberCode` BIGINT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`gugunCode`, `memberCode`),
  INDEX `fk_guguns_has_members_members1_idx` (`memberCode` ASC) VISIBLE,
  INDEX `fk_guguns_has_members_guguns1_idx` (`gugunCode` ASC) VISIBLE,
  CONSTRAINT `fk_guguns_has_members_guguns1`
    FOREIGN KEY (`gugunCode`)
    REFERENCES `housedb`.`guguns` (`gugunCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_guguns_has_members_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`sidoHits`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`sidoHits` ;

CREATE TABLE IF NOT EXISTS `housedb`.`sidoHits` (
  `sidoCode` BIGINT NOT NULL,
  `memberCode` BIGINT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sidoCode`, `memberCode`),
  INDEX `fk_members_has_sidos_sidos1_idx` (`sidoCode` ASC) VISIBLE,
  INDEX `fk_members_has_sidos_members1_idx` (`memberCode` ASC) VISIBLE,
  CONSTRAINT `fk_members_has_sidos_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_members_has_sidos_sidos1`
    FOREIGN KEY (`sidoCode`)
    REFERENCES `housedb`.`sidos` (`sidoCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`boards`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`boards` ;

CREATE TABLE IF NOT EXISTS `housedb`.`boards` (
  `boardCode` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `memberCode` BIGINT NOT NULL,
  PRIMARY KEY (`boardCode`),
  INDEX `fk_board_members1_idx` (`memberCode` ASC) VISIBLE,
  CONSTRAINT `fk_board_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`boardHits`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`boardHits` ;

CREATE TABLE IF NOT EXISTS `housedb`.`boardHits` (
  `memberCode` BIGINT NOT NULL,
  `boardCode` BIGINT NOT NULL,
  `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`memberCode`, `boardCode`),
  INDEX `fk_members_has_board_board1_idx` (`boardCode` ASC) VISIBLE,
  INDEX `fk_members_has_board_members1_idx` (`memberCode` ASC) VISIBLE,
  CONSTRAINT `fk_members_has_board_members1`
    FOREIGN KEY (`memberCode`)
    REFERENCES `housedb`.`members` (`memberCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_members_has_board_board1`
    FOREIGN KEY (`boardCode`)
    REFERENCES `housedb`.`boards` (`boardCode`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `housedb`.`news`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `housedb`.`news` ;

CREATE TABLE IF NOT EXISTS `housedb`.`news` (
  `newsCode` BIGINT NOT NULL AUTO_INCREMENT,
  `title` LONGTEXT NOT NULL,
  `description` LONGTEXT NOT NULL,
  `originalLink` LONGTEXT NOT NULL,
  `link` LONGTEXT NOT NULL,
  `publishDate` DATETIME NOT NULL,
  PRIMARY KEY (`newsCode`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
