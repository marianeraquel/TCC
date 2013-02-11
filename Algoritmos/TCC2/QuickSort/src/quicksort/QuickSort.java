/*
 * USO: 
 */
package quicksort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.Counters;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.InputSampler;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/****
 * O algoritmo é composto de duas fases:
 * 
 * 1. Realiza-se a leitura dos arquivos e particionamento recursivo (Fase Map
 * customizada + Particionador padrão + Fase Reduce customizada)
 * 
 * 2. Com os arquivos particionados corretamente, cada um é ordenado (Fase Map
 * padrão + Particionado padrão + Fase reduce padrão)
 * 
 ****/

public class QuickSort extends Configured implements Tool {

	enum QuickSortEnum {
		LARGER, SMALLER
	}

	// Contador global das partições
	Integer counter = 0;
	// Número de amostras para pegar o pivô
	Integer samples = 3; 
	double tempo_exec = 0.0;

	public static void main(String[] args) {
		int exitCode;
		try {
			exitCode = ToolRunner.run(new QuickSort(), args);
			System.exit(exitCode);
		} catch (Exception e) {
			System.err.println();
			System.err.println("Ooops. Ocorreu um erro inesperado. " + e.getLocalizedMessage());
			System.err.println();
			 e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public int run(String[] args) throws Exception {
		GenericOptionsParser a = new GenericOptionsParser(args);
		args = a.getRemainingArgs();
		System.out.println();

		if (args.length != 4) {
			printAndExit("Erro! Uso: quicksort <diretorio-entrada> <particoes> <sample> <saida>");
		}

		/****
		 * PARTE 1. Realiza-se a leitura dos arquivos e particionamento
		 * recursivo (Fase Map customizada + Particionador padrão + Fase Reduce
		 * customizada)
		 * 
		 ****/

		// Nome do diretório contendo os arquivos
		String inputPath = args[0];

		// Nome do diretório para escrita dos arquivos temporários
		String tempPath = "temp";

		// Nome do diretório para escrita dos arquivos ordenados
		String outputPath = "Output";

		// Número de partições esperadas ao final
		Integer partitions = Integer.parseInt(args[1]);

		samples = Integer.parseInt(args[2]);
		// Escrita dos resultados de tempo e tamanho das partições
		FileWriter printWriter = new FileWriter("/home/marianehadoop/Dropbox/TCC/Algoritmos/TestesTCC2/" + args[3], true);
						
		// Pilha para recursão
		Stack<QuickSortInfo> partitionStack = new Stack<QuickSortInfo>();
		Stack<QuickSortInfo> sortStack = new Stack<QuickSortInfo>();

		JobConf conf = getConfiguracao();
		FileSystem fs = FileSystem.get(conf);

		// Ver o que fazer com o número de chaves
		long numberKeys = 1;
		QuickSortInfo item = new QuickSortInfo(partitions, numberKeys, counter, inputPath, tempPath);
		partitionStack.push(item);

		       
        
		while (!partitionStack.empty()) {

			// Recupera próximo item a ser particionado
			QuickSortInfo info = partitionStack.pop();

			// Se o número de partições não foi atingido, particiona
			if (info.numberPartitions > 1) {
				Stack retorno = partitioner(info);
				partitionStack.addAll(retorno);
			}
			// Se o número de partições já foi atingido, adiciona para ordenação
			else {				
				sortStack.add(info);
			}
		}
		
		printWriter.write(Double.toString(tempo_exec) + "   ");
		
		while (!sortStack.empty()) {
			QuickSortInfo i = sortStack.pop();
			FileInputFormat.addInputPath(conf, new Path(i.inputPath));
			printWriter.write(i.numberKeys + " ");
		}

		Path outFS = new Path(outputPath);
		fs.mkdirs(outFS);
		int i = 0;
		for (Path path : FileInputFormat.getInputPaths(conf)) {
			System.out.println("Ordenar: " + path.toString());
			fs.rename(path, new Path(outFS, "File" + i));
			//fs.delete(path.getParent(), true);
			i++;
		}
		//fs.listStatus(fs.getWorkingDirectory(), new PathF(fs))
		//ExecuteShell("/home/marianehadoop/hadoop/bin/hadoop/dfs -rmr temp*");
		printWriter.write("\n");		
		printWriter.close();
		return 0;
	}

	@SuppressWarnings("unchecked")
	public Stack<QuickSortInfo> partitioner(QuickSortInfo item) throws Exception {

		// Configurações
		JobConf conf = getConfiguracao();

		// Duas tarefas reduce para 2 arquivos de saída
		conf.setNumReduceTasks(2);
		// Mapper, Reducer e Partitioner 
		conf.setPartitionerClass(QuickSortPartitioner.class);
		conf.setMapperClass(QuickSortMapper.class);
		conf.setReducerClass(QuickSortReducer.class);
		// Saída do Map é (key, particao) 
		conf.setMapOutputValueClass(IntWritable.class);
		conf.setMapOutputKeyClass(DoubleWritable.class);

		
		/** Diretórios de entrada e saída **/
		Path inFS = new Path(item.inputPath);
		Path outFS = new Path(item.getOutputPath());
		FileInputFormat.addInputPath(conf, inFS);
		SequenceFileOutputFormat.setOutputPath(conf, outFS);

		/** Eliminar o diretório de saída se já existir **/
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outFS)) {
			fs.delete(outFS, true);
		}

		/** Definir Pivot **/

		// Com os arquivos de entrada prontos para leitura, deve-se escolher
		// uma chave para ser o pivot.
		// Por ora, isso é feito com um amostrador, que lê um único valor
		// aleatório no arquivo.

		InputSampler.Sampler<DoubleWritable, Text> sampler = new InputSampler.RandomSampler<DoubleWritable, Text>(
				0.8, samples);
		Object[] vetor = sampler.getSample(conf.getInputFormat(), conf);
		float pivot = getMediana(vetor);

		conf.setFloat("quicksort-pivot", pivot);

		System.out.println("Lendo arquivos do diretório " + inFS.toString());
		System.out.println("Pivot: " + conf.getFloat("quicksort-pivot", 0.5f) + " obitido com " + samples + " amostras.");
		System.out.println();
		System.out.println();

		Date tempo_inicio = new Date(); 
		// Faz a divisão
		RunningJob job = JobClient.runJob(conf);

		Date tempo_fim = new Date();
		tempo_exec += (tempo_fim.getTime() - tempo_inicio.getTime())/1000.0;

		System.out.println();
		System.out.println();

		// Com os novos arquivos criados, apagar os antigos

		if (item.depth != 0) { 
			System.out.println("Apagando: " + inFS.toString());
			fs.delete(inFS, true);
		}

		// CRIAR NOVOS INFOS
		Counters counters = job.getCounters();

		double menores = (double) counters.findCounter(QuickSortEnum.SMALLER).getValue();
		double maiores = (double) counters.findCounter(QuickSortEnum.LARGER).getValue();
		item.numberKeys = maiores + menores;
		item.print();

		Stack s = new Stack();
		QuickSortInfo small = new QuickSortInfo();
		QuickSortInfo larger = new QuickSortInfo();

		/*** Calcular chaves e partições maiores ***/
		if (menores > 0) {
			small.inputPath = item.getOutputPath() + "/part-00000";
			small.outputPath = item.outputPath;
			small.depth = ++counter;
			small.numberKeys = menores;
			double part = (double) (small.numberKeys * item.numberPartitions) / (double) item.numberKeys;
			small.numberPartitions = (int) Math.max(Math.min(Math.round(part), item.numberPartitions - 1), 1);

			small.print();
			s.push(small);
		}
		if (maiores > 0) {
			larger.inputPath = item.getOutputPath() + "/part-00001";
			larger.outputPath = item.outputPath;
			larger.depth = ++counter;
			larger.numberKeys = (int) counters.findCounter(QuickSortEnum.LARGER).getValue();
			larger.numberPartitions = item.numberPartitions - small.numberPartitions;
			larger.print();
			s.push(larger);
		}
		return s;
	}

	/*** CONFIGURAÇÕES GERAIS ***/
	public JobConf getConfiguracao() {
		JobConf conf = new JobConf();
		conf.setJobName("Quicksort");
		conf.setJarByClass(QuickSort.class);

		/** Formato dos arquivos entrada e saída **/
		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);

        // seta o tipo de compressao e tratamento dos arquivos binarios
        SequenceFileOutputFormat.setCompressOutput(conf, true);
        SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
        
		/** Formato das chaves/valores de entrada e saída **/
		conf.setOutputKeyClass(DoubleWritable.class);
		conf.setOutputValueClass(Text.class);

		return conf;
	}



	public static Integer ExecuteShell(String s) throws Exception {
		Process procs = Runtime.getRuntime().exec(s);
		procs.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(procs.getInputStream()));
		String line = br.readLine();
		return Integer.parseInt(line);
	}

	public float getMediana(Object[] values) {
		ArrayList<DoubleWritable> newValues = new ArrayList<DoubleWritable>();
		for (Object a : values)
			newValues.add((DoubleWritable) a);
		Collections.sort(newValues);
		return (float) newValues.get(newValues.size() / 2).get();
	}

	public static void printAndExit(String s) {
		System.err.println(s);
		System.exit(2);
	}
}
