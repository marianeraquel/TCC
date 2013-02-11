package sortvalidator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;



public class SortValidator extends Configured implements Tool {
	

	
	static final Text error = new Text("error");

	public int run(String[] args) throws Exception {
		
		if (args.length != 2) {
			printAndExit("Erro! Uso: SortValidator.jar <diretorio-entrada> <diretorio-saida>");
		}
		
		JobConf job = (JobConf) getConf();
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setJobName("SortValidator");
		job.setJarByClass(SortValidator.class);
		job.setMapperClass(SortValidatorMapper.class);
		job.setReducerClass(SortValidatorReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setInputFormat(SequenceFileInputFormat.class);
		job.setOutputFormat(SequenceFileOutputFormat.class);
		
		FileSystem fs = FileSystem.get(job);
		if (fs.exists(new Path(args[1]))) {
			fs.delete(new Path(args[1]), true);
		}
		
		// force a single reducer
		job.setNumReduceTasks(1);
		// force a single split
		job.setLong("mapred.min.split.size", Long.MAX_VALUE);
		JobClient.runJob(job);
		
		String retorno = ExecuteShell("/home/marianehadoop/hadoop/bin/hadoop dfs -text " + args[1] + "/part-00000");
		System.out.println(retorno);
		return 0;
	}
	
	public static String ExecuteShell(String s) throws Exception {
		Process procs = Runtime.getRuntime().exec(s);
		procs.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(procs.getInputStream()));
		String line;
		StringBuilder retorno = new StringBuilder();
		while ((line = br.readLine()) != null) {
			retorno.append(line);
			retorno.append('\n');
		}
		return retorno.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new JobConf(), new SortValidator(), args);
		System.exit(res);
	}

	public static void printAndExit(String s) {
		System.err.println(s);
		System.exit(2);
	}
}
