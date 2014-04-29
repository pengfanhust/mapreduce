package hw4.hpopulate;

import hw4.Constants;
import hw4.plain.Record;
import hw4.plain.RecordKey;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import au.com.bytecode.opencsv.CSVParser;

public class HPopulate {
	private static final int INDEX_YEAR = 0;
	private static final int INDEX_MONTH = 2;
	private static final int INDEX_AIR_LINE = 6;
	private static final int INDEX_ARR_DEALY_MIN = 37;
	private static final int INDEX_CANCELLED = 41;
	private static final int INDEX_DIVERTED = 43;

	private static final CSVParser csvParser = new CSVParser();

	public static class HPopulateMapper extends
			Mapper<Object, Text, RecordKey, Record> {

		Configuration config;
		HTable table;
		List<Put> puts;

		protected void setup(Context context) throws IOException,
				InterruptedException {
			config = HBaseConfiguration.create();
			table = new HTable(config, Constants.TBL_NAME);
			table.setAutoFlush(false);
			table.setWriteBufferSize(1024 * 1024 * 50);
			puts = new LinkedList<Put>();
		}

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] line = csvParser.parseLine(value.toString());
			String year = line[INDEX_YEAR];
			String month = line[INDEX_MONTH];
			String airLine = line[INDEX_AIR_LINE];
			String delayMin = line[INDEX_ARR_DEALY_MIN];
			String cancelled = line[INDEX_CANCELLED];
			String diverted = line[INDEX_DIVERTED];

			// put record
			byte[] row = Bytes.toBytes(airLine.concat(month).concat(
					key.toString()));
			// Put value value1 into column data:1
			Put p1 = new Put(row);
			p1.add(Constants.COL_FNAME, Constants.COL_YEAR, Bytes.toBytes(year));
			p1.add(Constants.COL_FNAME, Constants.COL_MONTH,
					Bytes.toBytes(month));
			p1.add(Constants.COL_FNAME, Constants.COL_AIR_LINE,
					Bytes.toBytes(airLine));
			p1.add(Constants.COL_FNAME, Constants.COL_ARR_DELAY_MIN,
					Bytes.toBytes(delayMin));
			p1.add(Constants.COL_FNAME, Constants.COL_CANCELLED,
					Bytes.toBytes(cancelled));
			p1.add(Constants.COL_FNAME, Constants.COL_DIVERTED,
					Bytes.toBytes(diverted));
			p1.setWriteToWAL(false);
			puts.add(p1);
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			table.put(puts);
			table.close();
		}
	}

	public static void main(String[] args) throws Exception {
		Date begin = new Date();
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();

		if (otherArgs.length != 1) {
			System.err.println("Usage: flight delay avg <in>");
			System.exit(2);
		}

		// Create table
		Configuration config = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(config);

		if (admin.tableExists(Constants.TBL_NAME)) {
			admin.disableTable(Constants.TBL_NAME);
			admin.deleteTable(Constants.TBL_NAME);
		}
		HTableDescriptor htd = new HTableDescriptor(Constants.TBL_NAME);
		htd.setDeferredLogFlush(true);
		HColumnDescriptor hcd = new HColumnDescriptor(Constants.COL_FNAME);
		htd.addFamily(hcd);
		admin.createTable(htd);
		admin.close();

		Date middle = new Date();
		// filter all the valid leg1-leg2 pair and calculate the
		// delay time for each pair.
		Job job = new Job(config, "flight delay hpopulate");
		job.setJarByClass(HPopulate.class);
		job.setMapperClass(HPopulateMapper.class);
		job.setNumReduceTasks(0);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		job.setOutputFormatClass(NullOutputFormat.class);
		job.waitForCompletion(true);
		Date end = new Date();
		System.out.println(begin);
		System.out.println(middle);
		System.out.println(end);
	}
}
