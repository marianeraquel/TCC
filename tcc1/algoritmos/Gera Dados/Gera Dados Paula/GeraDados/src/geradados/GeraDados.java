///**
// * @file GeraDados.java @brief Gera arquivos de numeros inteiros no formato
// * binario.
// *
// * @author Paula Pinhao @year 2011
// * @version 1.0
// */
//package geradados;
//
//// <editor-fold defaultstate="collapsed" desc="Bibliotecas">
//import java.io.IOException;
//import java.util.Random;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.fs.permission.FsPermission;
//import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.SequenceFile;
//import org.apache.hadoop.io.SequenceFile.Writer;
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.SequenceFileOutputFormat;
//import org.apache.hadoop.util.ToolRunner;
//// </editor-fold>
//
//public class GeraDados {
//
//    /**
//     * Gera um arquivo com os dados inteiros
//     *
//     * @param path diretorio onde os arquivos devem ser gerados
//     * @param potencia numero de dados (10^potencia) a serem gerados
//     * @param num_cenarios numero de cenarios de testes a serem gerados
//     * @param num_arquivos numero de arquivos por cenario
//     * @throws IOException
//     */
//    protected void geraDados(String path, int potencia, int num_cenarios, int num_arquivos) throws IOException {
//        JobConf conf = new JobConf();
//
//        long num_dados = (long) Math.pow(10.0, potencia);
//        System.out.println("Gerando " + num_dados + " em " + num_arquivos);
//        //int num_dados = potencia;
//
//        int[] dados_arquivo;
//        dados_arquivo = new int[(int) num_dados];
//        // cria o diretorio pai
//        FileSystem.mkdirs(FileSystem.get(conf), new Path(path), FsPermission.getDefault());
//
//        for (int j = 1; j <= num_cenarios; j++) {
//            // cria os diretorios filhos
//            String nome_dir_filho = "Uniforme" + potencia + "_Folder" + j;
//            //String nome_dir_filho = potencia +"_int"+j;
//            FileSystem.mkdirs(FileSystem.get(conf), new Path(path, nome_dir_filho), FsPermission.getDefault());
//            Path dir_filho = new Path(path, nome_dir_filho);
//
//            for (int k = 1; k <= num_arquivos; k++) {
//                // cria os arquivos
//                String nome_arquivo = nome_dir_filho + "-File" + k;
//                Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, new Path(dir_filho, nome_arquivo), IntWritable.class, Text.class);
//
//                int valores_inteiros;
//                Random gerador_aleatorio = new Random();
//
//                // gera os dados e grava os arquivos
//                int i;
//
//                long dados_por_divisao = num_dados / num_arquivos;
//                for (i = 0; i < dados_por_divisao; i++) {
//                    dados_arquivo[i] = Math.abs(gerador_aleatorio.nextInt());
//
//                }
//                for (i = 0; i < dados_por_divisao; i++) {
//
//
//                    writer.append(new IntWritable(dados_arquivo[i]), new Text(""));
//                }
//                writer.close();
//            }
//        }
//    }
//
//    /**
//     * Inicializa o job e seta as configuracoes a serem utilizadas pela
//     * plataforma
//     *
//     * @param args[0] nome do diretorio de saida
//     * @param args[1] numero de dados a serem gerados
//     * @param args[2] numero de arquivos a serem gerados
//     * @return -1 (erro) ou 0 (sucesso)
//     * @throws IOException
//     */
//    public int run(String[] args) throws IOException {
//        if (args.length != 4) {
//            System.err.printf("Uso: %s [opcoes genericas] <path> <potencia> <num_cenarios> <num_arquivos>\n", getClass().getSimpleName());
//            ToolRunner.printGenericCommandUsage(System.err);
//            return -1;
//        }
//
//        JobConf conf = new JobConf();
//        conf.setJobName("GeraDados");
//
//        // formato do arquivo de saida
//        conf.setOutputFormat(SequenceFileOutputFormat.class);
//
//        geraDados(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
//
//        return 0;
//    }
//
//    /**
//     * Recebe os parametros
//     *
//     * @param args[0] nome do diretorio de saida
//     * @param args[1] numero de dados a serem gerados
//     * @param args[2] numero de arquivos a serem gerados
//     * @return -1 (erro) ou 0 (sucesso)
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//        new GeraDados().run(args);
//    }
//    // </editor-fold>
//}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
