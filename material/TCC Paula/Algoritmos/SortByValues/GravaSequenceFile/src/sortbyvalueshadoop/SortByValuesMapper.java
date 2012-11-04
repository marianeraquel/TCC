/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sortbyvalueshadoop;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 *
 * @author hadoop
 */
public class SortByValuesMapper implements Mapper<Object, Text, Text, Text>
{
    public void map(Object key, Text val, OutputCollector<Text, Text> collector, Reporter reporter) throws IOException
    {
        System.err.println("Map: " + key + " " + val);
        String[] chars = val.toString().split(",");

        for (int i = 0; i < chars.length - 1; ++i)
        {
            //System.err.println("antescollector");
            collector.collect(new Text(chars[i] + chars[i + 1]), new Text(chars[i + 1]));
            //System.err.println("dpscollector");
        }
    }

    public void configure(JobConf conf)
    {
    }

    public void close() throws IOException
    {
    }
}
