package com.redstoner.misc.mysql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.redstoner.misc.Main;

public class JSONManager
{
	public static Map<Serializable, Serializable> getConfiguration(String fileName)
	{
		File file = new File(Main.plugin.getDataFolder(), fileName);
		if (!file.exists())
		{
			try
			{
				PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
				writer.println("{}");
				writer.close();
			}
			catch (FileNotFoundException | UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			return loadMap(file);
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void saveConfiguration(Map<Serializable, Serializable> config, String fileName)
	{
		try
		{
			saveMap(new File(Main.plugin.getDataFolder(), fileName), config);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void saveList(File file, List<Serializable> entries) throws IOException
	{
		JSONArray array = new JSONArray();
		array.addAll(entries);
		FileWriter writer = new FileWriter(file);
		writer.write(array.toJSONString());
		writer.close();
	}
	
	public static List<Serializable> loadList(File file) throws IOException, ParseException
	{
		FileReader read = new FileReader(file);
		List<Serializable> entries = new ArrayList<>();
		JSONArray array = (JSONArray) new JSONParser().parse(read);
		for (Object o : array)
		{
			entries.add((Serializable) o);
		}
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	public static void saveMap(File file, Map<Serializable, Serializable> entries) throws IOException
	{
		JSONObject map = new JSONObject();
		map.putAll(entries);
		FileWriter writer = new FileWriter(file);
		writer.write(map.toJSONString());
		writer.close();
	}
	
	public static Map<Serializable, Serializable> loadMap(File file) throws IOException, ParseException
	{
		FileReader reader = new FileReader(file);
		JSONObject map = (JSONObject) new JSONParser().parse(reader);
		Map<Serializable, Serializable> entries = new HashMap<>();
		for (Object o : map.keySet())
		{
			entries.put((Serializable) o, (Serializable) map.get(o));
		}
		return entries;
	}
}
