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
public class QuickSortJob extends Configured implements Tool {

	// Nome do diretório contendo os arquivos
	String inputPath;

	// Nome do diretório para escrita dos arquivos temporários
	String tempPath;

	// Nome do diretório para escrita dos arquivos ordenados
	String outputPath;

	// Número de partições
	Integer partitions;

	// Contador global do depth
	Integer contador = 0;

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new QuickSortJob(), args);
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
		// Nome do diretório contendo os arquivos
		String inputPath = args[0];

		// Nome do diretório para escrita dos arquivos temporários
		String tempPath = "temp";

		// Nome do diretório para escrita dos arquivos ordenados
		String outputPath = "output";

		// Número de partições
		Integer partitions = Integer.parseInt(args[1]);

		// Ver o que fazer com o número de chaves
		Integer numberKeys = 0;
		JobConf conf = new JobConf();
		FileSystem fs = FileSystem.get(conf);
		for (FileStatus inputFileFS : fs.listStatus(new Path(inputPath))) {
			if (!inputFileFS.isDir() && inputFileFS.getPath().toString().contains("File")) {
				System.out.println("Lendo arquivo: " + inputFileFS.getPath().toString());
				numberKeys += ExecuteShell("/home/raquel/hadoop/lineCounter.sh "
						+ inputFileFS.getPath().toString());
				System.out.println("Num linhas: " + numberKeys);
			}
		}

		// CRIAR ESTRUTURA 1
		Estrutura item = new Estrutura(partitions, inputPath, tempPath, numberKeys);
		item.depth = contador;
		Stack<Estrutura> stack = new Stack<>();
		item.print();
		stack.push(item);

		while (!stack.empty()) {
			Estrutura pop = stack.pop();
			if (pop.numberPartitions > 1)
				stack.addAll(novaFuncao(pop));
			System.out.println("POP! GOES MY HEART");
		}
		// /** CONFIGURAÇÕES **/
		//
		// /** Gerais **/
		// JobConf conf = new JobConf();// ) getConf();
		// conf.setJobName("Quicksort");
		// conf.setJarByClass(QuickSortJob.class);
		// conf.setMapperClass(QuickSortMapper.class);
		//
		// /** Formato dos arquivos entrada e saída **/
		// conf.setInputFormat(SequenceFileInputFormat.class);
		// conf.setOutputFormat(SequenceFileOutputFormat.class);
		//
		// /** Formato das chaves/valores de entrada e saída **/
		// conf.setOutputKeyClass(DoubleWritable.class);
		// conf.setOutputValueClass(Text.class);
		//
		// /** Formato das chaves/valores de saída do MAPPER **/
		// conf.setMapOutputKeyClass(IntWritable.class);
		// conf.setMapOutputValueClass(DoubleWritable.class);
		//
		// /** Diretórios de entrada e saída **/
		// Path inFS = new Path(inputPath);
		// Path outFS = new Path(outputPath);
		// FileInputFormat.addInputPath(conf, new Path(inputPath));
		// SequenceFileOutputFormat.setOutputPath(conf, new Path(outputPath));
		// System.out.println("Lendo arquivos do diretório " + inFS.toString());
		//
		// /** Eliminar o diretório de saída se já existir **/
		// FileSystem fs = FileSystem.get(conf);
		// if (fs.exists(outFS)) {
		// fs.delete(outFS, true);
		// }
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

	public Stack<Estrutura> novaFuncao(Estrutura item) throws Exception {

		/***
		 *** A pilha contém: 1. inputFS: diretório de entrada para os arquivos 2.
		 * outputFS: diretório de saída para os arquivos 3. numberPartitions:
		 * Número esperado de partições 4. numberKeys: Número de chaves
		 ***/

		/*** CONFIGURAÇÕES ***/
		/** Gerais **/
		JobConf conf = new JobConf();
		conf.setJobName("Quicksort");
		conf.setJarByClass(QuickSortJob.class);
		conf.setMapperClass(QuickSortMapper.class);

		/** Formato dos arquivos entrada e saída **/
		conf.setInputFormat(SequenceFileInputFormat.class);
		conf.setOutputFormat(SequenceFileOutputFormat.class);

		/** Formato das chaves/valores de entrada e saída **/
		conf.setOutputKeyClass(DoubleWritable.class);
		conf.setOutputValueClass(Text.class);

		/** Formato das chaves/valores de saída do MAPPER **/
		conf.setMapOutputKeyClass(IntWritable.class);
		conf.setMapOutputValueClass(DoubleWritable.class);

		/** Diretórios de entrada e saída **/
		Path inFS = new Path(item.inputPath);
		Path outFS = new Path(item.outputPath + item.depth);
		FileInputFormat.addInputPath(conf, new Path(item.inputPath));
		SequenceFileOutputFormat.setOutputPath(conf, new Path(item.outputPath + item.depth));
		System.out.println("Lendo arquivos do diretório " + inFS.toString());

		/** Eliminar o diretório de saída se já existir **/
		FileSystem fs = FileSystem.get(conf);
		if (fs.exists(outFS)) {
			fs.delete(outFS, true);
		}

		/*** Definir Pivot ***/

		// Com os arquivos de entrada prontos para leitura, deve-se escolher
		// uma chave para ser o pivot.
		// Por ora, isso é feito com um amostrador, que lê um único valor
		// aleatório no arquivo.

		InputSampler.Sampler<DoubleWritable, Text> sampler = new InputSampler.RandomSampler<DoubleWritable, Text>(
				0.9, 1, 1);
		Object[] vetor = sampler.getSample(conf.getInputFormat(), conf);
		float pivot = (float) ((DoubleWritable) vetor[0]).get();
		conf.setFloat("quicksort-pivot", pivot);
		System.out.println("Pivot: " + conf.getFloat("quicksort-pivot", 0.5f));

		conf.setNumReduceTasks(2);
		conf.setMapperClass(QuickSortMapper.class);
		conf.setReducerClass(QuickSortReducer.class);
		JobClient.runJob(conf);
		System.out.println();

		// Com os novos arquivos criados, apagar os antigos
		// System.out.println("Apagando: " + item.inputPath);
		// fs.delete(inFS, true);

		// CRIAR NOVOS ITEMS

		Integer[] numberKeys = new Integer[2];
		int i = 0;
		for (FileStatus inputFileFS : fs.listStatus(outFS)) {
			if (!inputFileFS.isDir() && inputFileFS.getPath().toString().contains("part")) {
				numberKeys[i++] = ExecuteShell("/home/raquel/hadoop/lineCounter.sh "
						+ inputFileFS.getPath().toString());

				System.out.println("Lendo arquivo: " + inputFileFS.getPath().toString() + " -> "
						+ numberKeys[i - 1]);
			}
		}
		System.out.println("Num linhas: " + numberKeys[0] + " " + numberKeys[1]);

		/*** Calcular chaves e partições maiores ***/
		Estrutura menores = new Estrutura();
		menores.inputPath = item.outputPath + item.depth + "/part-00000";
		menores.outputPath = item.outputPath;
		menores.depth = ++contador;
		menores.numberKeys = numberKeys[0];
		menores.numberPartitions = Math.max(Math.min(
				Math.round(menores.numberKeys * item.numberPartitions / item.numberKeys),
				item.numberPartitions - 1), 1);
		menores.print();

		Estrutura maiores = new Estrutura();
		maiores.inputPath = item.outputPath + item.depth + "/part-00001";
		maiores.outputPath = item.outputPath;
		maiores.depth = ++contador;
		maiores.numberKeys = numberKeys[1];
		maiores.numberPartitions = item.numberPartitions - menores.numberPartitions;
		maiores.print();

		Stack<Estrutura> s = new Stack<>();
		if (maiores.numberKeys > 0) {
			s.push(maiores);
		}
		if (menores.numberKeys > 0) {
			s.push(menores);
		}
		return s;
		/*
		 * 3.7 if (numParticoesMaiores > 1) push(nomeParticaoMaior, // *
		 * numChavesMaiores, numParticoesMaiores)
		 */
		// if (numPartitionLarger > 1) {
		// stack.push(new Estrutura(numPartitionLarger, path1.toString(),
		// numberKeysLarger));
		// System.out.println("Push(" + numPartitionLarger + ", " +
		// path1.toString() + ", " + numberKeysLarger + ")");
		// }
		// /* 3.8 if (numParticoesMenores > 1) push(nomeParticaoMenor,
		// * numChavesMenores, numParticoesMenores) */
		// if (numPartitionSmaller > 1) {
		// stack.push(new Estrutura(numPartitionSmaller, path0.toString(),
		// numberKeysSmaller));
		// System.out.println("Push(" + numPartitionSmaller + ", " +
		// path0.toString() + ", " + numberKeysSmaller + ")");
		// }
		// }

		// JobConf conf = new JobConf();
		// FileSystem fs = FileSystem.get(conf);
		// String outputPath = "input";
		// Path outputPathFS = new Path(outputPath);
		//
		// int count = 0;
		//
		// /* 3. Enquanto a pilha não estiver vazia */
		//
		// while (!stack.empty()) {
		//
		// // recuperar nomeArquivoFS, numChaves, numParticoes 3.1 pop
		// Estrutura estrutura = stack.pop();
		// System.out.println("Pop(" + estrutura.numberPartitions + ", " +
		// estrutura.fileNameFS + ", " + estrutura.numberKeys + ")");
		//
		// /* 3.2 seleção do pivô */
		// Path pivotFileFS = null;
		// for (FileStatus inputPathFS : fs.listStatus(new
		// Path(estrutura.fileNameFS))) {
		// System.out.println(inputPathFS.getPath().toString());
		// if (!inputPathFS.isDir()) {// &&
		// inputPathFS.getPath().toString().contains("File")) {
		// pivotFileFS = inputPathFS.getPath();
		// }
		// }
		//
		// SequenceFile.Reader reader = new SequenceFile.Reader(fs, pivotFileFS,
		// conf);
		// WritableComparable pivot = (WritableComparable)
		// reader.getKeyClass().newInstance();
		//
		// Writable value = (Writable) reader.getValueClass().newInstance();
		//
		// reader.next(pivot, value);
		// System.out.println("pivot: " + pivot.toString() + " ->" +
		// value.toString() +"<-");
		//
		// reader.close();
		//
		//
		// /* 3.3 realizar a divisão */
		// WritableComparable key = (WritableComparable)
		// reader.getKeyClass().newInstance();
		// // Dividir os arquivos de acordo com o pivot
		//
		// /* 3.4 contar as chaves */
		// int numberKeysSmaller = 0, numberKeysLarger = 0;
		//
		// Path path0 = new Path(outputPathFS, "Parte" + count++);
		// Path path1 = new Path(outputPathFS, "Parte" + count++);
		//
		// SequenceFile.Writer output0 = new SequenceFile.Writer(fs, conf,
		// path0,
		// DoubleWritable.class, Text.class);
		// SequenceFile.Writer output1 = new SequenceFile.Writer(fs, conf,
		// path1,
		// DoubleWritable.class, Text.class);
		// for (FileStatus inputPathFS : fs.listStatus(new
		// Path(estrutura.fileNameFS))) {
		// if (!inputPathFS.isDir()) {// &&
		// inputPathFS.getPath().toString().contains("File")) {
		// reader = new SequenceFile.Reader(fs, inputPathFS.getPath(), conf);
		// System.out.println("Lendo: " + inputPathFS.getPath().toString());
		// while (reader.next(key, value)) {
		//
		// if (key.compareTo(pivot) <= 0) {
		// // System.out.println("Key: " + key.toString() + " < Pivot: " +
		// pivot.toString());
		// output0.append(key, value);
		// numberKeysSmaller++;
		// } else {
		// output1.append(key, value);
		// numberKeysLarger++;
		// // System.out.println("Key: " + key.toString() + " > Pivot: " +
		// pivot.toString());
		// }
		// }
		// reader.close();
		// }
		// }
		// System.out.println("Escrevedo: " + path0 + " e " + path1);
		// output0.close();
		// output1.close();
		// System.out.println("Apagando: " + estrutura.fileNameFS);
		// fs.delete(new Path(estrutura.fileNameFS), true);
		//
		// /* sabendo o número de elementos em cada arquivo, o próximo passo é
		// * definir quantas divisões ainda devem ser feitas para cada um deles
		// */
		//
		// /* 3.5 numParticoesMaiores = max(numChavesMaiores x numParticoes/
		// * numChaves, 1) */
		// int numPartitionLarger = Math.max(numberKeysLarger *
		// estrutura.numberPartitions / estrutura.numberKeys, 1);
		//
		// System.out.println("KeysL : " + numberKeysLarger);
		// System.out.println("PartL : " + numPartitionLarger);
		// /* 3.6 numParticoesMenores = numParticoes - numParticoesMaiores */
		// int numPartitionSmaller = estrutura.numberPartitions -
		// numPartitionLarger;
		// System.out.println("KeysS : " + numberKeysSmaller);
		// System.out.println("PartS : " + numPartitionSmaller);
		//
		// /* 3.7 if (numParticoesMaiores > 1) push(nomeParticaoMaior,
		// * numChavesMaiores, numParticoesMaiores) */
		// if (numPartitionLarger > 1) {
		// stack.push(new Estrutura(numPartitionLarger, path1.toString(),
		// numberKeysLarger));
		// System.out.println("Push(" + numPartitionLarger + ", " +
		// path1.toString() + ", " + numberKeysLarger + ")");
		// }
		// /* 3.8 if (numParticoesMenores > 1) push(nomeParticaoMenor,
		// * numChavesMenores, numParticoesMenores) */
		// if (numPartitionSmaller > 1) {
		// stack.push(new Estrutura(numPartitionSmaller, path0.toString(),
		// numberKeysSmaller));
		// System.out.println("Push(" + numPartitionSmaller + ", " +
		// path0.toString() + ", " + numberKeysSmaller + ")");
		// }
		// }
	}

	public class Estrutura {

		// Número esperado de partições
		int numberPartitions;
		// Arquivo
		String inputPath, outputPath;
		// Chaves
		int numberKeys;
		int depth;

		public Estrutura() {
		}

		public Estrutura(int numberPartitions, String inputPath, String outputPath, int numberKeys) {
			super();
			this.numberPartitions = numberPartitions;
			this.inputPath = inputPath;
			this.outputPath = outputPath;
			this.numberKeys = numberKeys;
		}

		public void print() {
			System.out.println();
			System.out.println("PARTIÇÃO: " + depth);
			System.out.println("Número de partições desejadas: " + numberPartitions);
			System.out.println("Número de chaves: " + numberKeys);
			System.out.println("Diretório Entrada: " + inputPath);
			System.out.println();
		}
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
// seta os tipos dos arquivos de entrada e saida

/*
 * conf.setOutputKeyClass(DoubleWritable.class);
 * conf.setOutputValueClass(Text.class);
 * 
 * conf.setMapperClass(QuickSortMapper.class); conf.setNumReduceTasks(0); AQUI
 * TENHO QUE: ESCOLHER UM PIVO MAP PERSONALIZADO PARTITIONER PERSONALIZADO
 * 
 * O MAP CONTA O NÚMERO DE LINHAS DO ARQUIVO SETA UMA VARIÁVEL NUMEROLINHAS
 */

// int depth = 1;
// Configuration conf = new Configuration();
// conf.set("recursion.depth", depth + "");
// JobConf job = new Job(conf);
// job.setJobName("Graph explorer");
//
// job.setMapperClass(DatasetImporter.class);
// job.setReducerClass(ExplorationReducer.class);
// job.setJarByClass(DatasetImporter.class);
//
// Path in = new Path("files/graph-exploration/import/");
// Path out = new Path("files/graph-exploration/depth_1");
//
// FileInputFormat.addInputPath(job, in);
// FileSystem fs = FileSystem.get(conf);
// if (fs.exists(out)) {
// fs.delete(out, true);
// }
//
// SequenceFileOutputFormat.setOutputPath(job, out);
// job.setInputFormatClass(TextInputFormat.class);
// job.setOutputFormatClass(SequenceFileOutputFormat.class);
// job.setOutputKeyClass(LongWritable.class);
// job.setOutputValueClass(VertexWritable.class);
//
// job.waitForCompletion(true);
//
// long counter = job.getCounters()
// .findCounter(ExplorationReducer.UpdateCounter.UPDATED)
// .getValue();
// depth++;
//
// while (counter > 0) {
// conf = new Configuration();
// conf.set("recursion.depth", depth + "");
// job = new Job(conf);
// job.setJobName("Graph explorer " + depth);
//
// job.setMapperClass(ExplorationMapper.class);
// job.setReducerClass(ExplorationReducer.class);
// job.setJarByClass(ExplorationMapper.class);
//
// in = new Path("files/graph-exploration/depth_" + (depth - 1) + "/");
// out = new Path("files/graph-exploration/depth_" + depth);
//
// SequenceFileInputFormat.addInputPath(job, in);
// if (fs.exists(out)) {
// fs.delete(out, true);
// }
//
// SequenceFileOutputFormat.setOutputPath(job, out);
// job.setInputFormatClass(SequenceFileInputFormat.class);
// job.setOutputFormatClass(SequenceFileOutputFormat.class);
// job.setOutputKeyClass(LongWritable.class);
// job.setOutputValueClass(VertexWritable.class);
//
// job.waitForCompletion(true);
// depth++;
// counter = job.getCounters()
// .findCounter(ExplorationReducer.UpdateCounter.UPDATED)
// .getValue();
// }

