package com.redstoner.modules.skullclick;

import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 0, compatible = 1)
@AutoRegisterListener
public class SkullClick implements Module, Listener
{
	private boolean enabled = false;
	private boolean seen = false;
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		// For some reason the event gets fired twice, this fixes it. Lol.
		if (seen)
		{
			seen = false;
			return;
		}
		seen = true;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && !e.isCancelled())
		{
			BlockState block = e.getClickedBlock().getState();
			if (block instanceof Skull)
			{
				Skull skull = (Skull) block;
				String owner = skull.getOwner();
				if (owner == null || owner.equals(""))
				{
					Utils.sendMessage(e.getPlayer(), null, "§eThat skull has no owner.");
				}
				else
				{
					Utils.sendMessage(e.getPlayer(), null, "§eThat's " + owner + ".");
				}
				if (!e.getPlayer().isSneaking())
				{
					e.setCancelled(true);
				}
			}
		}
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public void onEnable()
	{
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	@Override
	public String getCommandString()
	{
		return null;
	}
}
