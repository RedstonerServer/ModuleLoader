package com.redstoner.misc.mysql.types;

import com.redstoner.misc.mysql.types.date.Date;
import com.redstoner.misc.mysql.types.date.DateTime;
import com.redstoner.misc.mysql.types.date.Time;
import com.redstoner.misc.mysql.types.date.TimeStamp;
import com.redstoner.misc.mysql.types.date.Year;
import com.redstoner.misc.mysql.types.number.BigInt;
import com.redstoner.misc.mysql.types.number.Decimal;
import com.redstoner.misc.mysql.types.number.Double;
import com.redstoner.misc.mysql.types.number.Float;
import com.redstoner.misc.mysql.types.number.Int;
import com.redstoner.misc.mysql.types.number.MediumInt;
import com.redstoner.misc.mysql.types.number.SmallInt;
import com.redstoner.misc.mysql.types.number.TinyInt;
import com.redstoner.misc.mysql.types.text.Blob;
import com.redstoner.misc.mysql.types.text.Char;
import com.redstoner.misc.mysql.types.text.Enum;
import com.redstoner.misc.mysql.types.text.LongBlob;
import com.redstoner.misc.mysql.types.text.LongText;
import com.redstoner.misc.mysql.types.text.MediumBlob;
import com.redstoner.misc.mysql.types.text.MediumText;
import com.redstoner.misc.mysql.types.text.Set;
import com.redstoner.misc.mysql.types.text.Text;
import com.redstoner.misc.mysql.types.text.TinyText;
import com.redstoner.misc.mysql.types.text.VarChar;

public abstract class MysqlType
{
	public abstract String getName();
	
	public static MysqlType getTypeFromString(String type)
	{
		String[] splitType = type.split("\\(");
		String toSwitch = splitType[0].toUpperCase();
		String value = "";
		if (type.contains("(") && type.endsWith(")"))
		{
			value = splitType[1].substring(0, splitType[1].length() - 1);
		}
		switch (toSwitch)
		{
			case "CHAR":
				return new Char(Integer.valueOf(value));
			case "ENUM":
				return new Enum(value.replaceAll("'", "").split(","));
			case "VARCHAR":
				return new VarChar(Integer.valueOf(value));
			case "SET":
				return new Set(value.replaceAll("'", "").split(","));
			case "BLOB":
				return new Blob();
			case "TEXT":
				return new Text();
			case "MEDIUMBLOB":
				return new MediumBlob();
			case "LONGBLOB":
				return new LongBlob();
			case "TINYTEXT":
				return new TinyText();
			case "MEDIUMTEXT":
				return new MediumText();
			case "LONGTEXT":
				return new LongText();
			case "INT":
				return new Int(Integer.valueOf(value));
			case "TINYINT":
				return new TinyInt(Integer.valueOf(value));
			case "SMALLINT":
				return new SmallInt(Integer.valueOf(value));
			case "MEDIUMINT":
				return new MediumInt(Integer.valueOf(value));
			case "BIGINT":
				return new BigInt(Integer.valueOf(value));
			case "BIT":
				return new TinyInt(1);
			case "FLOAT":
				return new Float();
			case "DOUBLE":
				return new Double();
			case "DECIMAL":
				return new Decimal();
			case "DATE":
				return new Date();
			case "DATETIME":
				return new DateTime();
			case "TIME":
				return new Time();
			case "TIMESTAMP":
				return new TimeStamp();
			case "YEAR":
				return new Year();
		}
		return null;
	}
}
