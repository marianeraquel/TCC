package sortvalidator;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class SortValidatorMapper extends MapReduceBase implements Mapper<DoubleWritable, Text, Text, Text> {
	private DoubleWritable lastKey;
	private String filename;

	private String getFilename(FileSplit split) {
		return split.getPath().getName();
	}

	public void map(DoubleWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		if (lastKey == null) {
			filename = getFilename((FileSplit) reporter.getInputSplit());
			output.collect(new Text(filename + ":begin"), new Text(key.toString()));
			lastKey = new DoubleWritable();
		} else {
			if (key.compareTo(lastKey) < 0) {
				output.collect(SortValidator.error, new Text("misorder in " + filename + " last: '" + lastKey.toString()
						+ "' current: '" + key.toString() + "'"));
			}
		}
		lastKey.set(key.get());
	}
}
