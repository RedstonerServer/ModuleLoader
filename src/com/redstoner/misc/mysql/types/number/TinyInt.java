package com.redstoner.misc.mysql.types.number;

public class TinyInt extends Int {
	public TinyInt(int maxSize) {
		super(maxSize);
	}

	@Override
	public String getName() {
		return "TINY" + super.getName();
	}
}