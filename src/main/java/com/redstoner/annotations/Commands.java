package com.redstoner.annotations;

import com.redstoner.misc.CommandHolderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target (ElementType.TYPE)
@Retention (RetentionPolicy.RUNTIME)
public @interface Commands {
	CommandHolderType value();
}
