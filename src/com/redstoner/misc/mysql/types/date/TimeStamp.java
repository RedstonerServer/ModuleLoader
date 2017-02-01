package com.redstoner.misc.mysql.types.date;

import com.redstoner.misc.mysql.types.MysqlType;

public class TimeStamp extends MysqlType {
	@Override
	public String getName() {
		return "TIMESTAMP";
	}
}
