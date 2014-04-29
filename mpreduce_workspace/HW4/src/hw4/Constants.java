package hw4;

import org.apache.hadoop.hbase.util.Bytes;

public class Constants {
	public static final String TBL_NAME = "flighttbl";
	public static final byte[] COL_FNAME = Bytes.toBytes("data");

	public static final byte[] COL_YEAR = Bytes.toBytes("y");
	public static final byte[] COL_MONTH = Bytes.toBytes("m");
	public static final byte[] COL_AIR_LINE = Bytes.toBytes("al");
	public static final byte[] COL_ARR_DELAY_MIN = Bytes.toBytes("dly");
	public static final byte[] COL_CANCELLED = Bytes.toBytes("c");
	public static final byte[] COL_DIVERTED = Bytes.toBytes("d");
	

	public static final String YEAR = "2008";
	public static final String N_CANCELLED = "0.00";
	public static final String N_DIVERTED = "0.00";
}