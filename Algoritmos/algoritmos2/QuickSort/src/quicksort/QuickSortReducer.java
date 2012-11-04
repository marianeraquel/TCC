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

/**
 * 
 * @author raquel
 */

//public class QuickSortReducer extends MapReduceBase implements
//		Reducer<DoubleWritable, Text, DoubleWritable, Text> {
//
//	Double pivot;
//
//	@Override
//	public void reduce(DoubleWritable key, Iterator<Text> values,
//			OutputCollector<DoubleWritable, Text> output, Reporter reporter)
//			throws IOException {
//		output.collect(key, new Text("1"));
//	}
//
//}

public class QuickSortReducer extends MapReduceBase implements
		Reducer<IntWritable, DoubleWritable, DoubleWritable, Text> {

	Double pivot;

	@Override
	public void reduce(IntWritable key, Iterator<DoubleWritable> values,
			OutputCollector<DoubleWritable, Text> output, Reporter reporter)
			throws IOException {
		while (values.hasNext()) {
			output.collect(values.next(), new Text(""));
		}
	}

}

// static class ValidateReducer extends MapReduceBase implements
// Reducer<Text, Text, Text, Text> {
// private boolean firstKey = true;
// private Text lastKey = new Text();
// private Text lastValue = new Text();
//
// public void reduce(Text key, Iterator<Text> values,
// OutputCollector<Text, Text> output, Reporter reporter)
// throws IOException {
// if (error.equals(key)) {
// while (values.hasNext()) {
// output.collect(key, values.next());
// }
// } else {
// Text value = values.next();
// if (firstKey) {
// firstKey = false;
// } else {
// if (value.compareTo(lastValue) < 0) {
// output.collect(error, new Text("misordered keys last: "
// + lastKey + " '" + lastValue + "' current: " + key
// + " '" + value + "'"));
// }
// }
// lastKey.set(key);
// lastValue.set(value);
// }
// }
// }