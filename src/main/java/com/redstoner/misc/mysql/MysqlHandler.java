package com.redstoner.misc.mysql;

import com.redstoner.misc.Main;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlHandler {
	public static MysqlHandler INSTANCE;
	private       String       url, username, password;

	public MysqlHandler(String hostname, int port, String username, String password) {
		this.url = "jdbc:mysql://" + hostname + ":" + port + "/";
		this.username = username;
		this.password = password;
	}

	public static void init() {
		Map<Serializable, Serializable> mysqlCredentials     = new HashMap<>();
		File                            mysqlCredentialsFile = new File(Main.plugin.getDataFolder(), "mysqlCredentials.json");
		if (mysqlCredentialsFile.exists()) {
			try {
				mysqlCredentials = JSONManager.loadMap(mysqlCredentialsFile);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "MySQL config does not exist, creating an example one, things might (will) break!");
			mysqlCredentials.put("hostname", "localhost");
			mysqlCredentials.put("port", "3306");
			mysqlCredentials.put("username", "your username here");
			mysqlCredentials.put("password", "your password here");
			try {
				JSONManager.saveMap(mysqlCredentialsFile, mysqlCredentials);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String hostname = (String) mysqlCredentials.get("hostname");
		int    port     = Integer.valueOf((String) mysqlCredentials.get("port"));
		String username = (String) mysqlCredentials.get("username");
		String password = (String) mysqlCredentials.get("password");
		INSTANCE = new MysqlHandler(hostname, port, username, password);
	}

	public MysqlDatabase getDatabase(String databaseName) {
		return new MysqlDatabase(this, databaseName);
	}

	public Connection getConnection(String databaseName) throws IllegalStateException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url + databaseName, username, password);
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect to the database!", e);
		}
		return connection;
	}

	public List<MysqlDatabase> getDatabases() {
		try {
			List<MysqlDatabase> databases    = new ArrayList<>();
			Connection          connection   = DriverManager.getConnection(url.substring(0, url.length()), username, password);
			DatabaseMetaData    metadata     = connection.getMetaData();
			ResultSet           queryResults = metadata.getCatalogs();
			while (queryResults.next()) {
				String databaseName = queryResults.getString("TABLE_CAT");
				databases.add(new MysqlDatabase(this, databaseName));
			}
			connection.close();
			return databases;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
