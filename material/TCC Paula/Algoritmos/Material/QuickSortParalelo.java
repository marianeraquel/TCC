/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ordenacaoparalela;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
/**
 *
 * @author paula
 */
public class QuickSortParalelo
{
    /**	Global variables for the mapper and reducer tasks. */
    private static String inputDirPath;
    private static String outputDirPath;
    private static int numReduces;
    
    // classe de mapeamento
    public static class QuickSortParaleloMap extends Mapper<Text, Text, Text, Text>
    {


         public void setup(Context context)
         {
            init(context);

            // vector = leArquivo();
         }




        public void map(Text key, Text value, Context context) throws IOException, InterruptedException
        {
            int pivot = lenghtVector;

            // i = 1ª posicao
            // j = ultima posicao

            // se j - i <= tam_vetor / num_max_maps, entao, chama o reduce

            // enquanto i != j
            {
            // enquanto vetor[i] < vetor[pivo] entao i++
            // enquanto vetor[j] > vetor[pivo] entao j--
            // troca vetor[i] com vetor[j]
            }
            // quebra: qs(0, i); qs(i+1, ultima);

        }
    }

    // classe de reducao
    public static class QuickSortParaleloReduce extends Reducer<Text, Text, Text, Text>
    {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {

        }
    }

     /* Initializes the global variables from the job context for the mapper and reducer tasks. */
    private static void init(JobContext context)
    {
        Configuration conf = context.getConfiguration();
        inputDirPath = conf.get("QuickSortParalelo.inputDirPath");
        outputDirPath = conf.get("QuickSortParalelo.outputDirPath");
        numReduces = conf.getInt("QuickSortParalelo.numReduces", 0);
    }

    /**	Configures and runs job. */
    private static void job(Configuration conf) throws Exception
    {
        Job job = new Job(conf, "QuickSort Paralelo Job");
        job.setJarByClass(QuickSortParalelo.class);
        
        job.setNumReduceTasks(conf.getInt("QuickSortParalelo.numReduces", 0));
        
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        
        job.setMapperClass(JobMapper.class);
        job.setReducerClass(JobReducer.class);
        
        job.setMapOutputKeyClass(Key.class);
        job.setMapOutputValueClass(Value.class);
        
        job.setOutputKeyClass(IndexPair.class);
        job.setOutputValueClass(IntWritable.class);
        
        FileInputFormat.addInputPath(job, new Path(conf.get("QuickSortParalelo.inputPath")));
        if (conf.getInt("QuickSortParalelo.strategy", 0) == 4)
        {
            FileOutputFormat.setOutputPath(job, new Path(conf.get("QuickSortParalelo.outputDirPath")));
        }
        else
        {
            FileOutputFormat.setOutputPath(job, new Path(conf.get("QuickSortParalelo.tempDirPath")));
        }
        boolean ok = job.waitForCompletion(true);
        if(!ok)
        {
            throw new Exception("Job failed");
        }
    }



    public static void main(String[] args) throws Exception 
    {
        Configuration conf = new Configuration();

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2)
        {
            System.err.println("Uso: quicksortparalelo <dir-dfs-entrada> <dir-dfs-saida>");
            System.exit(2);
        }

        // Job controla os parametros de execucao da aplicacao paralela
        Job job = new Job(conf, "quicksort paralelo");

        // especifica a classe do programa principal
        job.setJarByClass(QuickSortParalelo.class);

        // especifica as classes de mapeamento e de reducao
        job.setMapperClass(QuickSortParalelo.class);
        job.setCombinerClass(QuickSortParalelo.class);
        job.setReducerClass(QuickSortParalelo.class);

        // especifica os tipos de dados da chave e do valor dos
        // pares finais resultantes (reducer)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // especifica os caminhos dos arquivos de entrada e saida no HDFS
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        // submete o job a execucao
        // wait: bloqueia o programa ate que a execucao termine
        // alternativa: utilizacao do metodo submit para submeter o
        // job a execucao sem causar o bloqueio do programa
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

