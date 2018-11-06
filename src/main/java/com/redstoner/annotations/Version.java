package com.redstoner.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The Version annotation, to be applied to all Classes that are part of the project.
 * 
 * @author Pepich */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Version
{
	/** The major indicator of the version. Will be used for compatibility detection.
	 * 
	 * @return the major version as an int */
	int major();
	
	int minor();
	
	int revision();
	
	/** The compatibility part of the version number. Will be used for compatibility detection.</br>
	 * Set to -1 if it is supposed to be always compatible.</br>
	 * Defaults to 1.
	 * 
	 * @return the smallest compatible version as an int. */
	int compatible() default 1;
}
