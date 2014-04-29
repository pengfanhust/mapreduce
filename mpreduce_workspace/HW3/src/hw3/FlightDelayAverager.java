package hw3;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * simple job, used to calculate the average.
 * @author pengfan
 *
 */
public class FlightDelayAverager {

	public static class FlightDelayMapper extends
			Mapper<Object, Text, Text, FloatWritable> {
		private Text t = new Text("dummy");
		private FloatWritable f = new FloatWritable();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			f.set(Float.valueOf(value.toString()));
			context.write(t, f);

		}
	}

	public static class FlightDelayReducer extends
			Reducer<Text, FloatWritable, Text, FloatWritable> {

		public void reduce(Text key, Iterable<FloatWritable> values,
				Context context) throws IOException, InterruptedException {
			int count = 0;
			double sum = 0;
			for (FloatWritable f : values) {
				count++;
				sum = sum + f.get();
			}
			context.write(null, new FloatWritable((float) (sum / count)));
		}
	}
}
