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
public class SortByValuesComparator implements RawComparator<Text>
{
		public int compare(byte[] text1, int start1, int length1, byte[] text2, int start2, int length2) {
	            //System.err.println("comparec1: "+text1.toString().getBytes()+" "+start1+" "+length1+" "+text2.toString()+" "+start2+" "+length2);

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

                        System.err.println("compara-chaves1");

			return compare;
		}

		public int compare(Text o1, Text o2)
                {
                        //System.err.println("comparec2: "+o1+" "+o2);
			// reverse the +1 since the extra text byte is not passed into
			// compare() from this function
                         System.err.println("compara-chaves2");

                        return compare(o1.getBytes(), 0, o1.getLength() - 1, o2.getBytes(), 0, o2.getLength() - 1);
		}
}