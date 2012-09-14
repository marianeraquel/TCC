/**
 * @file GeraDados.java @brief Gera arquivos de numeros inteiros no formato
 * binario.
 *
 * @author Paula Pinhao @year 2011
 * @version 1.0
 */
package geradados;

// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
// </editor-fold>

public class GeraDadosUniforme extends Configured implements Tool {
    // <editor-fold defaultstate="collapsed" desc="Gera Dados">

    /**
     * Gera um arquivo com os dados inteiros
     *
     * @param path diretorio onde os arquivos devem ser gerados
     * @param potencia numero de dados (10^potencia) a serem gerados
     * @param cenarios numero de cenarios de testes a serem gerados
     * @param arquivos numero de arquivos por cenario
     * @throws IOException
     */
    protected void geraDadosUniforme(String path, int potencia, int cenarios, int arquivos) throws IOException {
        JobConf conf = new JobConf();

        long num_dados = (long) Math.pow(10.0, potencia);
        FileSystem fs = FileSystem.get(conf);

        for (int j = 0; j < cenarios; j++) {

            // cria os diretorios filhos
            Path dir_filho = new Path(path, "Folder" + j);
            fs.mkdirs(dir_filho);
            System.out.println("Criou " + dir_filho);

            for (int k = 1; k <= arquivos; k++) {
                // cria os arquivos
                String nome_arquivo = "File" + k;
                Path arq = new Path(dir_filho, nome_arquivo);

                SequenceFile.Writer out = new SequenceFile.Writer(fs, conf, arq, IntWritable.class, Text.class);

                // gera os dados e grava os arquivos
                long dados_por_divisao = num_dados / arquivos;
                for (int i = 0; i < dados_por_divisao; i++) {
                    int valores_inteiros = Math.abs(RandomGenerator.uniform(1));
                    out.append(new IntWritable(valores_inteiros), new Text(""));
                }
                out.close();
            }
        }
    }

    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Configuracoes">
    /**
     * Inicializa o job e seta as configuracoes a serem utilizadas pela
     * plataforma
     *
     * @param args[0] nome do diretorio de saida
     * @param args[1] numero de dados a serem gerados
     * @param args[2] numero de arquivos a serem gerados
     * @param args[3] tipo de distribuição
     * @return -1 (erro) ou 0 (sucesso)
     * @throws IOException
     */
    @Override
    public int run(String[] args) throws IOException {

        if (args.length != 5) {
            System.err.printf("Uso: %s [opcoes genericas] <dir> <num_dados><num_arq>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        JobConf conf = new JobConf();
        conf.setJobName("GeraDados");

        // formato do arquivo de saida
        conf.setOutputFormat(SequenceFileOutputFormat.class);

        // especifica os tipos de chave e valor dos pares intermediarios (mapper)
        conf.setMapOutputKeyClass(IntWritable.class);
        conf.setMapOutputValueClass(Text.class);

        // especifica os tipos de chave e valor dos pares finais resultantes (reducer)
        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(IdentityMapper.class);
        conf.setReducerClass(IdentityReducer.class);

        // especifica o caminho do arquivo de saida no HDFS
        FileOutputFormat.setOutputPath(conf, new Path(args[0]));
        System.err.println("Criou diretório.");


        Date tempo_inicio = new Date();

        geraDadosUniforme(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        
        // submete o job a execucao
        //JobClient.runJob(conf);
        Date tempo_fim = new Date();
        long tempo_exec = tempo_fim.getTime() - tempo_inicio.getTime();
        System.err.println("-> Tempo total: " + tempo_exec + " milisegundos.");

        return 0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main">
    /**
     * Recebe os parametros
     *
     * @param args[0] nome do diretorio de saida
     * @param args[1] numero de dados a serem gerados
     * @param args[2] numero de arquivos a serem gerados
     * @return -1 (erro) ou 0 (sucesso)
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new GeraDadosUniforme(), args);
        System.exit(exitCode);
    }
    // </editor-fold>
}
