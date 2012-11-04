/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sortbytemperatureusingtotalorderpartitioner;

// cc SortByTemperatureUsingTotalOrderPartitioner A MapReduce program for sorting a SequenceFile with IntWritable keys using the TotalOrderPartitioner to globally sort the data
import java.net.URI;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.util.*;

// vv SortByTemperatureUsingTotalOrderPartitioner
public class SortByTemperatureUsingTotalOrderPartitioner extends Configured
        implements Tool
{

    @Override
    public int run(String[] args) throws Exception
    {
        //JobConf conf = JobBuilder.parseInputAndOutput(this, getConf(), args);
        JobConf conf = new JobConf(getConf(), getClass());

        if (conf == null)
        {
            return -1;
        }

        //conf.setInputFormat(TextInputFormat.class);
        conf.setInputFormat(SequenceFileInputFormat.class);
        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputFormat(SequenceFileOutputFormat.class);

        //conf.setNumReduceTasks(1);

        SequenceFileOutputFormat.setCompressOutput(conf, true);
        SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);

        conf.setPartitionerClass(TotalOrderPartitioner.class);

        InputSampler.Sampler<IntWritable, Text> sampler =
                new InputSampler.RandomSampler<IntWritable, Text>(0.1, 10000, 10);

        FileInputFormat.addInputPath(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        Path input = FileInputFormat.getInputPaths(conf)[0];
        input = input.makeQualified(input.getFileSystem(conf));

        Path partitionFile = new Path(input, "_partitions");
        TotalOrderPartitioner.setPartitionFile(conf, partitionFile);
        InputSampler.writePartitionFile(conf, sampler);

        // Add to DistributedCache
        URI partitionUri = new URI(partitionFile.toString() + "#_partitions");
        DistributedCache.addCacheFile(partitionUri, conf);
        DistributedCache.createSymlink(conf);

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception
    {
        int exitCode = ToolRunner.run(new SortByTemperatureUsingTotalOrderPartitioner(), args);
        System.exit(exitCode);
    }
}
// ^^ SortByTemperatureUsingTotalOrderPartitioner

