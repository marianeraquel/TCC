package quicksort;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import quicksort.QuickSort.QuickSortEnum;

/**
 * O Mapper conta as chaves maiores e menores que o pivot
 */

public class QuickSortMapper extends MapReduceBase implements
		Mapper<DoubleWritable, Text, DoubleWritable, IntWritable> {

	Double pivot;
	IntWritable smaller;
	IntWritable larger;

	public void map(DoubleWritable key, Text value, OutputCollector<DoubleWritable, IntWritable> output,
			Reporter reporter) throws IOException {

		if (key.get() <= pivot) {
			reporter.incrCounter(QuickSortEnum.SMALLER, 1);
			output.collect(key, smaller);
		} else {
			reporter.incrCounter(QuickSortEnum.LARGER, 1);
			output.collect(key, larger);
		}
	}

	@Override
	public void configure(JobConf job) {
		pivot = (double) job.getFloat("quicksort-pivot", 0.5f);
		smaller = new IntWritable(0);
		larger = new IntWritable(1);
		super.configure(job);
	}
}
//public class QuickSortMapper extends MapReduceBase implements
//Mapper<DoubleWritable, Text, IntWritable, DoubleWritable> {

// public class QuickSortMapper extends MapReduceBase implements
// Mapper<DoubleWritable, Text, IntWritable, DoubleWritable> {
//
// Double pivot;
// IntWritable smaller;
// IntWritable larger;
//
// public void map(DoubleWritable key, Text value, OutputCollector<IntWritable,
// DoubleWritable> output,
// Reporter reporter) throws IOException {
//
// if (key.get() <= pivot) {
// reporter.incrCounter(QuickSortEnum.SMALLER, 1);
// output.collect(smaller, key);
// } else {
// reporter.incrCounter(QuickSortEnum.LARGER, 1);
// output.collect(larger, key);
// }
// }
//
// @Override
// public void configure(JobConf job) {
// pivot = (double) job.getFloat("quicksort-pivot", 0.5f);
// smaller = new IntWritable(0);
// larger = new IntWritable(1);
// super.configure(job);
// }
// }

// static class ValidateMapper extends MapReduceBase implements
// Mapper<Text, Text, Text, Text> {
// private Text lastKey;
// private OutputCollector<Text, Text> output;
// private String filename;
//
// /**
// * Get the final part of the input name
// *
// * @param split
// * the input split
// * @return the "part-00000" for the input
// */
// private String getFilename(FileSplit split) {
// return split.getPath().getName();
// }
//
// public void map(Text key, Text value, OutputCollector<Text, Text> output,
// Reporter reporter) throws IOException {
// if (lastKey == null) {
// filename = getFilename((FileSplit) reporter.getInputSplit());
// output.collect(new Text(filename + ":begin"), key);
// lastKey = new Text();
// this.output = output;
// } else {
// if (key.compareTo(lastKey) < 0) {
// output.collect(error, new Text("misorder in " + filename
// + " last: '" + lastKey + "' current: '" + key + "'"));
// }
// }
// lastKey.set(key);
// }
//
// public void close() throws IOException {
// if (lastKey != null) {
// output.collect(new Text(filename + ":end"), lastKey);
// }
// }
// }