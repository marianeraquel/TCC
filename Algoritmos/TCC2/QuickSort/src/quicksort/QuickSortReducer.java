package quicksort;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import quicksort.QuickSort.QuickSortEnum;

public class QuickSortReducer extends MapReduceBase implements
		Reducer<DoubleWritable, IntWritable, DoubleWritable, Text> {

	@Override
	public void reduce(DoubleWritable key, Iterator<IntWritable> values,
			OutputCollector<DoubleWritable, Text> output, Reporter reporter)
			throws IOException {
		output.collect(key, new Text());
	}

}