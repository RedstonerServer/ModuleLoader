package com.redstoner.misc;

import com.redstoner.annotations.Version;
import com.redstoner.exceptions.MissingVersionException;

/** This class can be used to compare modules against the loader version or against each other to prevent dependency issues.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 0, compatible = -1)
public final class VersionHelper
{
	private VersionHelper()
	{}
	
	/** Checks two modules versions for compatibility.
	 * 
	 * @param base The base to compare to.
	 * @param module The module to compare.
	 * @return true, when the Major version of the base is bigger than the compatible version of the module, and the Version number of the base is smaller or equal to the Version number of the module.
	 * @throws MissingVersionException When one of the parameters is not annotated with a @Version annotation. */
	public static boolean isCompatible(Class<?> base, Class<?> module) throws MissingVersionException
	{
		if (!base.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The base object is not annotated with a version.");
		if (!module.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The module is not annotated with a version.");
		Version baseVersion = base.getClass().getAnnotation(Version.class);
		Version moduleVersion = module.getClass().getAnnotation(Version.class);
		if (baseVersion.major() > moduleVersion.major())
			return false;
		return baseVersion.major() >= moduleVersion.compatible();
	}
	
	/** Returns the version of a given class as a String.
	 * 
	 * @param clazz The class to grab the version number from.
	 * @return The version number of the class in format major.minor.revision.compatible.
	 * @throws MissingVersionException If the class is not annotated with @Version. */
	public static String getVersion(Class<?> clazz) throws MissingVersionException
	{
		if (!clazz.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The given class is not associated with a version.");
		Version ver = clazz.getAnnotation(Version.class);
		return ver.major() + "." + ver.minor() + "." + ver.revision() + "." + ver.compatible();
	}
}
