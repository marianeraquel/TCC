/**
 * ~/hadoop/bin/hadoop jar GeradorDados/dist/GeradorDados.jar <output path>
 * <keys> <files> <distribution> ~/hadoop/bin/hadoop fs -text
 * /user/raquel/dados/Folder/File1
 *
 */
package geradordados;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class GeradorDados extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new GeradorDados(), args);
        System.exit(exitCode);
    }

    protected void escreveDadosPareto(Writer out, Long n) throws IOException {
        for (int i = 0; i < n; i++) {
            out.append(new DoubleWritable(RandomGenerator.pareto(0.9)),
                    new Text(""));
        }
    }

    protected void escreveDadosNormal(Writer out, Long n) throws IOException {
        for (int i = 0; i < n; i++) {
            out.append(new DoubleWritable(RandomGenerator.gaussian(5.0, 1.0)),
                    new Text(""));
        }
    }

    protected void escreveDadosUniforme(Writer out, Long n) throws IOException {
        for (int i = 0; i < n; i++) {
            out.append(new DoubleWritable(RandomGenerator.uniform()), new Text(
                    ""));
        }
    }

    @Override
    public int run(String[] args) throws IOException {
        System.out.println();
        if (args.length != 4) {
            System.out.println();
            System.err
                    .println("Uso: <output path> <keys> <files> <distribution>");
            System.out.println();
            return -1;
        }

        JobConf conf = new JobConf();
        conf.setJobName("GeraDados");

        // formato do arquivo de saida
        conf.setOutputFormat(SequenceFileOutputFormat.class);

        // especifica os tipos de chave e valor dos pares intermediarios
        conf.setOutputKeyClass(DoubleWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(IdentityMapper.class);
        conf.setReducerClass(IdentityReducer.class);

        // especifica o caminho do arquivo de saida no HDFS
        FileOutputFormat.setOutputPath(conf, new Path(args[0]));

        Date tempo_inicio = new Date();

        String outputPath = args[0];
        Long keys = Long.parseLong(args[1]);
        Long files = Long.parseLong(args[2]);
        String distribution = args[3];

        FileSystem fs = FileSystem.get(conf);

        Path outputPathFS = new Path(outputPath);//, "Folder");

        // apaga arquivo de saida se ja existir no HDFS
        if (fs.exists(outputPathFS)) {
            System.out.println("Diretório existente sendo excluído");
            fs.delete(outputPathFS, true);
        }
        fs.mkdirs(outputPathFS);

        System.out.println("Criado diretório: " + outputPathFS.toString());

        Long n = keys / files;

        for (int i = 1; i <= files; i++) {
            // cria os arquivos
            String file = "File" + i;
            Path fileFS = new Path(outputPathFS, file);

            System.out.println("Criado arquivo: " + fileFS.toString());

            SequenceFile.Writer out = new SequenceFile.Writer(fs, conf, fileFS,
                    DoubleWritable.class, Text.class);

            if (distribution.equals("3")) {
                escreveDadosPareto(out, n);
            } else if (distribution.equals("2")) {
                escreveDadosNormal(out, n);
            } else {
                escreveDadosUniforme(out, n);
            }

            out.close();

            String cmdText = "bin/hadoop fs -text " + fileFS.toString();
            Runtime rt = Runtime.getRuntime();
            Process prcs = Runtime.getRuntime().exec(cmdText);
            InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            rt.runFinalization();

        }



        Date tempo_fim = new Date();
        long tempo_exec = tempo_fim.getTime() - tempo_inicio.getTime();
        System.err.println("-> Tempo total: " + tempo_exec + " milisegundos.");
        System.out.println();
        return 0;
    }
}
