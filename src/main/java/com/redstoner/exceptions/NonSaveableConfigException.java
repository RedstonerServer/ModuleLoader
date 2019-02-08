package com.redstoner.exceptions;

public class NonSaveableConfigException extends Exception {
	private static final long serialVersionUID = -7271481973389455510L;

	public NonSaveableConfigException() {
		super("This config does not support saving!");
	}
}
