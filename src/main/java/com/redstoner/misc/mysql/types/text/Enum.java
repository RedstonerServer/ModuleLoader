package com.redstoner.misc.mysql.types.text;

import com.redstoner.misc.mysql.types.MysqlType;

public class Enum extends MysqlType {
	private String[] possibleValues;

	public Enum(String... possibleValues) {
		this.possibleValues = possibleValues;
	}

	@Override
	public String getName() {
		String name = "ENUM(";

		for (int i = 0; i < possibleValues.length; i++) {
			name += "'" + possibleValues[i] + "'";

			if (i != possibleValues.length - 1) {
				name += ",";
			}
		}

		return name + ")";
	}

}
