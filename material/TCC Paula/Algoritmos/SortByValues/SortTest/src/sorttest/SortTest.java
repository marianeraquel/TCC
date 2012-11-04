/*
 * http://www.treadbook.com/wiki/Simple_Sorting
 */

package sorttest;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class SortTest extends Configured implements Tool {

	public static class Map extends
			Mapper<Object, Text, IntWritable, NullWritable> {

		private IntWritable num = new IntWritable();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			num.set(Integer.parseInt(value.toString()));
			context.write(num, NullWritable.get());
		}
	}

	public static class OrderedPartitioner extends Partitioner<IntWritable, NullWritable> {

		@Override
		public int getPartition(IntWritable key, NullWritable value, int numPartitions) {
			int num = key.get();
			int splitPoint = Integer.MAX_VALUE / numPartitions;
                        System.out.println(splitPoint);
			int partition = num / splitPoint;
                        System.out.println(partition);
			return partition < numPartitions ? partition : numPartitions -1;
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();

		Job job = new Job(conf, "SortTest");
		job.setJarByClass(SortTest.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reducer.class);

                job.setNumReduceTasks(2);
		//job.setNumReduceTasks(Integer.parseInt(otherArgs[2]));

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setPartitionerClass(OrderedPartitioner.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(NullWritable.class);

		TextInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new SortTest(), args);
		System.exit(res);
	}

}
