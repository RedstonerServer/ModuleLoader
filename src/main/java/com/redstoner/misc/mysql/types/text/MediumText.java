package com.redstoner.misc.mysql.types.text;

public class MediumText extends Text {
	@Override
	public String getName() {
		return "MEDIUM" + super.getName();
	}
}