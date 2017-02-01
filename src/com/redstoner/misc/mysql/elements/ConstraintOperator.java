package com.redstoner.misc.mysql.elements;

public enum ConstraintOperator {
	LESS_THAN, GREATER_THAN, EQUAL, NOT_EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL;
	
	public String toString() {
		switch (this) {
			case LESS_THAN:
				return "<";
			case GREATER_THAN:
				return ">";
			case EQUAL:
				return "=";
			case NOT_EQUAL:
				return "!=";
			case LESS_THAN_OR_EQUAL:
				return "<=";
			case GREATER_THAN_OR_EQUAL:
				return ">=";
			default:
				return "=";
		}
	}
}
