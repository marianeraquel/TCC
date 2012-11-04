/**
 * @file    OrdenacaoPorAmostragem.java
 * @brief   Programa MapReduce para ordenar um SequenceFile com chaves IntWritable
 *          usando a classe TotalOrderPartitioner para ordenar globalmente os dados.
 * 
 * @author  Paula Pinhao
 * @year    2011
 * @version 1.0
 */

package ordenacaoporamostragem;

// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.String;
import java.net.URI;
import java.util.Date;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.util.*;
// </editor-fold>

public class OrdenacaoPorAmostragem extends Configured implements Tool
{
    /**
     *
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] args) throws Exception
    {
        JobConf conf = new JobConf();

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherArgs.length != 12)
        {
            System.err.println("Uso: ordenacao_paralela <dir-dfs-entrada><particoes><freq_min><freq_max><freq_passo><amostras_min><amostras_max><anostras_passo><execucoes><dir-resultados><arq-resultados>");
            System.exit(2);
        }
        
        // parametro diretorio pai arquivos
        String dir = args[0];

        // parametros particionamento por amostragem
        // maximo de particoes
        int max_particoes = Integer.parseInt(args[1]);

        // freq - probabilidade com a qual uma chave sera escolhida
        double freq_min = Double.parseDouble(args[2]);
        double freq_max = Double.parseDouble(args[3]);
        double freq_passo = Double.parseDouble(args[4]);

        // numero total de amostras a serem obtidas das particoes selecionadas
        int num_amostras_min = Integer.parseInt(args[5]);
        int num_amostras_max = Integer.parseInt(args[6]);
        int num_amostras_passo = Integer.parseInt(args[7]);

        // numero de execucoes por arquivo
        int max_execucoes = Integer.parseInt(args[8]);
        
        // diretorio saida arquivos de resultados
        String nome_dir_saida = args[9];

        // nome arquivos de resultados
        String nome_arq_saida_1 = args[10];
        String nome_arq_saida_2 = args[11];

        FileSystem diretorio_pai = FileSystem.get(conf);
        for(FileStatus diretorio_filho : diretorio_pai.listStatus(new Path(dir)))
        {
            if(diretorio_filho.isDir())
            {
                conf = new JobConf(new Path(diretorio_filho.getPath().toString()));

                // seta os tipos dos arquivos de entrada e saida
                conf.setInputFormat(SequenceFileInputFormat.class);
                conf.setOutputFormat(SequenceFileOutputFormat.class);
                conf.setNumReduceTasks(max_particoes);

                // seta o tipo de compressao e tratamento dos arquivos binarios
                SequenceFileOutputFormat.setCompressOutput(conf, true);
                SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
                SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);

                // seta o tipo de chave do resultado final
                conf.setOutputKeyClass(IntWritable.class);

                // seta a classe de particionamento
                conf.setPartitionerClass(TotalOrderPartitioner.class);

                // especifica o caminho dos arquivos de entrada no HDFS
                FileInputFormat.addInputPath(conf, new Path(diretorio_filho.getPath().toString()));

                // cria os arquivos para gravar os resultados fora do hdfs
                File dir_resultados = new File(nome_dir_saida);

                File arquivo_resultados1 = new File(dir_resultados, nome_arq_saida_1);
                arquivo_resultados1.createNewFile();                
                PrintWriter printWriter1 = new PrintWriter(new FileWriter(arquivo_resultados1, true));

                File arquivo_resultados2 = new File(dir_resultados, nome_arq_saida_2);
                arquivo_resultados2.createNewFile();
                PrintWriter printWriter2 = new PrintWriter(new FileWriter(arquivo_resultados2, true));

                // testes com diferentes parametros
                for(double freq = freq_min; freq <= freq_max; freq += freq_passo)
                {
                    for(int num_amostras = num_amostras_min; num_amostras <= num_amostras_max; num_amostras += num_amostras_passo)
                    {
                        long tempo_total = 0;

                        long[] total_elementos_particao = new long[max_particoes];
                        for(int i = 0; i < max_particoes; i++) { total_elementos_particao[i] = 0; }

                        String freq_amostras = freq + " " + num_amostras + " ";

                        for(int i = 1; i <= max_execucoes; i++)
                        {
                            // especifica o caminho do arquivo de saida no HDFS
                            String path_saida = diretorio_filho.getPath().toString() + "-" + freq + "-" + num_amostras + "-" + i;
                            FileOutputFormat.setOutputPath(conf, new Path(path_saida));

                            // apaga arquivo de saida se ja existir no HDFS
                            conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(path_saida), true);

                            // especifica os parametros para particionamento por amostragem
                            InputSampler.Sampler<IntWritable, Text> sampler = new InputSampler.RandomSampler<IntWritable, Text>(freq, num_amostras, max_particoes);

                            // seta o caminho para criar o arquivo binario das particoes
                            Path input = FileInputFormat.getInputPaths(conf)[0];
                            input = input.makeQualified(input.getFileSystem(conf));

                            // cria o arquivo binario para armazenar as chaves que representam as particoes
                            Path partitionFile = new Path(input, "_partitions");
                            TotalOrderPartitioner.setPartitionFile(conf, partitionFile);

                            // escreve no arquivo binario que armazena as chaves que representam as particoes
                            InputSampler.writePartitionFile(conf, sampler);

                            // adiciona cache distribuido para compartilhar o arquivo de particao
                            // com as tarefas executando no cluster
                            URI partitionUri = new URI(partitionFile.toString() + "#_partitions");
                            DistributedCache.addCacheFile(partitionUri, conf);
                            DistributedCache.createSymlink(conf);

                            // submete o job a execucao
                            Date tempo_inicio = new Date();
                            JobClient.runJob(conf);
                            Date tempo_fim = new Date();
                            long tempo_exec = tempo_fim.getTime() - tempo_inicio.getTime();

                            printWriter1.print(freq_amostras + tempo_exec + " ");

                            FileSystem diretorio_saida = FileSystem.get(conf);
                            int j = 0;
                            for(FileStatus arq_saida : diretorio_saida.listStatus(new Path(path_saida)))
                            {
                                long num_linhas = 0;
                                if(!arq_saida.isDir())
                                {
                                    String cmd_conta_linhas = "bin/hadoop fs -text " + arq_saida.getPath().toString() + " | wc -l";
                                    Runtime rt = Runtime.getRuntime();
                                    Process prcs = Runtime.getRuntime().exec(cmd_conta_linhas);
                                    InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
                                    BufferedReader br = new BufferedReader(isr);

                                    String line;
                                    while((line = br.readLine()) != null) { num_linhas++; }

                                    rt.runFinalization();

                                    total_elementos_particao[j] += num_linhas;
                                    printWriter1.print(num_linhas + " ");

                                    j++;
                                }

                            }

                            printWriter1.print("\n");
                            printWriter1.flush();

                            tempo_total += tempo_exec;
                        }

                        printWriter1.print(freq_amostras);
                        printWriter2.print(freq_amostras);

                        double tempo_medio = tempo_total / (double) max_execucoes;
                        printWriter1.print(tempo_medio + " ");
                        printWriter2.print(tempo_medio + " ");
                        for(int j = 0; j < max_particoes; j++)
                        {
                            double media_elementos = total_elementos_particao[j] / (double) max_execucoes;

                            printWriter1.print(media_elementos + " ");
                            printWriter2.print(media_elementos + " ");
                        }

                        printWriter1.print("\n");
                        printWriter2.print("\n");

                        printWriter1.flush();
                        printWriter2.flush();
                    }
                }

                printWriter1.close();
                printWriter2.close();
            }
        }

        return 0;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        int exitCode = ToolRunner.run(new OrdenacaoPorAmostragem(), args);
        System.exit(exitCode);
    }
}
