package com.redstoner.misc;

import java.lang.annotation.Annotation;

import com.redstoner.annotations.Version;
import com.redstoner.exceptions.MissingVersionException;

/** This class can be used to compare modules against the loader version or against each other to prevent dependency issues.
 * 
 * @author Pepich */
@Version(major = 2, minor = 1, revision = 1, compatible = 0)
public final class VersionHelper
{
	private VersionHelper()
	{}
	
	/** Checks two classes versions for compatibility.
	 * 
	 * @param base The API to compare to.
	 * @param module The module to compare.
	 * @return true, when the module is up to date with the API, or the API supports outdated modules.
	 * @throws MissingVersionException When one of the parameters is not annotated with a @Version annotation. */
	public static boolean isCompatible(Class<?> api, Class<?> module) throws MissingVersionException
	{
		if (!api.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The API is not annotated with a version.");
		if (!module.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The module is not annotated with a version.");
		Version apiVersion = api.getAnnotation(Version.class);
		Version moduleVersion = module.getAnnotation(Version.class);
		return isCompatible(apiVersion, moduleVersion);
	}
	
	/** Checks two classes versions for compatibility.
	 * 
	 * @param base The API to compare to.
	 * @param module The module to compare.
	 * @return true, when the module is up to date with the API, or the API supports outdated modules.
	 * @throws MissingVersionException When one of the parameters is not annotated with a @Version annotation. */
	public static boolean isCompatible(Version apiVersion, Class<?> module) throws MissingVersionException
	{
		if (!module.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The module is not annotated with a version.");
		Version moduleVersion = module.getAnnotation(Version.class);
		return isCompatible(apiVersion, moduleVersion);
	}
	
	/** Checks two classes versions for compatibility.
	 * 
	 * @param base The API to compare to.
	 * @param module The module to compare.
	 * @return true, when the module is up to date with the API, or the API supports outdated modules.
	 * @throws MissingVersionException When one of the parameters is not annotated with a @Version annotation. */
	public static boolean isCompatible(Class<?> api, Version moduleVersion) throws MissingVersionException
	{
		if (!api.isAnnotationPresent(Version.class))
			throw new MissingVersionException("The API is not annotated with a version.");
		Version apiVersion = api.getAnnotation(Version.class);
		return isCompatible(apiVersion, moduleVersion);
	}
	
	/** Checks two versions for compatibility.
	 * 
	 * @param base The API version to compare to.
	 * @param module The module version to compare.
	 * @return true, when the module is up to date with the API, or the API supports outdated modules.
	 * @throws MissingVersionException When one of the parameters is not annotated with a @Version annotation. */
	public static boolean isCompatible(Version apiVersion, Version moduleVersion)
	{
		if (apiVersion.major() >= moduleVersion.compatible())
			return true;
		if (apiVersion.compatible() == -1)
			return false;
		if (apiVersion.compatible() <= moduleVersion.major())
			return true;
		return false;
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
	
	/** This method creates a new Version to use for compatibility checks.
	 * 
	 * @param major The major version
	 * @param minor The minor version
	 * @param revision The revision
	 * @param compatible The compatibility tag
	 * @return */
	public static Version create(int major, int minor, int revision, int compatible)
	{
		return new Version()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Version.class;
			}
			
			@Override
			public int revision()
			{
				return revision;
			}
			
			@Override
			public int minor()
			{
				return minor;
			}
			
			@Override
			public int major()
			{
				return major;
			}
			
			@Override
			public int compatible()
			{
				return compatible;
			}
		};
	}
}
