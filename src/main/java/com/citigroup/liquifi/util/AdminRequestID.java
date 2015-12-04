package com.citigroup.liquifi.util;

public enum AdminRequestID{
		NA(-1,"-1","NA"),
		GETORDERS(1,"1","GET ORDERS"),
		DELETEORDER(2,"2","DELETE ORDER"),
		DONEFORDAYORDER(3,"3","DONE FOR DAY ORDER"),
		CANCELCHILDORDER(4,"4","CANCEL CHILD ORDER"),
		DELETEBOOK(5,"5","DELETE BOOK"),
		GETPROPERTIES(6,"6","GET PROPERTIES"),
		
		SETPROPERTIES(7,"7","SETPROPERTIES"),
		
		TRIGGERMKTOPEN(8,"8","TRIGGER MARKET OPEN"),
		TRIGGERMKTCLOSE(9,"9","TRIGGER MARKET CLOSE"),
		TRIGGERMANNOPEN(10,"10","TRIGGER MANNING OPEN"),
		TRIGGERMANNCLOSE(11,"11","TRIGGER MANNING CLOSE"),
		PRICELOCKINEXEC(12,"12","PRICE LOCKED IN EXECUTION"),
		RELOADCAPACCFILE(13,"13","RELOAD CAPACC FILE"),
		RELOADSPECIALORDERFILE(14,"14","RELOAD SPECIAL ORDER FILE"),
		
		CONNMKTBYSYMBOL(15,"15","CONNECT MARKET BY SYMBOL"),
		CONNMKT(16,"16","CONNECT MARKET"),
		DISCONNMKTBYSYMBOL(17,"17","DISCONNECT MARKET BY SYMBOL"),
		DISCONNMKT(18,"18","DISCONNECT MARKET"),
		
		CLASSLEVELSETPROPERTY(19,"19","CLASSLEVELSETPROPERTY"),
		
		GETMKTDATA(20,"20","GET MARKET DATA"),
		SETMKTDATA(21,"21","SET MARKET DATA"),
		SETNOOP(22,"22","SET NOOP"),
		SETNOCP(23,"23","SET NOCP"),
		
		REMOVEENTRYFROMSUBMITTEDTASKMAP(24,"24","REMOVE ENTRY FROM SUBMITTED TASKMAP"),
		UPLOADFILE(25,"25","UPLOADFILE"),//should not be implemented in Core
		UNSETAUTOEX(26,"26","UNSETAUTOEX"),
		SENDMODIFYACK(27,"27","SENDMODIFYACK"),
		SENDCANCELACK(28,"28","SENDCANCELACK"),
		SWITCHTOSOR(29,"29", "SWITCH TO SOR"),//Used only by EMEA
		SWITCHTOXSVC(30,"30", "SWITCH TO XSVC"),//Used only by EMEA
		GETALLBOOKS(31,"31","GETALLBOOKS"),
		GETFIXSTRING(32,"32","GETFIXSTRING"),
		SETORDERAUTOEXECINELIGIBLE(33,"33","SETORDERAUTOEXECINELIGIBLE"),
		GETENVIRONMENTPROPERTIES(34,"34","GET ENVIRONMENT PROPERTIES"),
		SETENVIRONMENTPROPERTIES(35,"35","SET ENVIRONMENT PROPERTIES"),
		UNSOLICITEDCANCEL(36,"36","UNSOLICITED CANCEL"),
		CANCELIOI(37,"37","CANCEL IOI"),
		REMOVEPHANTOMCHILDORDER(38,"38","REMOVE PHANTOM CHILD ORDER"),
		TRADECORRECT(39, "39", "TRADE CORRECT"),
		GETEXECUTIONS(40, "40", "GET EXECUTIONS"),
		TRADEBUST(41, "41", "TRADE CORRECT"),
		REMOVEQUOTEORDER(42, "42", "REMOVE QUOTE ORDER"),
		SETOFFSET(43,"43", "SET PRICE OFFSET");
		
		private final int typeValue;
		private final String stringValue;
		private final String name;
		
		AdminRequestID(int typeValue, String typeString, String name) {
			this.typeValue = typeValue;
			this.name = name;
			this.stringValue = typeString;
		}
		
		public int intValue() {
			return typeValue;
		}
		
		public String getName() {
			return name;
		}

		public String stringValue() {
			return stringValue;
		}

		public static AdminRequestID valueOf(int adminRequestID) {
			switch(adminRequestID) {
			case 1: return AdminRequestID.GETORDERS;
			case 2: return AdminRequestID.DELETEORDER;
			case 3: return AdminRequestID.DONEFORDAYORDER;
			case 4: return AdminRequestID.CANCELCHILDORDER;
			case 5: return AdminRequestID.DELETEBOOK;
			case 6: return AdminRequestID.GETPROPERTIES;
			case 7: return AdminRequestID.SETPROPERTIES;
			case 8: return AdminRequestID.TRIGGERMKTOPEN;
			case 9: return AdminRequestID.TRIGGERMKTCLOSE;
			case 10: return AdminRequestID.TRIGGERMANNOPEN;
			case 11: return AdminRequestID.TRIGGERMANNCLOSE;
			case 12: return AdminRequestID.PRICELOCKINEXEC;
			case 13: return AdminRequestID.RELOADCAPACCFILE;
			case 14: return AdminRequestID.RELOADSPECIALORDERFILE;
			case 15: return AdminRequestID.CONNMKTBYSYMBOL;
			case 16: return AdminRequestID.CONNMKT;
			case 17: return AdminRequestID.DISCONNMKTBYSYMBOL;
			case 18: return AdminRequestID.DISCONNMKT;
			case 19: return AdminRequestID.CLASSLEVELSETPROPERTY;
			case 20: return AdminRequestID.GETMKTDATA;
			case 21: return AdminRequestID.SETMKTDATA;
			case 22: return AdminRequestID.SETNOOP;
			case 23: return AdminRequestID.SETNOCP;
			case 24: return AdminRequestID.REMOVEENTRYFROMSUBMITTEDTASKMAP;
			case 25: return AdminRequestID.UPLOADFILE;
			case 26: return AdminRequestID.UNSETAUTOEX;
			case 27: return AdminRequestID.SENDMODIFYACK;
			case 28: return AdminRequestID.SENDCANCELACK;
			case 29: return AdminRequestID.SWITCHTOSOR;
			case 30: return AdminRequestID.SWITCHTOXSVC;
			case 31: return AdminRequestID.GETALLBOOKS;
			case 32: return AdminRequestID.GETFIXSTRING;
			case 33: return AdminRequestID.SETORDERAUTOEXECINELIGIBLE;
			case 34: return AdminRequestID.GETENVIRONMENTPROPERTIES;
			case 35: return AdminRequestID.SETENVIRONMENTPROPERTIES;
			case 36: return AdminRequestID.UNSOLICITEDCANCEL;
			case 37: return AdminRequestID.CANCELIOI;
			case 38: return AdminRequestID.REMOVEPHANTOMCHILDORDER;
			case 39: return AdminRequestID.TRADECORRECT;
			case 40: return AdminRequestID.GETEXECUTIONS;
			case 41: return AdminRequestID.TRADEBUST;
			case 42: return AdminRequestID.REMOVEQUOTEORDER;
			case 43: return AdminRequestID.SETOFFSET;

			default:
				return AdminRequestID.NA;
			}
		}
}
