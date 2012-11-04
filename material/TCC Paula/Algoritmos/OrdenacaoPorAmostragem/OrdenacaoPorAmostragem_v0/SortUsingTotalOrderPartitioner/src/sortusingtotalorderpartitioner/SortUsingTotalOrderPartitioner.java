/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sortusingtotalorderpartitioner;

// cc SortUsingTotalOrderPartitioner A MapReduce program for sorting a SequenceFile with IntWritable keys using the TotalOrderPartitioner to globally sort the data
import java.net.URI;
import java.util.Date;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.util.*;

// vv SortUsingTotalOrderPartitioner
public class SortUsingTotalOrderPartitioner extends Configured
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

        double freq = Double.parseDouble(args[2]);
        int num_amostras = Integer.parseInt(args[3]);
        int max_particoes = Integer.parseInt(args[4]);

        /*
            freq - Probability with which a key will be chosen.
            numSamples - Total number of samples to obtain from all selected splits.
            maxSplitsSampled - The maximum number of splits to examine.
         */
        InputSampler.Sampler<IntWritable, Text> sampler =
                new InputSampler.RandomSampler<IntWritable, Text>(freq, num_amostras, max_particoes);

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

        Date tempo_inicio = new Date();
        JobClient.runJob(conf);
        Date tempo_fim = new Date();
        System.out.println("Tempo total: "
                + (tempo_fim.getTime() - tempo_inicio.getTime()) + " milisegundos.");

        return 0;
    }

    public static void main(String[] args) throws Exception
    {
        int exitCode = ToolRunner.run(new SortUsingTotalOrderPartitioner(), args);
        System.exit(exitCode);
    }
}
// ^^ SortUsingTotalOrderPartitioner
