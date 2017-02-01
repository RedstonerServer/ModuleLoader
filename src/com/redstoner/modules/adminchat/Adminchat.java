package com.redstoner.modules.adminchat;

import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.modules.Module;

/** AdminChat module. Allows staff to chat to other staff using /ac \<message\> as well as a one char prefix or a toggle.
 * 
 * @author Pepich */
@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 1, compatible = 1)
public class Adminchat implements Module
{
	private boolean enabled = false;
	
	@Override
	public void onEnable()
	{
		this.enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		this.enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public String getCommandString()
	{
		return null;
	}
}
