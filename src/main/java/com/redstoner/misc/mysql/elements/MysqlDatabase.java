package com.redstoner.misc.mysql.elements;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.redstoner.misc.mysql.MysqlQueryHandler;

public class MysqlDatabase {
	private Connection connection;
	
	public MysqlDatabase(Connection connection) {
		this.connection = connection;
	}
	
	public String getName() {
		try {
			return connection.getCatalog();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public MysqlTable getTable(String name) {
		return new MysqlTable(this, name);
	}
	
	public boolean createTable(String name, MysqlField... description) {
		return MysqlQueryHandler.queryNoResult(connection, "CREATE TABLE `" + name + "` " + getDescription(description) + ";");
	}
	
	public boolean createTableIfNotExists(String name, MysqlField... description) {
		return MysqlQueryHandler.queryNoResult(connection, "CREATE TABLE IF NOT EXISTS `" + name + "` " + getDescription(description) + ";");
	}
	
	public boolean dropTable(String name) {
		return MysqlQueryHandler.queryNoResult(connection, "DROP TABLE `" + name + "`;");
	}
	
	public boolean drop() {
		return MysqlQueryHandler.queryNoResult(connection, "DROP DATABASE `" + getName() + "`;");
	}
	
	public List<MysqlTable> getTables() {
		try {
			List<MysqlTable> tables = new ArrayList<>();
			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet queryResults = metadata.getTables(null, null, "%", null);
			
			while (queryResults.next()) {
				tables.add(new MysqlTable(this, queryResults.getString(3)));
			}
			
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Connection getConnection() {
		return connection;
	}
	
	private String getDescription(MysqlField... description) {
		String desc = "(";
		
		for (int i = 0; i < description.length; i++) {
			String nil = "";
			
			if (description[i].canBeNull()) {
				nil = " NOT NULL";
			}
			
			desc += "`" + description[i].getName() + "` " + description[i].getType().getName() + nil;
			
			if (i < description.length - 1) {
				desc += ",";
			}
		}
		
		desc += ")";
		
		return desc;
	}
}
