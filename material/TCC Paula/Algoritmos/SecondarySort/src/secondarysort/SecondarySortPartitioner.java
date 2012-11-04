package secondarysort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;
import customkey.IntPair;

public class SecondarySortPartitioner extends Partitioner<IntPair, IntWritable> 
{

 
	@Override
	public int getPartition(IntPair key, IntWritable value, int numOfPartitions) {
		// TODO Auto-generated method stub
		System.out.println("getpartitioner");
		return (key.getFirst().hashCode())%numOfPartitions;
	}
	
}
