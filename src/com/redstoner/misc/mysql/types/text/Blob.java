package com.redstoner.misc.mysql.types.text;

import com.redstoner.misc.mysql.types.MysqlType;

public class Blob extends MysqlType {
	@Override
	public String getName() {
		return "BLOB";
	}
}
