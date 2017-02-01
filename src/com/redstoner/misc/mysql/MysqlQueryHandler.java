package com.redstoner.misc.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlQueryHandler {
	public static ResultSet queryResult(Connection connection, String query) {
		try {
			Statement statement = connection.createStatement();
			ResultSet results = statement.executeQuery(query);
			
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean queryNoResult(Connection connection, String query) {
		try {
			CallableStatement statement = connection.prepareCall(query);
			statement.execute();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
