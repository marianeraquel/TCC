//package quicksort;
//
//import java.io.IOException;
//
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.DoubleWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.FileInputFormat;
//import org.apache.hadoop.mapred.FileOutputFormat;
//import org.apache.hadoop.mapred.JobClient;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.SequenceFileInputFormat;
//
//public class Quicksort2 {
//
//    public static void main(String[] args) throws IOException {
//        JobConf conf = new JobConf(Quicksort2.class);
//        conf.setJobName("sortexample");
//
//        FileInputFormat.setInputPaths(conf, new Path(args[0]));
//        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
//
//        conf.setInputFormat(SequenceFileInputFormat.class);
//        conf.setOutputKeyClass(DoubleWritable.class);
//        conf.setOutputValueClass(Text.class);
//
//        conf.setNumReduceTasks(2);
//
//        JobClient.runJob(conf);
//    }
//}