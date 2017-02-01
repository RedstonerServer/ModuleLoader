package com.redstoner.misc.mysql.elements;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.redstoner.misc.mysql.MysqlQueryHandler;

public class MysqlTable {
	private MysqlDatabase database;
	private String name;
	
	public MysqlTable(MysqlDatabase database, String name) {
		this.database = database;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public MysqlField[] describe() {
		try {
			List<MysqlField> description = new ArrayList<>();
			DatabaseMetaData metadata = database.getConnection().getMetaData();
			ResultSet queryResults = metadata.getColumns(null, null, name, null);
			
			while (queryResults.next()) {
				description.add(new MysqlField(queryResults.getString(4), queryResults.getString(6).split(" ")[0] + "(" + queryResults.getString(7) + ")", queryResults.getBoolean(11)));
			}
			
			return description.toArray(new MysqlField[0]);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean insert(String... values) {
		MysqlField[] description = describe();
		
		if (values.length > 0 && values.length == description.length) {
			String val = "(\"" + String.join("\",\"", values) + "\")";
			
			return MysqlQueryHandler.queryNoResult(database.getConnection(), "INSERT INTO `" + name + "` VALUES " + val + ";");
		} else {
			return false;
		}
	}
	
	public Object[] get(String fieldName, MysqlConstraint... constraints) {
		ResultSet results = MysqlQueryHandler.queryResult(database.getConnection(), "SELECT " + fieldName + " FROM `" + name + "`" + getConstraints(constraints) + ";");
		
		List<Object> resObj = new ArrayList<>();
		try {
			while (results.next()) {
				resObj.add(results.getObject(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return new Object[0];
		}
		
		return resObj.toArray(new Object[0]);
	}
	
	public boolean delete(MysqlConstraint... constraints) {
		return MysqlQueryHandler.queryNoResult(database.getConnection(), "DELETE FROM `" + name + "`" + getConstraints(constraints) + ";");
	}
	
	public boolean drop() {
		return MysqlQueryHandler.queryNoResult(database.getConnection(), "DROP TABLE `" + name + "`;");
	}
	
	private String getConstraints(MysqlConstraint... constraints) {
		String cons = "";
		
		if (constraints.length > 0) {
			cons += " WHERE ";
			
			for (int i = 0; i < constraints.length; i++) {
				MysqlConstraint constraint = constraints[i];
				
				cons += constraint.getFieldName() + constraint.getOperator().toString() + "\"" + constraint.getValue() + "\"";
				
				if (i < constraints.length - 1) {
					cons += " AND ";
				}
			}
		}
		
		return cons;
	}
}
