/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sortbyvalueshadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

/**
 *
 * @author hadoop
 */
public class SortByValuesPartitioner implements Partitioner<Text, Text>
{
    public int getPartition(Text key, Text value, int numPartitions)
    {
        //System.err.println("numPartitions: "+numPartitions);
        // just partition by the first character of each key since that's
        // how we are grouping for the reducer
        //System.err.println("getpartition:" + "k: "+key + " v: "+value);
        //System.err.println(key.toString().charAt(0) % numPartitions);
        System.err.println(key.toString().charAt(0) % numPartitions);
        return key.toString().charAt(0) % numPartitions;


    }

    public void configure(JobConf job)
    {
    }
}
