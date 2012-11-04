package sortbyvalueshadoop;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;

public class SortReducerByValues
{
	//public static final String INPUT = "/home/hadoop/data_in";
	//public static final String OUTPUT = "/home/hadoop/data_out";

	public static void main(String[] args) throws IOException
        {
            
		new SortByValuesDriver().run(args);
	}
	
	public int run(String[] args) throws IOException
        {
                if(args.length != 2)
                {
                    System.err.printf("Uso: %s [opcoes genericas] <entrada> <saida>\n", getClass().getSimpleName());
                    ToolRunner.printGenericCommandUsage(System.err);
                    return -1;
                }

		JobConf conf = new JobConf();
                conf.setJobName("Sort By Values");

		//conf.setInputFormat(SequenceFileInputFormat.class);
		//conf.setOutputFormat(SequenceFileOutputFormat.class);

                conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);

		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Text.class);

		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(SortReducerByValuesMapper.class);
		conf.setReducerClass(SortReducerByValuesReducer.class);

		conf.setOutputKeyComparatorClass(SortReducerByValuesKeyComparator.class);
		conf.setOutputValueGroupingComparator(SortReducerByValuesValueGroupingComparator.class);
        	conf.setPartitionerClass(SortReducerByValuesPartitioner.class);

                FileInputFormat.addInputPath(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));

		conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(args[0]), true);
		conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(args[1]), true);

		loadFakeData(args[0]);

		JobClient.runJob(conf).waitForCompletion();

                return 0;
	}

	public static final class SortReducerByValuesKeyComparator implements RawComparator<Text>
        {
		public int compare(byte[] text1, int start1, int length1, byte[] text2, int start2, int length2) {
			// hadoop gives you an extra byte before text data. get rid of it.
			byte[] trimmed1 = new byte[2];
			byte[] trimmed2 = new byte[2];
			System.arraycopy(text1, start1+1, trimmed1, 0, 2);
			System.arraycopy(text2, start2+1, trimmed2, 0, 2);

			char char10 = (char)trimmed1[0];
			char char20 = (char)trimmed2[0];
			char char11 = (char)trimmed1[1];
			char char21 = (char)trimmed2[1];

			// first enforce the same rules as the value grouping comparator
			// (first letter of key)
			int compare = new Character(char10).compareTo(char20);

			if(compare == 0)
                        {
				// ONLY if we're in the same reduce aggregate should we try and
				// sort by value (second letter of key)
				return -1 * new Character(char11).compareTo(char21);
			}

			return compare;
		}

		public int compare(Text o1, Text o2)
                {
			// reverse the +1 since the extra text byte is not passed into
			// compare() from this function
			return compare(o1.getBytes(), 0, o1.getLength() - 1, o2.getBytes(), 0, o2.getLength() - 1);
		}
	}


	public static final class SortReducerByValuesPartitioner implements Partitioner<Text, Text>
        {
		public int getPartition(Text key, Text value, int numPartitions)
                {
                        System.out.println("numPartitions: "+numPartitions);
			// just partition by the first character of each key since that's
			// how we are grouping for the reducer
			return key.toString().charAt(0) % numPartitions;
		}

                public void configure(JobConf job)
                {
                    
                }
	}

	public static final class SortReducerByValuesValueGroupingComparator implements RawComparator<Text>
        {
		public int compare(byte[] text1, int start1, int length1, byte[] text2, int start2, int length2)
                {
			// look at first character of each text byte array
			return new Character((char)text1[0]).compareTo((char)text2[0]);
		}

		public int compare(Text o1, Text o2)
                {
			return compare(o1.getBytes(), 0, o1.getLength(), o2.getBytes(), 0, o2.getLength());
		}
	}

	protected void loadFakeData(String path) throws IOException
        {
            System.out.println("loadFakeData: "+ path);
		JobConf conf = new JobConf();
		Writer writer = new Writer(FileSystem.get(conf), conf, new Path(path), Text.class, Text.class);

		for(int i = 0; i < 100; ++i)
                {
			String letterCSV = "";

			for(int j = 0; j < 10; ++j)
                        {
				letterCSV += (char)(65 + (int)(Math.random() * 26)) + ",";
			}

			writer.append(new Text(), new Text(letterCSV.substring(0, letterCSV.length() - 1)));
		}

		writer.close();
	}

	public static final class SortReducerByValuesMapper implements Mapper<Text, Text, Text, Text>
        {

		public void map(Text key, Text val, OutputCollector<Text,Text> collector, Reporter reporter) throws IOException
                {
                    System.out.println("Map: "+ key + " "+ val);
			String[] chars = val.toString().split(",");

			for(int i = 0; i < chars.length - 1; ++i)
                        {
				collector.collect(new Text(chars[i] + chars[i+1]), new Text(chars[i+1]));
			}
		}

		public void configure(JobConf conf) { }
		public void close() throws IOException { }
	}

	public static final class SortReducerByValuesReducer implements Reducer<Text, Iterator, Text, Text>
        {
		public void reduce(Text key, Iterator values, OutputCollector collector, Reporter reporter) throws IOException
                {
			// values should now be in order
			String check = key + ": ";

			while(values.hasNext())
                        {
				check += values.next();

                                 //System.out.println("Reduce: "+ check);
			}

			System.err.println(check);
		}

		public void configure(JobConf conf) { //conf.setNumReduceTasks(2);

                }
		public void close() throws IOException { }
	}
}
