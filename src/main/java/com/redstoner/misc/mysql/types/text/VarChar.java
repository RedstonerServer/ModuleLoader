package com.redstoner.misc.mysql.types.text;

import com.redstoner.misc.mysql.types.MysqlType;

public class VarChar extends MysqlType {
	private int maxSize;
	
	public VarChar(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	public String getName() {
		return "VARCHAR(" + maxSize + ")";
	}
}