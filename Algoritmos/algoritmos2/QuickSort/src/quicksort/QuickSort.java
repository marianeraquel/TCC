/*
 * 
 */
package quicksort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.InputSampler;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * 
 * @author raquel
 */

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

	// Nome do diretório contendo os arquivos
	String inputPath;

	// Nome do diretório para escrita dos arquivos temporários
	String tempPath;

	// Nome do diretório para escrita dos arquivos ordenados
	String outputPath;

	// Número de partições esperadas ao final
	Integer partitions;

	// Contador global das partições
	Integer counter = 0;

	// Pilha para recursão
	Stack<QuickSortInfo> stack = new Stack<>();

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new QuickSort(), args);
		System.exit(exitCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int run(String[] args) throws Exception {
		System.out.println();

		if (args.length != 2) {
			printAndExit("Erro! Uso: quicksort <diretorio-entrada> <particoes>");
		}

		/****
		 * PARTE 1. Realiza-se a leitura dos arquivos e particionamento
		 * recursivo (Fase Map customizada + Particionador padrão + Fase Reduce
		 * customizada)
		 * 
		 ****/

		/** Dados **/
		inputPath = args[0];
		tempPath = "temp";
		outputPath = "Output";
		partitions = Integer.parseInt(args[1]);

		JobConf conf = getGonfiguracao();
		FileSystem fs = FileSystem.get(conf);

		// Ver o que fazer com o número de chaves
		Integer numberKeys = 0;
		for (FileStatus inputFileFS : fs.listStatus(new Path(inputPath))) {
			if (!inputFileFS.isDir() && inputFileFS.getPath().toString().contains("File")) {
				numberKeys += ExecuteShell("/home/raquel/hadoop/lineCounter.sh "
						+ inputFileFS.getPath().toString());
			}
		}

		
		QuickSortInfo item = new QuickSortInfo(partitions, numberKeys, counter, inputPath, tempPath);
		item.print();
		stack.push(item);

		while (!stack.empty()) {
			
			// Recupera próximo item a ser particionado
			QuickSortInfo info = stack.pop();
			
			// Se o número de partições não foi atingido, particiona
			if (info.numberPartitions > 1) {
				Stack retorno = partitioner(info); 
				stack.addAll(retorno);
			} 
			// Se o número de partições já foi atingido, adiciona para ordenação
			else {
				sort(info);
			}
		}

//		// verificar as pastas que devem ser ordenadas
//		for (Path path : FileInputFormat.getInputPaths(conf))
//			System.out.println(path.toString());
//
//		/** Configura diretório de saída **/
//		Path outFS = new Path(outputPath);
//		SequenceFileOutputFormat.setOutputPath(conf, new Path(outputPath));
//		conf.setNumReduceTasks(partitions);
//		/** Eliminar o diretório de saída se já existir **/
//		if (fs.exists(outFS)) {
//			fs.delete(outFS, true);
//		}
//		JobClient.runJob(conf);
		
		//
		// /** PARTE A SER RETIRADA: LÊ DADOS E CONTA NÚMERO DE CHAVES **/
		// int numberKeys = 0;
		//
		// for (FileStatus inputFileFS : fs.listStatus(inFS)) {
		// if (!inputFileFS.isDir()
		// && inputFileFS.getPath().toString().contains("File")) {
		// System.out.println("Lendo arquivo: "
		// + inputFileFS.getPath().toString());
		// Process prcs = Runtime.getRuntime().exec(
		// "/home/raquel/hadoop/lineCounter.sh "
		// + inputFileFS.getPath().toString());
		// prcs.waitFor();
		// numberKeys += prcs.exitValue();
		// }
		// }
		//
		// System.out.println("Num linhas: " + numberKeys);
		// // Se quiser que a amostra seja buscada em todos os arquivos
		// System.out.println("Número de arquivos: "
		// + (fs.listStatus(inFS)).length);
		//
		// /** FIM DA PARTE A SER RETIRADA **/
		//
		// /** Definir Pivot **/
		// // Com os arquivos de entrada prontos para leitura, deve-se escolher
		// // uma chave para ser o pivot.
		// // Por ora, isso é feito com um amostrador, que lê um único valor
		// // aleatório no arquivo.
		//
		// InputSampler.Sampler<DoubleWritable, Text> sampler = new
		// InputSampler.RandomSampler<DoubleWritable, Text>(
		// 0.9, 1, 1);
		// Object[] vetor = sampler.getSample(conf.getInputFormat(), conf);
		// float pivot = (float) ((DoubleWritable) vetor[0]).get();
		// conf.setFloat("quicksort-pivot", pivot);
		// System.out.println("Pivot: " + conf.getFloat("quicksort-pivot",
		// 0.5f));
		//
		// conf.setNumReduceTasks(2);
		// conf.setMapperClass(QuickSortMapper.class);
		// conf.setReducerClass(QuickSortReducer.class);
		// // conf.setNumMapTasks(2);
		// // conf.setPartitionerClass(QuickSortPartitioner.class);
		// JobClient.runJob(conf);
		// System.out.println();

		// Nesse ponto os arquivos estão dividos. Deve-se ler e contar as linhas
		return 0;
	}

	/*** CONFIGURAÇÕES GERAIS ***/
	public JobConf getGonfiguracao() {
		JobConf conf = new JobConf();
		conf.setJobName("Quicksort");
		conf.setJarByClass(QuickSort.class);

		/** Formato dos arquivos entrada e saída **/
		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);

		/** Formato das chaves/valores de entrada e saída **/
		conf.setOutputKeyClass(DoubleWritable.class);
		conf.setOutputValueClass(Text.class);

		return conf;
	}

	public Stack<QuickSortInfo> partitioner(QuickSortInfo item) throws Exception {

		// QuickSortInfo:
		// Número esperado de partições
		// Chaves
		// Partição
		// Arquivos entrada e saída

		JobConf conf = getGonfiguracao();

		conf.setNumReduceTasks(2);
		conf.setMapperClass(QuickSortMapper.class);
		conf.setReducerClass(QuickSortReducer.class);
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(DoubleWritable.class);
		
		/** Diretórios de entrada e saída **/
		Path inFS = new Path(item.inputPath);
		Path outFS = new Path(item.outputPath + item.depth);
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
				0.9, 1, 1);
		Object[] vetor = sampler.getSample(conf.getInputFormat(), conf);
		float pivot = (float) ((DoubleWritable) vetor[0]).get();
		conf.setFloat("quicksort-pivot", pivot);

		System.out.println("Lendo arquivos do diretório " + inFS.toString());
		System.out.println("Pivot: " + conf.getFloat("quicksort-pivot", 0.5f));

		JobClient.runJob(conf);
		System.out.println();

		// Com os novos arquivos criados, apagar os antigos
		// System.out.println("Apagando: " + item.inputPath);
		// fs.delete(inFS, true);

		// CRIAR NOVOS INFOS

		Integer[] numberKeys = new Integer[2];
		int i = 0;
		for (FileStatus inputFileFS : fs.listStatus(outFS)) {
			if (!inputFileFS.isDir() && inputFileFS.getPath().toString().contains("part")) {
				numberKeys[i++] = ExecuteShell("/home/raquel/hadoop/lineCounter.sh "
						+ inputFileFS.getPath().toString());
				System.out.println(numberKeys[i-1]);
			}
		}

		/*** Calcular chaves e partições maiores ***/
		QuickSortInfo small = new QuickSortInfo();
		small.inputPath = item.outputPath + item.depth + "/part-00000";
		small.outputPath = item.outputPath;
		small.depth = ++counter;
		small.numberKeys = numberKeys[0];
		small.numberPartitions = Math.max(Math.min(
				Math.round(small.numberKeys * item.numberPartitions / item.numberKeys),
				item.numberPartitions - 1), 1);
		small.print();

		QuickSortInfo larger = new QuickSortInfo();
		larger.inputPath = item.outputPath + item.depth + "/part-00001";
		larger.outputPath = item.outputPath;
		larger.depth = ++counter;
		larger.numberKeys = numberKeys[1];
		larger.numberPartitions = item.numberPartitions - small.numberPartitions;
		larger.print();

		Stack<QuickSortInfo> s = new Stack<>();
		if (larger.numberKeys > 0) {
			s.push(larger);
		}
		if (small.numberKeys > 0) {
			s.push(small);
		}
		return s;
	}

	public void sort(QuickSortInfo item) throws Exception {

		JobConf conf = getGonfiguracao();

		conf.setNumReduceTasks(1);
		
		/** Diretórios de entrada e saída **/
		Path inFS = new Path(item.inputPath);
		Path outFS = new Path(outputPath);
		FileInputFormat.addInputPath(conf, inFS);
		SequenceFileOutputFormat.setOutputPath(conf, outFS);

		JobClient.runJob(conf);
		System.out.println();

	}

	
	public static Integer ExecuteShell(String s) throws Exception {
		Process procs = Runtime.getRuntime().exec(s);
		procs.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(procs.getInputStream()));
		String line = br.readLine();
		return Integer.parseInt(line);
	}

	public static void printAndExit(String s) {
		System.err.println(s);
		System.exit(2);
	}
}