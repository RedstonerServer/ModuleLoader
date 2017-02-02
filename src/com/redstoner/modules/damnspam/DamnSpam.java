package com.redstoner.modules.damnspam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 1, revision = 1, compatible = 1)
public class DamnSpam implements Module, Listener
{
	private boolean enabled = false;
	File configFile = new File(Main.plugin.getDataFolder(), "DamnSpam.json");
	Map<String, SpamInput> inputs;
	boolean changingInput = false;
	List<Material> acceptedInputs;
	HashMap<Material, int[][]> attachedBlocks;
	HashMap<Player, SpamInput> players;
	int maxTimeout = 240;
	String timeoutErrorString = "&cThe timeout must be -1 or within 0 and " + maxTimeout;
	
	@Override
	public void onEnable()
	{
		loadInputs();
		acceptedInputs = new ArrayList<Material>();
		Collections.addAll(acceptedInputs, Material.WOOD_BUTTON, Material.STONE_BUTTON, Material.LEVER);
		attachedBlocks = new HashMap<Material, int[][]>();
		attachedBlocks.put(Material.LEVER,
				new int[][] {{0, 7, 8, 15}, {5, 6, 13, 14}, {4, 12}, {3, 11}, {2, 10}, {1, 9}});
		attachedBlocks.put(Material.STONE_BUTTON,
				new int[][] {{0, 8}, {5, 6, 7, 13, 14, 15}, {4, 12}, {3, 11}, {2, 10}, {1, 9}});
		attachedBlocks.put(Material.WOOD_BUTTON,
				new int[][] {{0, 8}, {5, 6, 7, 13, 14, 15}, {4, 12}, {3, 11}, {2, 10}, {1, 9}});
		players = new HashMap<Player, SpamInput>();
		enabled = true;
	}
	
