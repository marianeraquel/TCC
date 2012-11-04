package secondarysort;

import java.io.IOException;



import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import customkey.IntPair;

public class SecondarySortMapper extends Mapper<LongWritable, Text, IntPair, IntWritable> {
	
	private String [] tokens = null;
	private IntWritable ONE = new IntWritable(1);


	@Override
	public void map(LongWritable key, Text value,
			Context context)
			throws IOException , InterruptedException{
System.out.println("map");
System.out.println(key);
System.out.println(value);
System.out.println();
		if(value!=null){
			tokens = value.toString().split(" ") ;
			ONE.set(Integer.parseInt(tokens[1]));
			context.write(new IntPair(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])), ONE);
					
		}
System.out.println(key);
System.out.println(value);
System.out.println();
for(int i = 0; i < 5; i++) System.out.println(tokens[i]);
	}



}
