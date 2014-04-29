package hadoop.wordcount;

import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.io.Text;

public class MyPartitioner<VALUE> extends Partitioner<Text, VALUE> {

	@Override
	public int getPartition(Text key, VALUE value, int numPartitions) {
		int p = -1;
		if (key.toString().toLowerCase().startsWith("m")) {
			p = 0;
		} else if (key.toString().toLowerCase().startsWith("n")) {
			p = 1;
		} else if (key.toString().toLowerCase().startsWith("o")) {
			p = 2;
		} else if (key.toString().toLowerCase().startsWith("p")) {
			p = 3;
		} else {
			p = 4;
		}
		return numPartitions == 5 ? p : 0;
	}

}