CREATE TABLE researchFields (
  `fieldId` int(4),
  `fieldName` VARCHAR(100)
)ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

INSERT INTO researchFields (fieldId, fieldName) VALUES (1, 'Physics');
INSERT INTO researchFields (fieldId, fieldName) VALUES (2, 'Chemistry');
INSERT INTO researchFields (fieldId, fieldName) VALUES (3, 'Biology');
INSERT INTO researchFields (fieldId, fieldName) VALUES (4, 'Material Science and Engineering');
INSERT INTO researchFields (fieldId, fieldName) VALUES (5, 'Chemical Engineering');
INSERT INTO researchFields (fieldId, fieldName) VALUES (6, 'Electrical Engineering');

CREATE TABLE user (
  `userId` int(4) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(30),
  `email` VARCHAR(50),
  `hash` VARCHAR(50),
  `phoneNumber` VARCHAR(20),
  `fields` VARCHAR(30),
  `address` VARCHAR(200),
  `COUNTRY` VARCHAR(20),
  `lastupdatetime` bigint,
  PRIMARY KEY(`userId`)
)AUTO_INCREMENT=1000
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE conference (
  `conferenceId` int(4) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200),
  `venue` VARCHAR(200),
  `startTime` DATE,
  `endTime` DATE,
  `fields` VARCHAR(30),
  `organizer` VARCHAR(200),
  `website` VARCHAR(500),
  `emails` VARCHAR(2000),
  `lastupdatetime` bigint,
  PRIMARY KEY(`conferenceId`)
)AUTO_INCREMENT=1000
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE presentation (
  `presentationId` int(4) NOT NULL AUTO_INCREMENT,
  `userId` int(4) NOT NULL,
  `conferenceId` int(4) NOT NULL,
  `title` VARCHAR(400),
  `authors` VARCHAR(200),
  `fileName` VARCHAR(200),
  `abs` VARCHAR(2000),
  `lastupdatetime` bigint,
  `isPrivate` tinyint(1),
  PRIMARY KEY(`presentationId`),
  CONSTRAINT `fk_userId` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_conferenceId` FOREIGN KEY (`conferenceId`) REFERENCES `conference` (`conferenceId`) ON DELETE CASCADE ON UPDATE CASCADE
)AUTO_INCREMENT=1000
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;