package quicksort;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

public class QuickSortPartitioner implements Partitioner<DoubleWritable, IntWritable> {

	@Override
	public int getPartition(DoubleWritable key, IntWritable value, int numPartitions) {
		return value.get();
	}

	@Override
	public void configure(JobConf job) {
	}

}
