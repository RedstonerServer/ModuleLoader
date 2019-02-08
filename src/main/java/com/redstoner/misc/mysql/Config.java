package com.redstoner.misc.mysql;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.redstoner.exceptions.NonSaveableConfigException;
import com.redstoner.misc.Main;

public class Config
{
	private File file;
	private JSONObject config;
	private JSONParser parser;
	
	public Config()
	{
		file = null;
		parser = new JSONParser();
		config = new JSONObject();
	}
	
	public Config(JSONObject config)
	{
		this.file = null;
		this.parser = new JSONParser();
		this.config = config;
	}
	
	private Config(File file) throws IOException, ParseException
	{
		this.file = file;
		parser = new JSONParser();
		if (file.exists())
		{
			config = loadConfig(file);
		}
		else
		{
			config = new JSONObject();
		}
	}
	
	public static final Config getConfig(String fileName) throws IOException, ParseException
	{
		return new Config(new File(Main.plugin.getDataFolder(), fileName));
	}
	
	public static final Config getConfig(File file) throws IOException, ParseException
	{
		return new Config(file);
	}
	
	private JSONObject loadConfig(File file) throws IOException, ParseException
	{
		FileReader reader = new FileReader(file);
		JSONObject object = (JSONObject) parser.parse(reader);
		reader.close();
		return object;
	}
	
	@Override
	public String toString()
	{
		return config.toJSONString();
	}
	
	public JSONObject asObject()
	{
		return config;
	}
	
	public void save() throws IOException, NonSaveableConfigException
	{
		if (file == null)
		{
			throw new NonSaveableConfigException();
		}
		PrintWriter writer = new PrintWriter(file);
		writer.write(config.toJSONString());
		writer.close();
	}
	
	public void refresh() throws IOException, ParseException, NonSaveableConfigException
	{
		if (file == null)
		{
			throw new NonSaveableConfigException();
		}
		loadConfig(file);
	}
	
	public void setFile(String fileName)
	{
		file = new File(Main.plugin.getDataFolder(), fileName);
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	@SuppressWarnings("unchecked")
	public void put(String key, String value)
	{
		config.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public void put(String key, List<String> value)
	{
		JSONArray array = new JSONArray();
		for (String entry : value)
		{
			array.add(entry);
		}
		config.put(key, array);
	}
	
	@SuppressWarnings("unchecked")
	public void putArray(String key, JSONArray value)
	{
		config.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public void put(String key, Map<String, String> value)
	{
		JSONObject object = new JSONObject();
		for (String valKey : value.keySet())
		{
			String valVal = value.get(valKey);
			object.put(valKey, valVal);
		}
		config.put(key, object);
	}
	
	@SuppressWarnings("unchecked")
	public void put(String key, JSONObject value)
	{
		config.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public void putAll(Map<String, String> entry)
	{
		for (String key : entry.keySet())
		{
			String value = entry.get(key);
			config.put(key, value);
		}
	}
	
	public boolean containsKey(String key)
	{
		return config.containsKey(key);
	}
	
	public String get(String key)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof String)
			{
				return (String) value;
			}
		}
		return null;
	}
	
	public String getOrDefault(String key, String defaultValue)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof String)
			{
				return (String) value;
			}
			return null;
		}
		else
		{
			return defaultValue;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getList(String key)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof JSONArray)
			{
				JSONArray array = (JSONArray) value;
				List<String> output = new ArrayList<String>();
				for (String entry : (String[]) array.toArray(new String[0]))
				{
					output.add(entry);
				}
				return output;
			}
		}
		return null;
	}
	
	public JSONArray getArray(String key)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof JSONArray)
			{
				JSONArray array = (JSONArray) value;
				return array;
			}
		}
		return null;
	}
	
	public Map<String, String> getMap(String key)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof JSONObject)
			{
				JSONObject object = (JSONObject) value;
				@SuppressWarnings("unchecked")
				Set<Map.Entry<String, String>> entrySet = object.entrySet();
				Map<String, String> output = new HashMap<String, String>();
				for (Map.Entry<String, String> entry : entrySet)
				{
					output.put(entry.getKey(), entry.getValue());
				}
				return output;
			}
		}
		return null;
	}
	
	public JSONObject getObject(String key)
	{
		if (containsKey(key))
		{
			Object value = config.get(key);
			if (value instanceof JSONObject)
			{
				JSONObject object = (JSONObject) value;
				return object;
			}
		}
		return null;
	}
	
	public void remove(String key)
	{
		config.remove(key);
	}
	
	@SuppressWarnings("unchecked")
	public Set<Entry<String, String>> getAll()
	{
		return config.entrySet();
	}
}
