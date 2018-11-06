package com.redstoner.misc.mysql.elements;

public class MysqlConstraint {
	private String fieldName, value;
	private ConstraintOperator operator;
	
	public MysqlConstraint(String fieldName, ConstraintOperator operator, String value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getValue() {
		return value;
	}

	public ConstraintOperator getOperator() {
		return operator;
	}
}
