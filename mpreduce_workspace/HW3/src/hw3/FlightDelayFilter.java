package hw3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import au.com.bytecode.opencsv.CSVParser;

/**
 * Between june 2007 and may 2008 Year 0 Month 2
 * 
 * FlightDate 5 Origin: ORD 11 Dest: JFK 17 DepTime 24 ArrTime 35
 * ArrDelayMinutes 37 Cancelled 41 Diverted 43
 * 
 * @author pengfan
 * 
 */
public class FlightDelayFilter {

	private static final int INDEX_YEAR = 0;
	private static final int INDEX_MONTH = 2;
	private static final int INDEX_FLIGHTDATE = 5;
	private static final int INDEX_ORIGIN = 11;
	private static final int INDEX_DEST = 17;
	private static final int INDEX_DEPTIME = 24;
	private static final int INDEX_ARRTIME = 35;
	private static final int INDEX_ARR_DEALY_MIN = 37;
	private static final int INDEX_CANCELLED = 41;
	private static final int INDEX_DIVERTED = 43;

	private static final int YEAR_MIN = 2007;
	private static final int MONTH_MIN = 6;
	private static final int YEAR_MAX = 2008;
	private static final int MONTH_MAX = 5;
	private static final String ORIG = "ORD";
	private static final String DES = "JFK";
	private static final CSVParser csvParser = new CSVParser();

	public static class FlightDelayFilterMapper extends
			Mapper<Object, Text, FlightRecordKey, FlightRecord> {

		/**
		 * Map used to filter the records.
		 */
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] line = csvParser.parseLine(value.toString());
			// skip the head line
			if (isHead(line)) {
				return;
			}
			if (satisifyDate(line[INDEX_YEAR], line[INDEX_MONTH])
					&& satisifyStatus(line[INDEX_CANCELLED],
							line[INDEX_DIVERTED])) {
				// the flight is between 2007-06 and 2008-05
				// and have not been cancelled or diverted
				String flightType = getFlightType(line[INDEX_ORIGIN],
						line[INDEX_DEST]);
				if (flightType.equals(FlightRecord.FLIGHT_TYPE_L1)) {
					// leg1 , we only care about the dest location and the arrive time
					FlightRecordKey k = new FlightRecordKey(line[INDEX_DEST],
							line[INDEX_FLIGHTDATE]);
					FlightRecord v = new FlightRecord(
							line[INDEX_ARR_DEALY_MIN], line[INDEX_ARRTIME],
							flightType);
					context.write(k, v);
				} else if (flightType.equals(FlightRecord.FLIGHT_TYPE_L2)) {
					// leg2 , we only care about the origin location and the depart time
					FlightRecordKey k = new FlightRecordKey(line[INDEX_ORIGIN],
							line[INDEX_FLIGHTDATE]);
					FlightRecord v = new FlightRecord(
							line[INDEX_ARR_DEALY_MIN], line[INDEX_DEPTIME],
							flightType);
					context.write(k, v);
				}
			}
		}

		private boolean isHead(String[] line) {
			return line[0].equals("Year");
		}

		private String getFlightType(String orig, String dest) {
			if (orig.equals(ORIG) && !dest.equals(DES)) {
				return FlightRecord.FLIGHT_TYPE_L1;
			}
			if (!orig.equals(ORIG) && dest.equals(DES)) {
				return FlightRecord.FLIGHT_TYPE_L2;
			}
			return FlightRecord.FLIGHT_TYPE_UNVALID;
		}

		private boolean satisifyDate(String y, String m) {
			int year = Integer.valueOf(y).intValue();
			int month = Integer.valueOf(m).intValue();
			if (year == YEAR_MIN) {
				return month >= MONTH_MIN;
			} else if (year == YEAR_MAX) {
				return month <= MONTH_MAX;
			} else if (YEAR_MIN < year && year < YEAR_MAX) {
				return true;
			}
			return false;
		}

		private boolean satisifyStatus(String cancelled, String diverted) {
			return cancelled.equals("0.00") && diverted.equals("0.00");
		}

	}

	public static class FlightDelayFilterReducer extends
			Reducer<FlightRecordKey, FlightRecord, Text, FloatWritable> {
		private FloatWritable v = new FloatWritable();

		/**
		 * Reducer used to join the records
		 */
		public void reduce(FlightRecordKey key, Iterable<FlightRecord> values,
				Context context) throws IOException, InterruptedException {
			List<FlightRecord> l1s = new LinkedList<FlightRecord>();
			List<FlightRecord> l2s = new LinkedList<FlightRecord>();
			
			// separate the leg1 and leg2 records.
			for (FlightRecord r : values) {
				if (r.getFlightType().equals(FlightRecord.FLIGHT_TYPE_L1)) {
					l1s.add(new FlightRecord(r.getFlightDelayTime(), r
							.getFlightTime(), r.getFlightType()));
				} else {
					l2s.add(new FlightRecord(r.getFlightDelayTime(), r
							.getFlightTime(), r.getFlightType()));
				}
			}

			// find the match leg1-leg2 pair
			for (FlightRecord rl1 : l1s) {
				for (FlightRecord rl2 : l2s) {
					if (Integer.valueOf(rl1.getFlightTime()) < Integer
							.valueOf(rl2.getFlightTime())) {
						v.set(Float.valueOf(rl1.getFlightDelayTime())
								+ Float.valueOf(rl2.getFlightDelayTime()));
						context.write(null, v);
					}
				}
			}
		}
	}
}
