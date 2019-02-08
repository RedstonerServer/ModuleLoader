package com.redstoner.misc.mysql.types.date;

import com.redstoner.misc.mysql.types.MysqlType;

public class DateTime extends MysqlType {
	@Override
	public String getName() {
		return "DATETIME";
	}
}
