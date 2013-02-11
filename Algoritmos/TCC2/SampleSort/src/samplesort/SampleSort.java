/**
 * @file OrdenacaoPorAmostragem.java @brief Programa MapReduce para ordenar um
 * SequenceFile com chaves IntWritable usando a classe TotalOrderPartitioner
 * para ordenar globalmente os dados.
 *
 * @author Paula Pinhao @year 2011
 * @version 1.0
 */
package samplesort;

// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
import java.io.FileWriter;
import java.net.URI;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.InputSampler;
import org.apache.hadoop.mapred.lib.TotalOrderPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


// </editor-fold>

public class SampleSort extends Configured implements Tool {

	enum SampleSortEnum {
		P0, P1, P2, P3, P4, P5, P6, P7, P8, P9
	}

	public SampleSortEnum teste(int p) {
		if (p == 0)
			return SampleSortEnum.P0;
		return SampleSortEnum.P1;
	}


	public static void main(String[] args) {
		int exitCode;
		try {
			exitCode = ToolRunner.run(new SampleSort(), args);
			System.exit(exitCode);
		} catch (Exception e) {
			System.err.println();
			System.err.println("Ooops. Ocorreu um erro inesperado. " + e.getLocalizedMessage());
			System.err.println();
			 e.printStackTrace();
		}
	}
	
	@Override
	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf();

		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		System.out.println(args.length);
		if (otherArgs.length < 3) {
			System.err.println("Erro! Uso: samplesort <entrada> <particoes> <saida> [execucoes]");
			System.exit(2);
		}

		// Diretório de entrada
		String inputPath = args[0];
		Path inFS = new Path(inputPath);

		// Diretório de saída
		String outputPath = "Output"; //args[1];
		Path outFS = new Path(outputPath);

		// Número de partições
		int partitions = Integer.parseInt(args[1]);
		String saida = args[2];

		// Probabilidade com a qual uma chave sera escolhida
		double freqMin = 0.9; // Double.parseDouble(args[5]);
		double freqMax = 0.9; // Double.parseDouble(args[6]);
		double freqPasso = 0.1; // Double.parseDouble(args[7]);

		// Número de amostras
		int sampleMin = 10000; // Integer.parseInt(args[8]);
		int sampleMax = 10000;// Integer.parseInt(args[9]);
		int samplePasso = 1; // Integer.parseInt(args[10]);

		// numero de execucoes por arquivo ?
		int max_execucoes = 1; 
		//if (args.length > 2);
		//	max_execucoes = Integer.parseInt(args[3]);

		FileSystem fs = FileSystem.get(conf);

		//for (FileStatus inputFileFS : fs.listStatus(inFS)) {

			System.out.println("Lendo entrada: " + inFS);

			//if (inputFileFS.isDir()) {

				conf = getConfiguracao();

				// Número de partições
				conf.setNumReduceTasks(partitions);
				conf.setReducerClass(SampleSortReducer.class);
				
				// especifica o caminho dos arquivos de entrada no HDFS
				FileInputFormat.addInputPath(conf, inFS);//new Path(inputFileFS.getPath().toString()));

				// cria os arquivos para gravar os resultados fora do hdfs
				FileWriter printWriter = new FileWriter(
						"/home/marianehadoop/Dropbox/TCC/Algoritmos/TestesTCC2/" + saida, true);

				// testes com diferentes parametros

				for (double freq = freqMin; freq <= freqMax; freq += freqPasso) {

					for (int num_amostras = sampleMin; num_amostras <= sampleMax; num_amostras += samplePasso) {

						long tempo_total = 0;

						long[] total_elementos_particao = new long[partitions];
						// for (int i = 0; i < max_particoes; i++) {
						// total_elementos_particao[i] = 0;
						// }

						String freq_amostras = freq + " " + num_amostras + " ";

						for (int i = 1; i <= max_execucoes; i++) {

							// especifica o caminho do arquivo de saida no HDFS
							//String path_saida = inputFileFS.getPath().toString() + "-" + freq + "-"
							//		+ num_amostras + "-" + i;
							FileOutputFormat.setOutputPath(conf, outFS);

							// apaga arquivo de saida se ja existir no HDFS
							conf.getWorkingDirectory().getFileSystem(conf).delete(outFS, true);

							// especifica os parametros para particionamento por
							// amostragem
							InputSampler.Sampler<DoubleWritable, Text> sampler = new InputSampler.RandomSampler<DoubleWritable, Text>(
									freq, num_amostras, partitions);

							// seta o caminho para criar o arquivo binario das
							// particoes
							Path input = FileInputFormat.getInputPaths(conf)[0];
							input = input.makeQualified(input.getFileSystem(conf));

							// cria o arquivo binario para armazenar as chaves
							// que representam as particoes
							Path partitionFile = new Path(input, "_partitions");
							TotalOrderPartitioner.setPartitionFile(conf, partitionFile);

							// escreve no arquivo binario que armazena as chaves
							// que representam as particoes
							InputSampler.writePartitionFile(conf, sampler);

							// adiciona cache distribuido para compartilhar o
							// arquivo de particao
							// com as tarefas executando no cluster
							URI partitionUri = new URI(partitionFile.toString() + "#_partitions");
							DistributedCache.addCacheFile(partitionUri, conf);
							DistributedCache.createSymlink(conf);

							// submete o job a execucao

							Date tempo_inicio = new Date();

							RunningJob job = JobClient.runJob(conf);

							Date tempo_fim = new Date();

							Counters counters = job.getCounters();

							double tempo_exec = (tempo_fim.getTime() - tempo_inicio.getTime())/1000.0;

							printWriter.write(tempo_exec + " ");
/*							Counters.Counter counter = counters.findCounter("org.apache.hadoop.mapred.Task$Counter", "REDUCE_OUTPUT_RECORDS");
							
							printWriter.write(counter.getValue() + " ");



							for (SampleSortEnum part : SampleSortEnum.values()) {
								long partitionSize = counters.getCounter(part);
								printWriter.write(partitionSize + " ");
							}
							*/ 
        
                            for (FileStatus arq_saida : fs.listStatus(outFS)) {

                                long num_linhas = 0;
                                if (!arq_saida.isDir() && arq_saida.getPath().toString().contains("part")) {
                                    String cmd_conta_linhas = "/home/marianehadoop/hadoop/bin/hadoop fs -text " + arq_saida.getPath().toString() + " | wc -l";
                                    Runtime rt = Runtime.getRuntime();
                                    Process prcs = Runtime.getRuntime().exec(cmd_conta_linhas);
                                    InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
                                    BufferedReader br = new BufferedReader(isr);

                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        num_linhas++;
                                    }

                                    rt.runFinalization();

                                    
                                    printWriter.write(num_linhas + " ");
                                }

                            }//*/
 
							printWriter.write("\n");
						}
					}
				}

				printWriter.close();
			//}
		//}

		return 0;
	}

	/*** CONFIGURAÇÕES GERAIS ***/
	public JobConf getConfiguracao() {
		JobConf conf = new JobConf();
		conf.setJobName("Samplesort");
		conf.setJarByClass(SampleSort.class);

		/** Formato dos arquivos entrada e saída **/
		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);

		// Tipo de compressão e tratamento dos arquivos binários
		SequenceFileOutputFormat.setCompressOutput(conf, true);
		SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
		SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
        
		/** Formato das chaves/valores de entrada e saída **/
		conf.setOutputKeyClass(DoubleWritable.class);
		conf.setOutputValueClass(Text.class);

		// seta a classe de particionamento
		conf.setPartitionerClass(TotalOrderPartitioner.class);
		
		return conf;
	}

}
