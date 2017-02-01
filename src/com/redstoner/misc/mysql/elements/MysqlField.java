package com.redstoner.misc.mysql.elements;

import com.redstoner.misc.mysql.types.MysqlType;

public class MysqlField {
	private String name;
	private MysqlType type;
	private boolean canBeNull;
	
	public MysqlField(String name, MysqlType type, boolean canBeNull) {
		this.name = name;
		this.type = type;
		this.canBeNull = canBeNull;
	}
	
	public MysqlField(String name, String type, boolean canBeNull) {
		this.name = name;
		this.type = MysqlType.getTypeFromString(type);
		this.canBeNull = canBeNull;
	}
	
	public String getName() {
		return name;
	}
	
	public MysqlType getType() {
		return type;
	}
	
	public boolean canBeNull() {
		return canBeNull;
	}
}
