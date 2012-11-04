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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
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
    final static int num_dados = 5;
    final static int valor_max = 20;

    // <editor-fold defaultstate="collapsed" desc="Gera dados">
    /** Gera um arquivo com os dados inteiros a serem ordenados
     * @param path diretorio onde o arquivo deve ser gerado
     * @param num_dados numero de dados a serem gerados
     * @param valor_max valor maximo dos valores gerados
     * @throws IOException
     */
    protected void geraDados(String path, int num_dados, int valor_max) throws IOException
    {
        JobConf conf = new JobConf();
        //Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(path), Text.class, Text.class);
        //SequenceFile.Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(path), Text.class, Text.class);

        File dir = new File(path);
        dir.mkdir();

        File arq = new File(dir, "teste.txt");

        FileOutputStream saida = new FileOutputStream(arq);
        String valores_inteiros;

        for(int i = 0; i < num_dados; i++)
        {      
            valores_inteiros = (int)((Math.random() * valor_max) + 1) + " ";

            saida.write(valores_inteiros.getBytes());

            //writer.append(new Text(), new Text(valores_inteiros));

            System.out.print(valores_inteiros);
	}

        System.out.println("");
        saida.close();
        //writer.close();
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Comparacoes">
    /** Classe Comparacao de Chaves
     * Compara as chaves de cada particao
     */
    public final class ComparacaoChaves implements RawComparator<Text>
    {
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
        {
            return 0;
        }

        public int compare(Text o1, Text o2)
        {
            return 0;
        }
    }

    /** Classe Comparacao de Valores
     * Compara os valores de cada particao e assim determina para qual reducer
     * a saida do mapper deve ir (ordena a entrada do reducer)
     */
    public final class ComparacaoValores implements RawComparator<IntWritable>
    {
        /** Compara dois inteiros - chave = numero da particao
         * @param b1
         * @param s1 offset do atributo 1
         * @param l1 tamanho do atributo 1
         * @param b2
         * @param s2 offset do atributo 2
         * @param l2 tamanho do atributo 2
         * @return
        */
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
        {
            int a1 = Integer.parseInt(new String(b1, s1, l1));
            int a2 = Integer.parseInt(new String(b2, s2, l2));
            
            if(a1 > a2) { return 1; }
            else if(a1 < a2) { return -1; }
            else { return 0; }
        }

        public int compare(IntWritable o1, IntWritable o2)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Particionamento">
    /** Classe de Particionamento
     * Implementa os metodos da interface Partitioner
     */
    public static final class Particionamento implements Partitioner<Text, IntWritable>
    {
        /** Determina a faixa de valores das particoes
         * @param key chave
         * @param value valor inteiro
         * @param numPartitions numero de particoes
         * @return numero da particao para a qual o dado sera deslocado
         */
        public int getPartition(Text key, IntWritable value, int numPartitions)
        {
            System.err.println("gpart: k:"+key +" v:"+value);
            int faixa = (value.get() / (valor_max / numPartitions + 1));
            System.err.println("v/np: "+faixa);
            return faixa;
        }

        public void configure(JobConf job) {}
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Mapeamento">
    /** Classe de Mapeamento
     * Implementa os metodos da interface Mapper
     */
    public static final class Mapeamento implements Mapper<LongWritable, Text, Text, IntWritable>
    {
        /** Le o arquivo e forma os pares (chave, valor)
         * @param key byte offset da linha do arquivo
         * @param value conteudo da linha do arquivo
         * @param collector pares (key, value)
         * @param reporter informacoes sobre a tarefa atual
         * @throws IOException
         */
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> collector, Reporter reporter) throws IOException
        {
            System.out.println("map: " + "k:"+key +"  v:"+ value);

            String[] valores_inteiros = value.toString().split(" ");
           
            /*
            int[] valores_inteiros = null;
            for(int j = 0; j < valores_linha.length; j++)
            {
                valores_inteiros[j] = Integer.parseInt(valores_linha[j]);
            }
            */

            for(int i = 0; i < valores_inteiros.length; i++)
            {
                System.out.println("ac");
                collector.collect(new Text(), new IntWritable(Integer.parseInt(valores_inteiros[i])));
                System.out.println("dc: "+collector);
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
    public static final class Reducao implements Reducer<Text, Iterator, Text, Text>
    {
        /** Concatena as particoes, uma vez que ja estao ordenadas
         * @param key
         * @param values
         * @param collector
         * @param reporter
         * @throws IOException
         */
        public void reduce(Text key, Iterator values, OutputCollector collector, Reporter reporter) throws IOException
        {

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
        if (args.length != 2)
        {
            System.err.printf("Uso: %s [opcoes genericas] <entrada> <saida>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }
        else
        {
            // carrega os arquivos de configuracao da pasta conf do Hadoop
            JobConf conf = new JobConf();
            conf.setJobName("Quick Sort Haddop");

            // especifica os formatos (chave, valor) dos arquivos de entrada e saida
            conf.setInputFormat(TextInputFormat.class);
            conf.setOutputFormat(TextOutputFormat.class);

            // especifica os tipos de chave e valor dos pares intermediarios (mapper)
            conf.setMapOutputKeyClass(Text.class);
            conf.setMapOutputValueClass(IntWritable.class);

            // especifica os tipos de chave e valor dos pares finais resultantes (reducer)
            conf.setOutputKeyClass(Text.class);
            conf.setOutputValueClass(Text.class);

            // especifica as classes de mapeamento e de reducao
            conf.setMapperClass(Mapeamento.class);
            conf.setReducerClass(Reducao.class);

            // especifica as classes para implementacao da ordenacao
            conf.setOutputKeyComparatorClass(ComparacaoChaves.class);
            conf.setOutputValueGroupingComparator(ComparacaoValores.class);
            conf.setPartitionerClass(Particionamento.class);
            
            // especifica os caminhos dos arquivos de entrada e saida no HDFS
            FileInputFormat.addInputPath(conf, new Path(args[0]));
            FileOutputFormat.setOutputPath(conf, new Path(args[1]));

            // apaga arquivos de entrada ou saida se ja existirem
            //conf.getWofinal int num_dados = 5;rkingDirectory().getFileSystem(conf).delete(new Path(args[0]), true);
            conf.getWorkingDirectory().getFileSystem(conf).delete(new Path(args[1]), true);

            geraDados(args[0], num_dados, valor_max);

            // submete o job a execucao
            // bloqueia o programa ate que a execucao termine
            JobClient.runJob(conf).waitForCompletion();

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
        new QuickSortHadoop().run(args);
    }
    // </editor-fold>
}