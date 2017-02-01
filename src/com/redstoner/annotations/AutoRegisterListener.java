package com.redstoner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The auto register annotation, to be put onto Classes that implement listener when you are too lazy to register the events yourself.
 * 
 * @author Pepich */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Version(major = 1, minor = 0, revision = 1, compatible = 1)
public @interface AutoRegisterListener
{}
