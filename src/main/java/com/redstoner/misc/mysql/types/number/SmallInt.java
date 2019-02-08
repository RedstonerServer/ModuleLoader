package com.redstoner.misc.mysql.types.number;

public class SmallInt extends Int {
	public SmallInt(int maxSize) {
		super(maxSize);
	}

	@Override
	public String getName() {
		return "SMALL" + super.getName();
	}
}