/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sortbyvalueshadoop;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

/**
 *
 * @author hadoop
 */
public class SortByValuesReducer implements Reducer<Text, Iterator, Text, Text>
        {
		public void reduce(Text key, Iterator values, OutputCollector collector, Reporter reporter) throws IOException
                {
			// values should now be in order
			String check = key + ": ";

                        System.err.println("reduce");

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