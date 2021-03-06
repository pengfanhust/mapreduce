package hw3;

import hw3.FlightDelayAverager.FlightDelayMapper;
import hw3.FlightDelayAverager.FlightDelayReducer;
import hw3.FlightDelayFilter.FlightDelayFilterMapper;
import hw3.FlightDelayFilter.FlightDelayFilterReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class FlightDelay {
	public static void main(String []args) throws Exception {
		Configuration confFilter = new Configuration();
		String[] otherArgs = new GenericOptionsParser(confFilter, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: flight delay avg <in> <intermediaout> <out>");
			System.exit(2);
		}
		
		// filter all the valid leg1-leg2 pair and calculate the 
		// delay time for each pair.
		Job jobFilter = new Job(confFilter, "flight delay filter");
		jobFilter.setJarByClass(FlightDelayFilter.class);
		jobFilter.setMapperClass(FlightDelayFilterMapper.class);
		jobFilter.setReducerClass(FlightDelayFilterReducer.class);

		jobFilter.setMapOutputKeyClass(FlightRecordKey.class);
		jobFilter.setMapOutputValueClass(FlightRecord.class);

		jobFilter.setOutputKeyClass(Text.class);
		jobFilter.setOutputValueClass(FloatWritable.class);

		jobFilter.setNumReduceTasks(10);
		FileInputFormat.addInputPath(jobFilter, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(jobFilter, new Path(otherArgs[1]));

		jobFilter.waitForCompletion(true);
		
		// calculate the average delay time
		Configuration confAvg = new Configuration();
		Job jobAvg = new Job(confAvg, "flight delay avg");
		jobAvg.setJarByClass(FlightDelayAverager.class);
		jobAvg.setMapperClass(FlightDelayMapper.class);
		jobAvg.setReducerClass(FlightDelayReducer.class);

		jobAvg.setOutputKeyClass(Text.class);
		jobAvg.setOutputValueClass(FloatWritable.class);

		jobAvg.setNumReduceTasks(1);
		FileInputFormat.addInputPath(jobAvg, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(jobAvg, new Path(otherArgs[2]));
		jobAvg.waitForCompletion(true);
	}
}
