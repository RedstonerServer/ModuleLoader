package com.redstoner.misc.mysql;

import java.sql.*;

public class MysqlQueryHandler {
	public static ResultSet queryResult(Connection connection, String query) {
		try {
			Statement statement = connection.createStatement();
			ResultSet results   = statement.executeQuery(query);

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
