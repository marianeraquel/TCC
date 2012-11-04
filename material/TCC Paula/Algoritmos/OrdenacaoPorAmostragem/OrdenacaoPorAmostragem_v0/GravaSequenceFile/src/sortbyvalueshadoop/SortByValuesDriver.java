/* Sort reducer input values in hadoop
 * Resource:
http://www.riccomini.name/Topics/DistributedComputing/Hadoop/SortByValue/
 */
package sortbyvalueshadoop;

import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

public class SortByValuesDriver
{

    public static void main(String[] args) throws IOException
    {

        new SortByValuesDriver().run(args);
    }

    public int run(String[] args) throws IOException
    {
        if (args.length != 3)
        {
            System.err.printf("Uso: %s [opcoes genericas] <entrada> <saida>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        JobConf conf = new JobConf();
        conf.setJobName("Sort By Values");

        conf.setInputFormat(SequenceFileInputFormat.class);
        conf.setOutputFormat(SequenceFileOutputFormat.class);
       

        loadFakeData(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));


        return 0;
    }

    protected void loadFakeData(String path, int num_dados, int valor_max) throws IOException
    {
        System.out.println("loadFakeData: " + path);
        JobConf conf = new JobConf();

        /*
        File dir = new File(path, "");
        dir.mkdir();

        File arq = new File(dir, "%dinteiros.txt", num_dados);
        */

        Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(path), IntWritable.class, Text.class);

        int valores_inteiros;

        for(int j = 0; j < 10; j++)
        {
            for(int i = 0; i < num_dados; i++)
            {
                valores_inteiros =  ((int)(Math.random() * valor_max) + 1);

                System.err.println(valores_inteiros);

                writer.append(new IntWritable(valores_inteiros), new Text(""));
            }

            writer.close();
        }
    }
}