	public void loadInputs()
	{
		inputs = new HashMap<String, SpamInput>();
		try
		{
			FileReader reader = new FileReader(configFile);
			JSONObject json = (JSONObject) new JSONParser().parse(reader);
			for (Object key : json.keySet())
			{
				JSONObject inputData = (JSONObject) json.get(key);
				String uuid = (String) inputData.get("creator");
				Double timeoutOn = (Double) inputData.get("timeout_on");
				Double timeoutOff = (Double) inputData.get("timeout_off");
				Double lastTime = (Double) inputData.get("last_time");
				inputs.put((String) key, new SpamInput(uuid, timeoutOff, timeoutOn, lastTime));
			}
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveInputs()
	{
		JSONObject json = new JSONObject();
		for (String key : inputs.keySet())
		{
			JSONObject jsonInput = new JSONObject();
			SpamInput input = inputs.get(key);
			jsonInput.put("creator", input.player);
			jsonInput.put("timeout_on", input.timeoutOn);
			jsonInput.put("timeout_off", input.timeoutOff);
			jsonInput.put("last_time", input.lastTime);
			json.put(key, jsonInput);
		}
		try
		{
			PrintWriter writer = new PrintWriter(configFile);
			writer.write(json.toJSONString());
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public String locationString(Location loc)
	{
		return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}
	
	public boolean isAcceptableTimeout(double timeout)
	{
		return (timeout > 0 && timeout <= maxTimeout) || timeout == -1;
	}
	
	public boolean canBuild(Player player, Block block)
	{
		BlockBreakEvent event = new BlockBreakEvent(block, player);
		Bukkit.getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
	
	@Command(hook = "damnspamSingle")
	public void damnspam(CommandSender sender, double seconds)
	{
		boolean destroyingInput = false;
		seconds = (double) Math.round(seconds * 100) / 100;
		if (seconds == 0)
			destroyingInput = true;
		else if (!isAcceptableTimeout(seconds))
		{
			Utils.sendMessage(sender, null, "&cThe timeout must be -1 or within 0 and " + maxTimeout, '&');
			return;
		}
		Utils.sendMessage(sender, null, "&aPlease click the input you would like to set.", '&');
		setPlayer((Player) sender, destroyingInput, seconds, seconds);
	}
	
	@Command(hook = "damnspamDouble")
	public void damnspam(CommandSender sender, double secondsOff, double secondsOn)
	{
		boolean destroyingInput = false;
		secondsOn = (double) Math.round(secondsOn * 100) / 100;
		secondsOff = (double) Math.round(secondsOff * 100) / 100;
		if (secondsOn == 0 && secondsOff == 0)
		{
			destroyingInput = true;
		}
		else if (!(isAcceptableTimeout(secondsOn) && isAcceptableTimeout(secondsOff)))
		{
			Utils.sendMessage(sender, null, "&cThe timeout must be -1 or within 0 and " + maxTimeout, '&');
			return;
		}
		Utils.sendMessage(sender, null, "&aPlease click the input you would like to set.", '&');
		setPlayer((Player) sender, destroyingInput, secondsOff, secondsOn);
	}
	
	public void setPlayer(Player player, boolean destroying, double timeoutOff, double timeoutOn)
	{
		SpamInput input = null;
		if (!destroying)
		{
			input = new SpamInput(player.getUniqueId().toString(), timeoutOff, timeoutOn, 0);
		}
		players.put(player, input);
	}
	
	public boolean attemptInputRegister(Player player, Block block, Cancellable event)
	{
		if (players.containsKey(player))
		{
			if (!acceptedInputs.contains(block.getType()))
			{
				Utils.sendMessage(player, null, "&cThat block is not an acceptable input!", '&');
				return true;
			}
			String typeStr = block.getType().toString().toLowerCase().replace("_", " ");
			String locationStr = locationString(block.getLocation());
			changingInput = true;
			boolean buildCheck = canBuild(player, block);
			changingInput = false;
			if (!buildCheck)
			{
				Utils.sendMessage(player, null,
						"&cThere is no timeout to remove on this " + typeStr + "(by setting the timeout to 0)", '&');
				return true;
			}
			SpamInput input = players.get(player);
			if (input == null)
			{
				if (!inputs.containsKey(locationStr))
				{
					Utils.sendMessage(player, null,
							"&cThere is no timeout to remove on this " + typeStr + "(by setting the timeout to 0)",
							'&');
					return true;
				}
				inputs.remove(locationStr);
				Utils.sendMessage(player, null, "&aSuccessfully removed the timeout for this " + typeStr, '&');
			}
			else
			{
				inputs.put(locationStr, players.get(player));
				Utils.sendMessage(player, null, "&aSuccessfully set a timeout for this " + typeStr, '&');
			}
			event.setCancelled(true);
			players.remove(player);
			saveInputs();
			return true;
		}
		return false;
	}
	
	public void checkBlockBreak(BlockBreakEvent event, Block block)
	{
		if (!acceptedInputs.contains(block.getType()))
			return;
		String posStr = locationString(block.getLocation());
		if (!inputs.containsKey(posStr))
			return;
		SpamInput input = inputs.get(posStr);
		Player sender = event.getPlayer();
		String typeStr = block.getType().toString().toLowerCase().replace("_", " ");
		String inputStr = (block.getLocation().equals(event.getBlock()) ? "this " + typeStr
				: "the " + typeStr + " attached to that block");
		if (!sender.isSneaking())
		{
			Utils.sendMessage(sender, null, "&cYou cannot destroy " + inputStr, '&');
			Utils.sendMessage(sender, "", "&c&nSneak&c and break or set the timeout to 0 if you want to remove it.",
					'&');
			event.setCancelled(true);
			return;
		}
		if (sender.hasPermission("damnspam.admin") || sender.getUniqueId().toString().equals(input.player))
		{
			inputs.remove(posStr);
			saveInputs();
			Utils.sendMessage(sender, null, "&aSuccesfully removed " + inputStr, '&');
		}
		else
		{
			Utils.sendMessage(sender, null, "&cYou are not allowed to remove " + inputStr, '&');
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Block> getAttachedBlocks(Block block)
	{
		List<Block> blocks = new ArrayList<Block>();
		BlockFace[] directions = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
				BlockFace.EAST};
		for (int i = 0; i < directions.length; i++)
		{
			Block side = block.getRelative(directions[i]);
			int[][] dvalues = attachedBlocks.get(side.getType());
			if (dvalues != null)
			{
				boolean onSide = false;
				for (int val : dvalues[i])
				{
					if (side.getData() == (byte) val)
					{
						onSide = true;
						break;
					}
				}
				if (onSide)
					blocks.add(side);
			}
		}
		return blocks;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBreak(BlockBreakEvent event)
	{
		if (changingInput || event.isCancelled())
			return;
		boolean register = attemptInputRegister(event.getPlayer(), event.getBlock(), event);
		if (!register)
		{
			Block block = event.getBlock();
			checkBlockBreak(event, block);
			for (Block affected : getAttachedBlocks(block))
			{
				checkBlockBreak(event, affected);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event)
	{
		boolean register = attemptInputRegister(event.getPlayer(), event.getClickedBlock(), event);
		if (!register && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.isCancelled())
		{
			Player sender = event.getPlayer();
			Block block = event.getClickedBlock();
			String posStr = locationString(block.getLocation());
			SpamInput data = inputs.get(posStr);
			if (data != null)
			{
				String btype = block.getType().toString().toLowerCase().replace("_", " ");
				double checktime = 0;
				if (btype.equals("lever") && block.getData() < 8)
					checktime = data.timeoutOff;
				else
					checktime = data.timeoutOn;
				double timeLeft = (data.lastTime + checktime)
						- ((double) Math.round((double) System.currentTimeMillis() / 10) / 100);
				timeLeft = (double) Math.round(timeLeft * 100) / 100;
				if (checktime == -1)
				{
					event.setCancelled(true);
					Utils.sendMessage(sender, null, "&cThis " + btype + " is locked permanently by /damnspam.", '&');
				}
				else if (timeLeft > 0)
				{
					event.setCancelled(true);
					Utils.sendMessage(sender, null, "&cThis " + btype + " has a damnspam timeout of " + checktime
							+ ", with " + timeLeft + " left.", '&');
				}
				else
				{
					data.lastTime = (double) Math.round((double) System.currentTimeMillis() / 10) / 100;
				}
				inputs.put(posStr, data);
			}
		}
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command damnspam {\n" + 
				"	perm utils.damnspam;\n" + 
				"	\n" + 
				"	[double:seconds] {\n" + 
				"		run damnspamSingle seconds;\n" + 
				"		help Set single input cooldown for button or lever.;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"	\n" + 
				"	[double:secondsOff] [double:secondsOn] {\n" + 
				"		run damnspamDouble secondsOff secondsOn;\n" + 
				"		help Set input cooldown after it's been turned off and turned on (for lever only).;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
}
