/****
 ** ~/hadoop/bin/hadoop jar GeradorDados.jar <output path>
 ** <keys> <files> <distribution> 
 ** ~/hadoop/bin/hadoop fs -text /user/raquel/dados/Folder/File1
 **
 ****/

package geradordados;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
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
			out.append(new DoubleWritable(RandomGenerator.pareto(0.9)), new Text());
		}
	}

	protected void escreveDadosNormal(Writer out, Long n) throws IOException {
		for (int i = 0; i < n; i++) {
			out.append(new DoubleWritable(RandomGenerator.gaussian(5.0, 1.0)), new Text());
		}
	}

	protected void escreveDadosUniforme(Writer out, Long n) throws IOException {
		for (int i = 0; i < n; i++) {
			out.append(new DoubleWritable(RandomGenerator.uniform()), new Text());
		}
	}

	protected void escreveDadoUnico(Writer out, Long n) throws IOException {
		DoubleWritable a = new DoubleWritable(RandomGenerator.uniform());
		for (int i = 0; i < n; i++) {
			out.append(a, new Text());
		}
	}

	protected void escreveDadoDupla(Writer out, Long n) throws IOException {
		DoubleWritable a = new DoubleWritable(RandomGenerator.uniform());
		DoubleWritable b = new DoubleWritable(RandomGenerator.uniform());

		for (int i = 0; i < n / 2; i++) {
			out.append(a, new Text());
			out.append(b, new Text());
		}
	}

	@Override
	public int run(String[] args) throws IOException {
		System.out.println();
		if (args.length != 4) {
			System.out.println();
			System.err.println("Uso: <output path> <keys> <files> <distribution>");
			System.err.println("1. Uniforme 2. Normal 3. Pareto 4. Unico valor 5. Dois valor");
			System.out.println();
			return -1;
		}

		JobConf conf = new JobConf();
		conf.setJobName("GeraDados");

		// formato do arquivo de saida
		conf.setOutputFormat(SequenceFileOutputFormat.class);
                // seta o tipo de compressao e tratamento dos arquivos binarios
                SequenceFileOutputFormat.setCompressOutput(conf, true);
                SequenceFileOutputFormat.setOutputCompressorClass(conf, GzipCodec.class);
                SequenceFileOutputFormat.setOutputCompressionType(conf, CompressionType.BLOCK);
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

		Path outputPathFS = new Path(outputPath);// , "Folder");

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

			SequenceFile.Writer out = new SequenceFile.Writer(fs, conf, fileFS, DoubleWritable.class,
					Text.class);

			if (distribution.equals("5")) {
				escreveDadoDupla(out, n);
			} else if (distribution.equals("4")) {
				escreveDadoUnico(out, n);
			} else if (distribution.equals("3")) {
				escreveDadosPareto(out, n);
			} else if (distribution.equals("2")) {
				escreveDadosNormal(out, n);
			} else {
				escreveDadosUniforme(out, n);
			}

			out.close();

			/*String cmdText = "/home/raquel/hadoop/bin/hadoop fs -text " + fileFS.toString();
			Runtime rt = Runtime.getRuntime();
			Process prcs = Runtime.getRuntime().exec(cmdText);
			InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			rt.runFinalization();*/

		}

		Date tempo_fim = new Date();
		double tempo_exec = (tempo_fim.getTime() - tempo_inicio.getTime())/1000.0;
		System.err.println("-> Tempo total: " + tempo_exec + " segundos.");
		System.out.println();
		return 0;
	}
}
