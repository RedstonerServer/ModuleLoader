package com.redstoner.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import com.redstoner.misc.CommandHolderType;

@Target(ElementType.TYPE)
public @interface Commands
{
	CommandHolderType value();
}
