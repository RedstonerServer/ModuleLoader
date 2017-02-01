package com.redstoner.misc.mysql.types.number;

import com.redstoner.misc.mysql.types.MysqlType;

public class Int extends MysqlType {
	private int maxSize;
	
	public Int(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	public String getName() {
		return "INT(" + maxSize + ")";
	}
}