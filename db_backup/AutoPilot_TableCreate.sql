
CREATE TABLE dbo.LFAppName ( 
    AppName	varchar(50) NOT NULL 
    )
LOCK ALLPAGES


CREATE TABLE dbo.LFBrokerInfo ( 
    BrokerName     	varchar(20) NOT NULL,
    BrokerURL      	varchar(255) NOT NULL,
    EncryptedUserID	varchar(255) NOT NULL,
    EncryptedPass  	varchar(255) NOT NULL,
    Active         	char(1) NOT NULL,
    Comments       	varchar(255) NOT NULL,
    CONSTRAINT LFBrokerIn_Broker_12932476622 PRIMARY KEY(BrokerName)
)
LOCK DATAROWS


CREATE TABLE dbo.LFCategory ( 
    Category	varchar(50) NOT NULL 
    )
LOCK ALLPAGES


CREATE TABLE dbo.LFCommonOverwriteTag ( 
    AppName                   	varchar(20) NOT NULL,
    CommonOverwriteTagListName	varchar(255) NOT NULL,
    TagID                     	varchar(10) DEFAULT 0 NOT NULL,
    TagValue                  	varchar(255) NOT NULL,
    IsFunction                	char(1) NULL,
    CONSTRAINT LFCommonOv_1468155852 PRIMARY
KEY(AppName,CommonOverwriteTagListName,TagID)
)
LOCK DATAROWS


CREATE TABLE dbo.LFInputTagValues ( 
    TestID        	varchar(20) NOT NULL,
    ActionSequence	int NOT NULL,
    InputTagValue 	varchar(255) NOT NULL,
    IsFunction    	char(1) NULL,
    TagID         	varchar(10) DEFAULT 0 NOT NULL,
    CONSTRAINT LFInputTag_1697676622 PRIMARY
KEY(TestID,ActionSequence,TagID)
)
LOCK DATAROWS


CREATE TABLE dbo.LFOutputMsg ( 
    OutputMsgID   	int NOT NULL,
    TestID        	varchar(20) NOT NULL,
    ActionSequence	int NOT NULL,
    Template      	varchar(255) NULL,
    OutputMsg     	varchar(1500) NULL,
    TopicID       	varchar(255) NOT NULL,
    CONSTRAINT LFOutputMs_Output_14372481752 PRIMARY
KEY(TestID,ActionSequence,OutputMsgID)
)
LOCK DATAROWS


CREATE TABLE dbo.LFOutputTagValues ( 
    TestID          	varchar(20) NOT NULL,
    ActionSequence  	int NOT NULL,
    OutputMsgID     	int NOT NULL,
    TagID           	varchar(50) NOT NULL,
    ExpectedTagValue	varchar(255) NOT NULL,
    CONSTRAINT PRIMARY_KEY PRIMARY
KEY(TestID,ActionSequence,OutputMsgID,TagID)
)
LOCK DATAROWS


CREATE TABLE dbo.LFTemplate ( 
    AppName                   	varchar(20) NOT NULL,
    TemplateName              	varchar(255) NOT NULL,
    MsgType                   	varchar(255) NOT NULL,
    MsgTemplate               	text NOT NULL,
    Description               	varchar(255) NOT NULL,
    IsInput                   	char(1) NULL,
    CommonOverwriteTagListName	varchar(255) NULL,
    CONSTRAINT LFInputTem_737673201 PRIMARY KEY(TemplateName)
)
LOCK DATAROWS


CREATE TABLE dbo.LFTestCase ( 
    TestID        	varchar(20) NOT NULL,
    Category      	varchar(50) NOT NULL,
    Description   	varchar(255) NOT NULL,
    AppName       	varchar(50) DEFAULT 'US' NOT NULL,
    Region        	varchar(50) DEFAULT 'US' NOT NULL,
    ReleaseNum    	varchar(50) DEFAULT '2.2' NOT NULL,
    Active        	char(1) DEFAULT 'y' NOT NULL,
    SecurityClass 	int NOT NULL,
    Name          	varchar(50) NULL,
    LastModifiedBy	varchar(20) NULL,
    CONSTRAINT LFAutomate_TestID_11492471492 PRIMARY KEY(TestID)
)
LOCK DATAROWS


CREATE TABLE dbo.LFTestInputSteps ( 
    TestID        	varchar(20) NOT NULL,
    ActionSequence	int NOT NULL,
    Template      	varchar(255) NULL,
    Message       	text NULL,
    MsgType       	varchar(20) NULL,
    UseOutputMsg  	char(1) NULL,
    OutputMsgID   	varchar(10) NULL,
    TopicID       	varchar(50) NULL,
    CONSTRAINT lftis_pk PRIMARY KEY(TestID,ActionSequence)
)
LOCK DATAROWS


CREATE TABLE dbo.LFTestOutcome ( 
    TestID                	varchar(20) NOT NULL,
    LastActivityUserID    	varchar(50) DEFAULT user_name() NOT
NULL,
    ValidationTimestamp   	datetime DEFAULT getdate() NOT NULL,
    Symbol                	varchar(20) NOT NULL,
    ValidationResultStatus	varchar(20) NOT NULL,
    ValidationObjectDetail	text NOT NULL,
    ValidationResultMsg   	text NOT NULL,
    FailedInputStep       	int NOT NULL,
    FailedOutputMsgID     	int NOT NULL,
    Comments              	varchar(255) NOT NULL,
    TestingBroker         	varchar(255) NOT NULL,
    CONSTRAINT pk_LFTestOutcome PRIMARY
KEY(TestID,LastActivityUserID,ValidationTimestamp)
)
LOCK ALLPAGES


CREATE TABLE dbo.LFTopic ( 
    BrokerName 	varchar(20) NOT NULL,
    TopicName  	varchar(255) NOT NULL,
    Description	varchar(50) NOT NULL,
    Active     	char(1) NOT NULL,
    TopicID    	varchar(100) NOT NULL,
    CONSTRAINT LFTopic_PrimaryKey PRIMARY KEY(TopicID)
)
LOCK DATAROWS

ALTER TABLE dbo.LFTopic
    ADD CONSTRAINT LFTopic_1405248061
	FOREIGN KEY(BrokerName)
	REFERENCES dbo.LFBrokerInfo(BrokerName) 
