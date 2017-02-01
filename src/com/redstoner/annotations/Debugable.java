package com.redstoner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Debugable annotation, to be added to methods that invoke the Debugger.notifyMethod method for debugging purposes.
 * 
 * @author Pepich */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Debugable
{}
