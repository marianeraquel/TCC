/**
 * @file    QuickSortParalelo.java
 * @brief   Implementa a versão paralela do algoritmo Quicksort, descrita em
 * [Kale e Solomonik 2010], em MapReduce no ambiente Hadoop.
 *
 * @author  Paula Pinhão
 * @year    2011
 * @version 1.0
*/

package quicksorthadoop;

// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
import customkey.IntPair;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
// </editor-fold>

public class QuickSortHadoop
{
    final static int valor_max = 20;
    final static int num_dados = 20;

    // num_maquinas * num_nucleos
    //final static  int num_tarefas_reduce = 4;

    // <editor-fold defaultstate="collapsed" desc="Calcula Particao">
    /**
     * @param valor
     * @return
     */
    public static int calculaParticao(int valor)
    {
        return 0;
        //return valor / ((valor_max / num_tarefas_reduce) + 1);
    }

    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Comparacoes">
    /** Classe Comparacao de Chaves
     * Compara as chaves de cada particao
     */
    public class ComparacaoChaves extends WritableComparator
    {
        protected ComparacaoChaves() { super(IntPair.class, true); }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2)
        {
            System.out.println("compare chave wc");
            
            IntPair pair1 = (IntPair) w1;
            IntPair pair2 = (IntPair) w2;
                    
            int cmp = compare(pair1.getFirst(), pair2.getFirst());
            
            if(cmp != 0) { return cmp; }

            return compare(pair1.getSecond(), pair2.getSecond());
        }
    }

    /** Classe Comparacao de Valores
     * Compara os valores de cada particao e assim determina para qual reducer
     * a saida do mapper deve ir (ordena a entrada do reducer)
     */
    public class ComparacaoValores extends WritableComparator
    {
        protected ComparacaoValores() { super(IntPair.class, true); }

        @Override
        public int compare(WritableComparable w1, WritableComparable w2)
        {         
            IntPair pair1 = (IntPair) w1;
            IntPair pair2 = (IntPair) w2;

            //compara pivo e valores para formar
            // duas particoes: menores e maiores que o pivo
            if(pair1.getSecond() < pair1.getFirst() || pair1.getSecond() < pair1.getFirst())
            {

            }

            return compare(pair1.getFirst(), pair2.getFirst());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Particionamento">
    /** Classe de Particionamento
     * Implementa os metodos da interface Partitioner
     */
    public static final class Particionamento implements Partitioner<IntPair, IntWritable>
    {
        /** Determina a faixa de valores das particoes (mesmo numero de valores em
         * cada particao)
         * @param key chave
         * @param value valor inteiro
         * @param numPartitions numero de particoes = numero de tarefas reduce
         * @return numero da particao para a qual o dado sera deslocado
         */
        public int getPartition(IntPair key, IntWritable value, int numPartitions)
        {
            int contador_elementos = 0;
            int particao_destino = 0;

            int total_elementos_por_particao = num_dados/numPartitions;
            contador_elementos++;
            if(contador_elementos == total_elementos_por_particao)
            {
                contador_elementos =0;
                particao_destino++;
            }

            return particao_destino;
        }

        public void configure(JobConf job) {}
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mapeamento">
    /** Classe de Mapeamento
     * Implementa os metodos da interface Mapper
     */
    public static final class Mapeamento implements Mapper<LongWritable, Text, IntPair, IntWritable>
    {
        /** Le o arquivo e forma os pares (chave, valor)
         * @param key byte offset da linha do arquivo
         * @param value conteudo da linha do arquivo
         * @param collector pares (key, value)
         * @param reporter informacoes sobre a tarefa atual
         * @throws IOException
         */
        public void map(LongWritable key, Text value, OutputCollector<IntPair, IntWritable> collector, Reporter reporter) throws IOException
        {
            // le o arquivo
            String[] linha = value.toString().split(" ");

            // define um pivo
            int pivo = linha.length/2;

            for(int i = 0; i < linha.length; i++)
            {
                // calcula as particoes
                IntWritable valor = new IntWritable(Integer.parseInt(linha[i]));
                collector.collect(new IntPair(new IntWritable(pivo), valor), valor);
            }
        }

        public void configure(JobConf conf) {}

        public void close() throws IOException {}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Reducao">
    /** Classe de Reducao
     * Implementa os metodos da interface Reducer
     */
    public static final class Reducao implements Reducer<IntPair, IntWritable, IntPair, IntWritable>
    {
        /** Concatena as particoes, uma vez que ja estao ordenadas
         * @param key
         * @param values
         * @param collector
         * @param reporter
         * @throws IOException
         */
        public void reduce(IntPair key, Iterator<IntWritable> values, OutputCollector<IntPair, IntWritable> collector, Reporter reporter) throws IOException
        {
	    while(values.hasNext())
            {
                //NullWritable.get()
		collector.collect(key, values.next());
	    }
        }

        public void configure(JobConf conf) {}

        public void close() throws IOException {}
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Configuracoes">
    /** Inicializa o job e seta as configuracoes a serem utilizadas pela plataforma
     * @param args[0] nome do arquivo de entrada
     * @param args[1] nome do arquivo de saida
     * @return -1 (erro) ou 0 (sucesso)
     * @throws IOException
     */
    public int run(String[] args) throws IOException
    {
        if (args.length != 3)
        {
            System.err.printf("Uso: %s [opcoes genericas] <entrada> <saida> <num_processadores>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }
        else
        {
            // carrega os arquivos de configuracao da pasta conf do Hadoop
            JobConf job = new JobConf();
            job.setJobName("Quick Sort Haddop");

            // especifica a classe do programa principal
            job.setJarByClass(QuickSortHadoop.class);

            // numero de tarefas reduce
            //job.setNumReduceTasks(num_tarefas_reduce);
            job.setNumReduceTasks(Integer.getInteger(args[2]));

            // especifica os formatos (chave, valor) dos arquivos de entrada e saida
            job.setInputFormat(TextInputFormat.class);
            job.setOutputFormat(TextOutputFormat.class);

            // especifica os tipos de chave e valor dos pares intermediarios (mapper)
            job.setMapOutputKeyClass(IntPair.class);
            job.setMapOutputValueClass(IntWritable.class);

            // especifica os tipos de chave e valor dos pares finais resultantes (reducer)
            job.setOutputKeyClass(IntPair.class);
            job.setOutputValueClass(IntWritable.class);

            // especifica as classes de mapeamento e de reducao
            job.setMapperClass(Mapeamento.class);
            job.setReducerClass(Reducao.class);

            // especifica as classes para implementacao da ordenacao
            job.setPartitionerClass(Particionamento.class);
            job.setOutputKeyComparatorClass(ComparacaoChaves.class);
            job.setOutputValueGroupingComparator(ComparacaoValores.class);
           
            // especifica os caminhos dos arquivos de entrada e saida no HDFS
            FileInputFormat.addInputPath(job, new Path(args[0]));
            FileOutputFormat.setOutputPath(job, new Path(args[1]));

            // apaga arquivos de entrada ou saida se ja existirem
            //job.getWorkingDirectory().getFileSystem(job).delete(new Path(args[0]), true);
            job.getWorkingDirectory().getFileSystem(job).delete(new Path(args[1]), true);

            // submete o job a execucao
            // bloqueia o programa ate que a execucao termine
            JobClient.runJob(job).waitForCompletion();

            JobConf job1 = new JobConf(job, QuickSortHadoop.class);
            JobConf job2 = new JobConf(job, QuickSortHadoop.class);

            JobClient.runJob(job1);
            JobClient.runJob(job2);

            return 0;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main">
     /** Recebe os nomes dos arquivos
      * @param args[0] nome do arquivo de entrada
      * @param args[1] nome do arquivo de saida
      * @throws IOException
      */
    public static void main(String[] args) throws IOException
    {
        //geraDados(args[0], num_dados, valor_max);
        new QuickSortHadoop().run(args);      
    }
    // </editor-fold>
}