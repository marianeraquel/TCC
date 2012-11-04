/*
 * 
 */
package quicksort;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 *
 * @author raquel
 */
public class QuickSortJob extends Configured implements Tool {
      

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new QuickSortJob(), args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        System.out.println();

        if (args.length != 2) {
            printAndExit("Erro! Uso: quicksort <diretorio-entrada> <particoes>");
        }

        JobConf conf = new JobConf();

        // Diretório contendo os arquivos
        String inputPath = args[0];

        // Diretório para escrita dos arquivos ordenados
        String outputPath = "output";

        // Número de partições
        Integer partitions = Integer.parseInt(args[1]);


        // seta os tipos dos arquivos de entrada e saida
        conf.setInputFormat(SequenceFileInputFormat.class);
        conf.setOutputFormat(SequenceFileOutputFormat.class);

        /* AQUI TENHO QUE:
         * ESCOLHER UM PIVO
         * MAP PERSONALIZADO
         * PARTITIONER PERSONALIZADO
         * 
         * O MAP CONTA O NÚMERO DE LINHAS DO ARQUIVO
         * SETA UMA VARIÁVEL NUMEROLINHAS
         */

        Path in = new Path(inputPath);
        Path out = new Path(outputPath);

        // Diretório de entrada
        FileInputFormat.addInputPath(conf, new Path(inputPath));

        // Diretório de saída
        SequenceFileOutputFormat.setOutputPath(conf, new Path(outputPath));


        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(out)) {
            fs.delete(out, true);
        }

        // Formatos de saída
        conf.setOutputKeyClass(DoubleWritable.class);
        conf.setOutputValueClass(Text.class);

        // n maps e sem reduce
//        conf.setNumMapTasks(2);
//        conf.setNumReduceTasks(0);

        conf.setPartitionerClass(QuickSortPartitioner.class);
        
        JobClient.runJob(conf);

//        int depth = 1;
//        Configuration conf = new Configuration();
//        conf.set("recursion.depth", depth + "");
//        JobConf job = new Job(conf);
//        job.setJobName("Graph explorer");
//
//        job.setMapperClass(DatasetImporter.class);
//        job.setReducerClass(ExplorationReducer.class);
//        job.setJarByClass(DatasetImporter.class);
//
//        Path in = new Path("files/graph-exploration/import/");
//        Path out = new Path("files/graph-exploration/depth_1");
//
//        FileInputFormat.addInputPath(job, in);
//        FileSystem fs = FileSystem.get(conf);
//        if (fs.exists(out)) {
//            fs.delete(out, true);
//        }
//
//        SequenceFileOutputFormat.setOutputPath(job, out);
//        job.setInputFormatClass(TextInputFormat.class);
//        job.setOutputFormatClass(SequenceFileOutputFormat.class);
//        job.setOutputKeyClass(LongWritable.class);
//        job.setOutputValueClass(VertexWritable.class);
//
//        job.waitForCompletion(true);
//
//        long counter = job.getCounters()
//                .findCounter(ExplorationReducer.UpdateCounter.UPDATED)
//                .getValue();
//        depth++;
//
//        while (counter > 0) {
//            conf = new Configuration();
//            conf.set("recursion.depth", depth + "");
//            job = new Job(conf);
//            job.setJobName("Graph explorer " + depth);
//
//            job.setMapperClass(ExplorationMapper.class);
//            job.setReducerClass(ExplorationReducer.class);
//            job.setJarByClass(ExplorationMapper.class);
//
//            in = new Path("files/graph-exploration/depth_" + (depth - 1) + "/");
//            out = new Path("files/graph-exploration/depth_" + depth);
//
//            SequenceFileInputFormat.addInputPath(job, in);
//            if (fs.exists(out)) {
//                fs.delete(out, true);
//            }
//
//            SequenceFileOutputFormat.setOutputPath(job, out);
//            job.setInputFormatClass(SequenceFileInputFormat.class);
//            job.setOutputFormatClass(SequenceFileOutputFormat.class);
//            job.setOutputKeyClass(LongWritable.class);
//            job.setOutputValueClass(VertexWritable.class);
//
//            job.waitForCompletion(true);
//            depth++;
//            counter = job.getCounters()
//                    .findCounter(ExplorationReducer.UpdateCounter.UPDATED)
//                    .getValue();
//        }
        System.out.println();
        return 0;

    }

    public static void printAndExit(String s) {
        System.err.println(s);
        System.exit(2);
    }
}
