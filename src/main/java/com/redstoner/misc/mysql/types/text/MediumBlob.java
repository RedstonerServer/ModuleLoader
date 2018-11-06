package com.redstoner.misc.mysql.types.text;

public class MediumBlob extends Blob {
	@Override
	public String getName() {
		return "MEDIUM" + super.getName();
	}
}