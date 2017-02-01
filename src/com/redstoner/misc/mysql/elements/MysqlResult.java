package com.redstoner.misc.mysql.elements;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlResult {
	private ResultSet results;
	
	public MysqlResult(ResultSet results) {
		this.results = results;
	}
	
	public Object getObject(int columnIndex, Class<?> type) throws SQLException {
		return results.getObject(columnIndex, type);
	}
}
