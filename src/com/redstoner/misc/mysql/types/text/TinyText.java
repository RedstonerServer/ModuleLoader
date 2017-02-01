package com.redstoner.misc.mysql.types.text;

public class TinyText extends Text {
	@Override
	public String getName() {
		return "TINY" + super.getName();
	}
}
