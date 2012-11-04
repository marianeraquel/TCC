package quicksort;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class QuickSortPartitioner implements Partitioner<DoubleWritable, Text> {

	Double pivot;

	@Override
	public int getPartition(DoubleWritable key, Text value, int numPartitions) {
		if (key.get() < pivot) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public void configure(JobConf conf) {
		// setar o pivô
		pivot = (double) conf.getFloat("quicksort-pivot", 0.5f);
	}
}

