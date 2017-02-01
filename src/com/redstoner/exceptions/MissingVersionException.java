package com.redstoner.exceptions;

import com.redstoner.annotations.Version;

/** To be thrown when a module is not annotated with its version. If this gets thrown, then oh boy, you're in trouble now.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 0, compatible = -1)
public class MissingVersionException extends Exception
{
	private static final long serialVersionUID = 4940161335512222539L;
	
	public MissingVersionException()
	{
		super();
	}
	
	public MissingVersionException(String message)
	{
		super(message);
	}
}
