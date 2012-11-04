/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sortbyvalueshadoop;

import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;

/**
 *
 * @author hadoop
 */
public class SortByValuesGroupComparator implements RawComparator<Text>
        {
		public int compare(byte[] text1, int start1, int length1, byte[] text2, int start2, int length2)
                {
                           System.err.println("comparev1");
			// look at first character of each text byte array
			return new Character((char)text1[0]).compareTo((char)text2[0]);
		}

		public int compare(Text o1, Text o2)
    {
                        System.err.println("comparev2");
     
			return compare(o1.getBytes(), 0, o1.getLength(), o2.getBytes(), 0, o2.getLength());
		}
	}