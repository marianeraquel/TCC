/* Sort reducer input values in hadoop
 * Resource:
http://www.riccomini.name/Topics/DistributedComputing/Hadoop/SortByValue/
 */
package sortbyvalueshadoop;

import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

public class SortByValuesDriver
{
    public static void main(String[] args) throws IOException
    {
       new SortByValuesDriver().run(args);
    }

    public int run(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            System.err.printf("Uso: %s [opcoes genericas] <entrada> <saida>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        JobConf conf = new JobConf();
        conf.setJobName("Sort By Values");
        conf.setNumReduceTasks(1);

        conf.setInputFormat(SequenceFileInputFormat.class);
        conf.setOutputFormat(SequenceFileOutputFormat.class);
       
        //conf.setMapOutputKeyClass(Text.class);
        //conf.setMapOutputValueClass(Text.class);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(SortByValuesMapper.class);
        conf.setReducerClass(SortByValuesReducer.class);

        conf.setOutputKeyComparatorClass(SortByValuesComparator.class);
        conf.setOutputValueGroupingComparator(SortByValuesGroupComparator.class);
        conf.setPartitionerClass(SortByValuesPartitioner.class);

        FileInputFormat.addInputPath(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        //conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(args[0]), true);
        conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(args[1]), true);

        loadFakeData(args[0]);

        JobClient.runJob(conf).waitForCompletion();

        return 0;
    }

    protected void loadFakeData(String path) throws IOException
    {
        System.out.println("loadFakeData: " + path);
        JobConf conf = new JobConf();

        Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(path), Text.class, Text.class);
        //Writer writer = new Writer(FileSystem.get(conf), conf, new Path(path), Text.class, Text.class);

        for (int i = 0; i < 100; ++i)
        {
            String letterCSV = "";

            for (int j = 0; j < 10; ++j)
            {
                letterCSV += (char) (65 + (int) (Math.random() * 26)) + ",";
                
            }
            //System.err.println(letterCSV);
            writer.append(new Text(), new Text(letterCSV.substring(0, letterCSV.length() - 1)));
        }

        writer.close();
    }
}