package testes;
//package quicksort;
//
//import java.io.IOException;
//
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.DoubleWritable;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.FileInputFormat;
//import org.apache.hadoop.mapred.FileOutputFormat;
//import org.apache.hadoop.mapred.JobClient;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.MapReduceBase;
//import org.apache.hadoop.mapred.Mapper;
//import org.apache.hadoop.mapred.OutputCollector;
//import org.apache.hadoop.mapred.Reporter;
//import org.apache.hadoop.mapred.SequenceFileOutputFormat;
//
//public class QuickSortPreprocessor {
//
//    static class PreprocessorMapper extends MapReduceBase implements Mapper {
//
//        private Text word = new Text();
// 
//        public void map(LongWritable key, Text value,
//                OutputCollector output, Reporter reporter) throws IOException {
//            String line = value.toString();
//            String[] tokens = line.split("t");
//            if (tokens == null || tokens.length != 2) {
//                System.err.print("Problem with input line: " + line + "n");
//                return;
//            }
//            int nbOccurences = Integer.parseInt(tokens[1]);
//            word.set(tokens[0]);
//            output.collect(new IntWritable(nbOccurences), word);
//        }
//
//        @Override
//        public void map(Object k1, Object v1, OutputCollector oc, Reporter rprtr) throws IOException {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//       
//    }
//
//    public static void main(String[] args) throws IOException {
//        JobConf conf = new JobConf(QuickSortPreprocessor.class);
//        
//        FileInputFormat.setInputPaths(conf, new Path(args[0]));
//        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
//
//        conf.setMapperClass(MapperDivisor.class);
//        conf.setOutputKeyClass(DoubleWritable.class);
//        conf.setOutputValueClass(Text.class);
//        conf.setNumReduceTasks(0);
//        conf.setOutputFormat(SequenceFileOutputFormat.class);
//        JobClient.runJob(conf);
//        
//    }
//}