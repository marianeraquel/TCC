///**
// * bin/hadoop jar ~/Dropbox/TCC/Algoritmos/algoritmos\ tcc\
// * 2/QuickSort/dist/QuickSort.jar <folder> <particoes>
// */
//package quicksort;
//
//import java.util.Stack;
//import org.apache.hadoop.conf.Configured;
//import org.apache.hadoop.fs.FileStatus;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.DoubleWritable;
//import org.apache.hadoop.io.FloatWritable;
//import org.apache.hadoop.io.SequenceFile;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.Writable;
//import org.apache.hadoop.io.WritableComparable;
//import org.apache.hadoop.io.compress.GzipCodec;
//import org.apache.hadoop.mapred.FileInputFormat;
//import org.apache.hadoop.mapred.FileOutputFormat;
//import org.apache.hadoop.mapred.JobClient;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.SequenceFileInputFormat;
//import org.apache.hadoop.mapred.SequenceFileOutputFormat;
//import org.apache.hadoop.util.Tool;
//import org.apache.hadoop.util.ToolRunner;
//
///**
// *
// * @author Raquel
// */
//public class QuickSort extends Configured implements Tool {
//
//    public static void main(String[] args) throws Exception {
//        int exitCode = ToolRunner.run(new QuickSort(), args);
//        System.exit(exitCode);
//    }
//
//    public void printAndExit(String s) {
//        System.err.println(s);
//        System.exit(2);
//    }
//
//    static void usage() {
//        System.out.println("Usage : QuickSort <input path> <partitions>");
//        System.exit(1);
//    }
//
//    @Override
//    public int run(String[] args) throws Exception {
//        System.out.println();
//        JobConf conf = new JobConf();
//
//        // String[] otherArgs = new GenericOptionsParser(jobconf,
//        // args).getRemainingArgs();
//        if (args.length != 2) {
//            printAndExit("Erro! Uso: quicksort <diretorio-entrada> <particoes>");
//        }
//
//        // Diretório contendo os arquivos
//        String inputFilePath = args[0];
//
//        // Número de partições
//        Integer partitions = Integer.parseInt(args[1]);
//
//        // Pilha para armazenar a estrutura auxiliar
//        Stack<Estrutura> stack;
//        stack = new Stack<>();
//
//        // seta os tipos dos arquivos de entrada e saida
//        conf.setInputFormat(SequenceFileInputFormat.class);
//        conf.setOutputFormat(SequenceFileOutputFormat.class);
//
//        /* 1. Ler arquivos de entrada */
//
//        // objeto file system
//        FileSystem fs = FileSystem.get(conf);
//        int numberKeys = 0;
//
//        // ler diretório de entrada
//        System.out.println("Lendo arquivos do diretório " + inputFilePath);
//
//        // Arquivo do pivo
//        Path pivotFileFS = null;
//
//        for (FileStatus inputPathFS : fs.listStatus(new Path(inputFilePath))) {
//
//            if (!inputPathFS.isDir() && inputPathFS.getPath().toString().contains("File")) {
//                pivotFileFS = inputPathFS.getPath();
//                Process prcs = Runtime.getRuntime().exec("lineCounter.sh " + inputPathFS.getPath().toString());
//                prcs.waitFor();
//                numberKeys += prcs.exitValue();
//            }
//        }
//
//        System.out.println("Num linhas: " + numberKeys);
//
//        String inputPath = "input";
//        Path inputPathFS = new Path(inputPath);
//        fs.mkdirs(inputPathFS);
//        
//        String outputPath = "output";
//        Path outputPathFS = new Path(outputPath);
//        
//        fs.delete(inputPathFS, true);
//        fs.delete(outputPathFS, true);
//         
//        conf.setNumReduceTasks(partitions);
//         
//        /* 2. push(nomeArquivoFS, numChaves, numParticoes) */
//        stack.push(new Estrutura(partitions, inputFilePath, numberKeys));
//
//        novaFuncao(stack);
//
//        System.out.println();
//        System.out.println();
//
//
//
//
//        
//
//        FileOutputFormat.setOutputPath(conf, outputPathFS);
//        FileInputFormat.addInputPath(conf, inputPathFS);
//
//        SequenceFileOutputFormat.setCompressOutput(conf, true);
//        SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
//        SequenceFileOutputFormat.setOutputCompressionType(conf, SequenceFile.CompressionType.BLOCK);
//        conf.setOutputKeyClass(DoubleWritable.class);
//        
//        JobClient.runJob(conf);
//
//
////			if (inputPathFS.isDir()) {
////				conf = new JobConf(new Path(inputPathFS.getPath().toString()));
////
////				// seta os tipos dos arquivos de entrada e saida
////				conf.setNumReduceTasks(partitions);
////
////				// seta a classe de particionamento
////				conf.setPartitionerClass(TotalOrderPartitioner.class);
////
////				// especifica o caminho do arquivo de saida no HDFS
////				String ouputPath = inputPathFS.getPath().toString() + "-out";
////
////				// especifica o caminho dos arquivos de entrada e saída no HDFS
////				FileInputFormat.addInputPath(conf, new Path(inputPathFS
////						.getPath().toString()));
////				FileOutputFormat.setOutputPath(conf, new Path(ouputPath));
////
////				// apaga arquivo de saida se ja existir no HDFS
////				conf.getWorkingDirectory().getFileSystem(conf)
////						.delete(new Path(ouputPath), true);
////
////			}
//
//        /**
//         * ************* Para ajudar a implementação ****************
//         *
//         * 1. Ler os arquivos de entrada (hadoop file system)
//         *
//         * 2. push(nomeArquivoFS, numChaves, numParticoes) // numParticoes
//         * indica quantas divisões devem ser feitas ao todo naquele arquivo
//         *
//         * 3. Enquanto a pilha não estiver vazia
//         *
//         * // recuperar nomeArquivoFS, numChaves, numParticoes 3.1 pop
//         *
//         * 3.2 seleção do pivô
//         *
//         * 3.3 realizar a divisão // o hadoop fornece a classe partitioner para
//         * divisão de arquivos, mas é preciso verificar o nome dos arquivos
//         * gerados
//         *
//         * 3.4 contar as chaves: numChavesMaiores e numChavesMenores // pode-se
//         * usar a função do linux para contar as linhas, mas seria melhor se
//         * encontrasse uma função própria dentro do hadoop
//         *
//         *
//         * // sabendo o número de elementos em cada arquivo, o próximo passo é
//         * definir quantas divisões ainda devem ser feitas para cada um deles
//         *
//         * 3.5 numParticoesMaiores = max(numChavesMaiores x numParticoes/
//         * numChaves, 1)
//         *
//         * 3.6 numParticoesMenores = numParticoes - numParticoesMaiores
//         *
//         * // usar o max (valor, 1) garante que mesmo que o número de chaves
//         * seja muito pequena, haverá uma partição para aqueles dados. se não
//         * houvesse essa restrição, caso o número de elementos fosse muito
//         * pequeno, o número determinado pelo cálculo poderia ser arrendondado
//         * para zero
//         *
//         * 3.7 if (numParticoesMaiores > 1) push(nomeParticaoMaior,
//         * numChavesMaiores, numParticoesMaiores)
//         *
//         * 3.8 if (numParticoesMenores > 1) push(nomeParticaoMenor,
//         * numChavesMenores, numParticoesMenores)
//         *
//         * // nesse ponto os arquivos estão divididos, e devem ser ordenados.
//         *
//         * 4. Iniciar as tarefas map/reduce para realizar a ordenação de cada
//         * arquivo
//         */
//        // FileSystem diretorio = FileSystem.get(conf);
//        // System.out.println(" Diretorio. " +
//        // diretorio.getWorkingDirectory());
///*
//         Configuration dconf = new Configuration();
//         // conf.set("fs.default.name", "hdfs://localhost:9000");
//         fs = FileSystem.get(dconf);
//
//         FileStatus[] fileStatus = fs.listStatus(fs.getHomeDirectory());
//         for (FileStatus status : fileStatus) {
//         System.out.println("File: " + status.getPath());
//         }
//
//         if (args.length != 2) {
//         usage();
//         }
//
//         // HadoopDFS deals with Path
//         Path inFile = new Path(args[0]);
//         Path outFile = new Path(args[1]);
//
//         // Check if input/output are valid
//         if (!fs.exists(inFile)) {
//         printAndExit("Input file not found");
//         }
//         if (!fs.isFile(inFile)) {
//         printAndExit("Input should be a file");
//         }
//         if (fs.exists(outFile)) {
//         printAndExit("Output already exists");
//         }
//
//         // Read from and write to new file
//         FSDataInputStream in = fs.open(inFile);
//         FSDataOutputStream out = fs.create(outFile);
//         byte buffer[] = new byte[256];
//         try {
//         int bytesRead = 0;
//         while ((bytesRead = in.read(buffer)) > 0) {
//         out.write(buffer, 0, bytesRead);
//         }
//
//         } catch (IOException e) {
//         System.out.println("Error while copying file");
//         } finally {
//         in.close();
//         out.close();
//         }
//
//       
//         */
//        System.out.println();
//        return 0;
//    }
//
//    public void novaFuncao(Stack<Estrutura> stack) throws Exception {
//        JobConf conf = new JobConf();
//        FileSystem fs = FileSystem.get(conf);
//        String outputPath = "input";
//        Path outputPathFS = new Path(outputPath);
//
//        int count = 0;
//        /* 3. Enquanto a pilha não estiver vazia */
//        while (!stack.empty()) {
//
//            // recuperar nomeArquivoFS, numChaves, numParticoes 3.1 pop
//            Estrutura estrutura = stack.pop();
//            System.out.println("Pop(" + estrutura.numberPartitions + ", " + estrutura.fileNameFS + ", " + estrutura.numberKeys + ")");
//
//            /* 3.2 seleção do pivô */
//            Path pivotFileFS = null;
//            for (FileStatus inputPathFS : fs.listStatus(new Path(estrutura.fileNameFS))) {
//                System.out.println(inputPathFS.getPath().toString());
//                if (!inputPathFS.isDir()) {// && inputPathFS.getPath().toString().contains("File")) {
//                    pivotFileFS = inputPathFS.getPath();
//                }
//            }
//
//            SequenceFile.Reader reader = new SequenceFile.Reader(fs, pivotFileFS, conf);
//            WritableComparable pivot = (WritableComparable) reader.getKeyClass().newInstance();
//
//            Writable value = (Writable) reader.getValueClass().newInstance();
//
//            reader.next(pivot, value);
//            System.out.println("pivot: " + pivot.toString() + " ->" + value.toString() +"<-");
//
//            reader.close();
//
//
//            /* 3.3 realizar a divisão */
//            WritableComparable key = (WritableComparable) reader.getKeyClass().newInstance();
//            // Dividir os arquivos de acordo com o pivot
//
//            /* 3.4 contar as chaves */
//            int numberKeysSmaller = 0, numberKeysLarger = 0;
//
//            Path path0 = new Path(outputPathFS, "Parte" + count++);
//            Path path1 = new Path(outputPathFS, "Parte" + count++);
//
//            SequenceFile.Writer output0 = new SequenceFile.Writer(fs, conf, path0,
//                    DoubleWritable.class, Text.class);
//            SequenceFile.Writer output1 = new SequenceFile.Writer(fs, conf, path1,
//                    DoubleWritable.class, Text.class);
//            for (FileStatus inputPathFS : fs.listStatus(new Path(estrutura.fileNameFS))) {
//                if (!inputPathFS.isDir()) {// && inputPathFS.getPath().toString().contains("File")) {
//                    reader = new SequenceFile.Reader(fs, inputPathFS.getPath(), conf);
//                    System.out.println("Lendo: " + inputPathFS.getPath().toString());
//                    while (reader.next(key, value)) {
//
//                        if (key.compareTo(pivot) <= 0) {
////                            System.out.println("Key: " + key.toString() + " < Pivot: " + pivot.toString());
//                            output0.append(key, value);
//                            numberKeysSmaller++;
//                        } else {
//                            output1.append(key, value);
//                            numberKeysLarger++;
////                            System.out.println("Key: " + key.toString() + " > Pivot: " + pivot.toString());
//                        }
//                    }
//                    reader.close();
//                }
//            }
//            System.out.println("Escrevedo: " + path0 + " e " + path1);
//            output0.close();
//            output1.close();
//            System.out.println("Apagando: " + estrutura.fileNameFS);
//            fs.delete(new Path(estrutura.fileNameFS), true);
//
//            /* sabendo o número de elementos em cada arquivo, o próximo passo é
//             * definir quantas divisões ainda devem ser feitas para cada um deles */
//
//            /* 3.5 numParticoesMaiores = max(numChavesMaiores x numParticoes/
//             * numChaves, 1) */
//            int numPartitionLarger = Math.max(numberKeysLarger * estrutura.numberPartitions / estrutura.numberKeys, 1);
//
//            System.out.println("KeysL : " + numberKeysLarger);
//            System.out.println("PartL : " + numPartitionLarger);
//            /* 3.6 numParticoesMenores = numParticoes - numParticoesMaiores */
//            int numPartitionSmaller = estrutura.numberPartitions - numPartitionLarger;
//            System.out.println("KeysS : " + numberKeysSmaller);
//            System.out.println("PartS : " + numPartitionSmaller);
//
//            /* 3.7 if (numParticoesMaiores > 1) push(nomeParticaoMaior,
//             * numChavesMaiores, numParticoesMaiores) */
//            if (numPartitionLarger > 1) {
//                stack.push(new Estrutura(numPartitionLarger, path1.toString(), numberKeysLarger));
//                System.out.println("Push(" + numPartitionLarger + ", " + path1.toString() + ", " + numberKeysLarger + ")");
//            }
//            /* 3.8 if (numParticoesMenores > 1) push(nomeParticaoMenor,
//             * numChavesMenores, numParticoesMenores) */
//            if (numPartitionSmaller > 1) {
//                stack.push(new Estrutura(numPartitionSmaller, path0.toString(), numberKeysSmaller));
//                System.out.println("Push(" + numPartitionSmaller + ", " + path0.toString() + ", " + numberKeysSmaller + ")");
//            }
//        }
//
//    }
//
//    public class Estrutura {
//
//        // Número esperado de partições
//        int numberPartitions;
//        // Arquivo
//        String fileNameFS;
//        // Chaves
//        int numberKeys;
//
//        public Estrutura(int numberPartitions, String fileNameFS, int numberKeys) {
//            this.numberPartitions = numberPartitions;
//            this.fileNameFS = fileNameFS;
//            this.numberKeys = numberKeys;
//        }
//    }
//}