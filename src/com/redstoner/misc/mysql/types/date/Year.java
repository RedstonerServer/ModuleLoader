package com.redstoner.misc.mysql.types.date;

import com.redstoner.misc.mysql.types.MysqlType;

public class Year extends MysqlType {
	@Override
	public String getName() {
		return "YEAR";
	}
}
