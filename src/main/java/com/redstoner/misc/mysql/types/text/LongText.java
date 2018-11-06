package com.redstoner.misc.mysql.types.text;

public class LongText extends Text {
	@Override
	public String getName() {
		return "LONG" + super.getName();
	}
}