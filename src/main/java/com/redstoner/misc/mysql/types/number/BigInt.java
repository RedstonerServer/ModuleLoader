package com.redstoner.misc.mysql.types.number;

public class BigInt extends Int {
	public BigInt(int maxSize) {
		super(maxSize);
	}

	@Override
	public String getName() {
		return "BIG" + super.getName();
	}
}