package com.redstoner.misc.mysql.types.number;

public class MediumInt extends Int {
	public MediumInt(int maxSize) {
		super(maxSize);
	}

	@Override
	public String getName() {
		return "MEDIUM" + super.getName();
	}
}