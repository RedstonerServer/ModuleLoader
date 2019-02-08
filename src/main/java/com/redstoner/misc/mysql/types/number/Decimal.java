package com.redstoner.misc.mysql.types.number;

import com.redstoner.misc.mysql.types.MysqlType;

public class Decimal extends MysqlType {
	@Override
	public String getName() {
		return "DECIMAL";
	}
}
