package com.redstoner.misc.mysql.types.text;

public class LongBlob extends Blob {
	@Override
	public String getName() {
		return "LONG" + super.getName();
	}
}