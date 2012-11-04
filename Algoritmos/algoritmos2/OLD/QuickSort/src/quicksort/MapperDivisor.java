///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package quicksort;
//
//import java.io.IOException;
//import org.apache.hadoop.io.DoubleWritable;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.MapReduceBase;
//import org.apache.hadoop.mapred.Mapper;
//import org.apache.hadoop.mapred.OutputCollector;
//import org.apache.hadoop.mapred.Reporter;
//
///**
// *
// * @author raquel
// */
//public class MapperDivisor extends MapReduceBase implements Mapper {
//
//    private Text word = new Text("\n");
//
//    public void map(DoubleWritable key, Text value,
//            OutputCollector output, Reporter reporter) throws IOException {
//        output.collect(key, word);
//    }
//
//    @Override
//    public void map(Object k1, Object v1, OutputCollector oc, Reporter rprtr) throws IOException {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//}
