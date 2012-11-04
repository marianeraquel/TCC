/**
 * @file    GeraDados.java
 * @brief   Gera arquivos de numeros inteiros no formato binario.
 * @author  Paula Pinhao
 * @year    2011
 * @version 1.0
*/

package geradados;

// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
// </editor-fold>

public class GeraDados
{
    // <editor-fold defaultstate="collapsed" desc="Gera Dados">
    /** Gera um arquivo com os dados inteiros
     * @param path diretorio onde os arquivos devem ser gerados
     * @param potencia numero de dados (10^potencia) a serem gerados
     * @param num_cenarios numero de cenarios de testes a serem gerados
     * @param num_arquivos numero de arquivos por cenario
     * @throws IOException
     */
    protected void geraDados(String path, int potencia, int num_cenarios, int num_arquivos) throws IOException
    {
        JobConf conf = new JobConf();

        long num_dados = (long)Math.pow(10.0, potencia);
        //int num_dados = potencia;

        // cria o diretorio pai
        FileSystem.mkdirs(FileSystem.get(conf), new Path(path), FsPermission.getDefault());

        for(int j = 1; j <= num_cenarios; j++)
        {
             // cria os diretorios filhos
            String nome_dir_filho = "10_"+potencia+"_int"+j;
            //String nome_dir_filho = potencia +"_int"+j;
            FileSystem.mkdirs(FileSystem.get(conf), new Path(path, nome_dir_filho), FsPermission.getDefault());
            Path dir_filho = new Path(path, nome_dir_filho);

            for(int k = 1; k <= num_arquivos; k++)
            {
                 // cria os arquivos
                String nome_arquivo = nome_dir_filho + "-in" + k;
                Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(dir_filho, nome_arquivo), IntWritable.class, Text.class);

                int valores_inteiros;
                Random gerador_aleatorio = new Random();

                // gera os dados e grava os arquivos
                int i;

                long dados_por_divisao = num_dados / num_arquivos;
                for(i = 0; i < dados_por_divisao; i++)
                {
                    valores_inteiros = Math.abs(gerador_aleatorio.nextInt());

                    writer.append(new IntWritable(valores_inteiros), new Text(""));
                }

                writer.close();
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Configuracoes">
    /** Inicializa o job e seta as configuracoes a serem utilizadas pela plataforma
     * @param args[0] nome do diretorio de saida
     * @param args[1] numero de dados a serem gerados
     * @param args[2] numero de arquivos a serem gerados
     * @return -1 (erro) ou 0 (sucesso)
     * @throws IOException
     */
    public int run(String[] args) throws IOException
    {
        if (args.length != 4)
        {
            System.err.printf("Uso: %s [opcoes genericas] <dir> <num_dados><num_arq>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        JobConf conf = new JobConf();
        conf.setJobName("GeraDados");

        // formato do arquivo de saida
        conf.setOutputFormat(SequenceFileOutputFormat.class);

        geraDados(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        
        return 0;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main">
    /** Recebe os parametros
     * @param args[0] nome do diretorio de saida
     * @param args[1] numero de dados a serem gerados
     * @param args[2] numero de arquivos a serem gerados
     * @return -1 (erro) ou 0 (sucesso)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        new GeraDados().run(args);
    }
    // </editor-fold>
}
