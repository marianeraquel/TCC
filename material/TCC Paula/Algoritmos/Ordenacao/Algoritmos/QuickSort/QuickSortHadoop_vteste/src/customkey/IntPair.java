/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package customkey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

/**
 *
 * @author hadoop
 */
public class IntPair implements WritableComparable<IntPair>
{
    IntWritable second;

    public IntWritable getSecond() { return second; }

    public IntPair()
    {
        second = new IntWritable();
    }

    public IntPair(int second)
    {
        this(new IntWritable(second));
    }

    public IntPair(IntWritable second)
    {
        this.second = second;
    }

    @Override
    public void readFields(DataInput in) throws IOException
    {
        second.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException
    {
        second.write(out);
    }

    @Override
    public int compareTo(IntPair that)
    {
        return this.second.compareTo(that.second);
        /*
        // compara chaves
        int cmp = first.compareTo(that.first);

        // compara valores
        if (cmp == 0)
        {
            cmp = second.compareTo(that.second);
        }

        return cmp;
        */
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IntPair)
        {
            IntPair that = (IntPair) obj;
            return (second.equals(that.second));
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return second.hashCode();
    }

    @Override
    public String toString()
    {
        return second.toString();
    }
}
