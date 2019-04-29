package com.redstoner.misc.mysql.elements;

import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.MysqlQueryHandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MysqlDatabase {
	private Connection connection = null;

	private final MysqlHandler handler;
	private final String databaseName;

	public MysqlDatabase(MysqlHandler handler, String databaseName) {
		this.handler = handler;
		this.databaseName = databaseName;
	}

	public MysqlTable getTable(String name) {
		return new MysqlTable(this, name);
	}

	public boolean createTable(String name, MysqlField... description) {
		return MysqlQueryHandler.queryNoResult(getConnection(), "CREATE TABLE `" + name + "` " + getDescription(description) + ";");
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

	public boolean createTableIfNotExists(String name, MysqlField... description) {
		return MysqlQueryHandler.queryNoResult(getConnection(), "CREATE TABLE IF NOT EXISTS `" + name + "` " + getDescription(description) + ";");
	}

	public boolean dropTable(String name) {
		return MysqlQueryHandler.queryNoResult(getConnection(), "DROP TABLE `" + name + "`;");
	}

	public boolean drop() {
		return MysqlQueryHandler.queryNoResult(getConnection(), "DROP DATABASE `" + getName() + "`;");
	}

	public String getName() {
		try {
			return getConnection().getCatalog();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<MysqlTable> getTables() {
		try {
			List<MysqlTable> tables       = new ArrayList<>();
			DatabaseMetaData metadata     = getConnection().getMetaData();
			ResultSet        queryResults = metadata.getTables(null, null, "%", null);

			while (queryResults.next()) {
				tables.add(new MysqlTable(this, queryResults.getString(3)));
			}

			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = handler.getConnection(databaseName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection = handler.getConnection(databaseName);
		}

		return connection;
	}
}
