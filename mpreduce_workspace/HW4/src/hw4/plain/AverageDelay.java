package hw4.plain;

import hw4.Constants;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import au.com.bytecode.opencsv.CSVParser;

public class AverageDelay {

	private static final int INDEX_YEAR = 0;
	private static final int INDEX_MONTH = 2;
	private static final int INDEX_AIR_LINE = 6;
	private static final int INDEX_ARR_DEALY_MIN = 37;
	private static final int INDEX_CANCELLED = 41;
	private static final int INDEX_DIVERTED = 43;
	private static final CSVParser csvParser = new CSVParser();

	public static class AverageDelayMapper extends
			Mapper<Object, Text, RecordKey, Record> {
		private Map<RecordKey, Record> temp;

		protected void setup(Context context) throws IOException,
				InterruptedException {
			temp = new HashMap<RecordKey, Record>();
		}

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] line = csvParser.parseLine(value.toString());
			// head line
			if (line[0].equals("Year"))
				return;

			// year is not be 2008
			if (!line[INDEX_YEAR].equals(Constants.YEAR))
				return;

			// has be cancelled or diverted
			if (!(line[INDEX_CANCELLED].equals(Constants.N_CANCELLED) && line[INDEX_DIVERTED]
					.equals(Constants.N_DIVERTED)))
				return;

			RecordKey k = new RecordKey();
			k.set(line[INDEX_AIR_LINE], Integer.parseInt(line[INDEX_MONTH]));

			Record v = temp.get(k);
			if (v == null) {
				v = new Record();
				v.set(Integer.parseInt(line[INDEX_MONTH]));
				temp.put(k, v);
			}
			v.add(Float.parseFloat(line[INDEX_ARR_DEALY_MIN]));
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			for (Map.Entry<RecordKey, Record> e : temp.entrySet()) {
				context.write(e.getKey(), e.getValue());
			}
		}
	}

	public static class FlightDelayFilterReducer extends
			Reducer<RecordKey, Record, Text, Text> {
		private Text k = new Text();

		public void reduce(RecordKey key, Iterable<Record> values,
				Context context) throws IOException, InterruptedException {
			float arr[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			long count[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			for (Record r : values) {
				arr[r.getFlightMonth() - 1] = arr[r.getFlightMonth() - 1]
						+ r.getArrDelayMinSum();
				count[r.getFlightMonth() - 1] = count[r.getFlightMonth() - 1]
						+ r.getArrDelayMinCount();
			}
			String c = key.getAirLineName();
			for (int i = 0; i < arr.length; i++) {
				int avg;
				if (count[i] == 0) {
					avg = 0;
				} else {
					avg = Math.round(arr[i] / count[i]);
				}
				// float avg = arr[i] / count[i];
				c = c.concat(",(").concat(String.valueOf(i + 1)).concat(",")
						.concat(String.valueOf(avg)).concat(")");
			}
			k.set(c);
			context.write(null, k);
		}
	}

	public static void main(String[] args) throws Exception {
		Date begin = new Date();
		Configuration confFilter = new Configuration();
		String[] otherArgs = new GenericOptionsParser(confFilter, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: flight delay avg <in> <out>");
			System.exit(2);
		}

		Job job = new Job(confFilter, "flight delay");
		job.setJarByClass(AverageDelay.class);
		job.setGroupingComparatorClass(GroupingComparator.class);
		job.setMapperClass(AverageDelayMapper.class);
		job.setReducerClass(FlightDelayFilterReducer.class);

		job.setMapOutputKeyClass(RecordKey.class);
		job.setMapOutputValueClass(Record.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(10);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		job.waitForCompletion(true);
		Date end = new Date();
		System.out.println(begin);
		System.out.println(end);
	}
}