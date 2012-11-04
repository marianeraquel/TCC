package wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {

  // Classe de mapeamento : herda de Mapper
  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();


    /*
          map(String chave, String valor):
              Tokens tokens = Tokenize(valor);
              para cada token t em tokens:
              CriaIntermediario(t, "1");
    */
    // O processamento do Mapper eh feito no metodo map
    // que deve ser sobrecarregado
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      // gera tokens da linha do documento, onde cada token corresponde a uma palavra
      StringTokenizer itr = new StringTokenizer(value.toString());

      // faz o mapeamento (token, '1') para todos os tokens
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());

        // associa cada token ao valor “1”, criando os pares intermediarios <token, “1”>
        // os pares intermediarios sao repassados ao framework, que agrupa todos os
        // valores do mesmo token, criando os pares <token, list (“1”, “1”, “1”, ...)>
        // ordenados pela chave
        context.write(word, one);
      }
    }
  }


  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    /*
        reduce(String chave, Iterador valores):
            int resultado = 0;
            para cada valor v em valores:
            resultado += (int)v;
            retorne(chave, resultado);
    */
    // O processamento do Reducer eh feito no metodo reduce
    // O metodo run passa para a funcao um token (key) e a lista
    // de valores unitarios
    // A funcao reduce simplesmente soma os valores unitarios de cada
    // palavra e cria o par <palavra , total_de_ocorrencias >
    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {

      int sum = 0;
      // Soma os valores unitarios da palavra
      for (IntWritable val : values) {
        sum += val.get();
      }

      result.set(sum);
      // cria o par
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    // Carrega os arquivos de configuracao da pasta conf
    Configuration conf = new Configuration();

    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length != 2)
    {
      System.err.println("Usage: wordcount <in> <out>");
      System.exit(2);
    }

    // Job controla os parametros de execucao da aplicacao paralela
    Job job = new Job(conf, "word count");

    // especifica a classe do programa principal
    job.setJarByClass(WordCount.class);

    // especifica as classes de mapeamento e de reducao
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);

    // especificaca os tipos de dados da chave e do valor dos
    // pares finais resultantes (reducer)
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    // especifica os caminhos dos arquivos de entrada e saida no HDFS
    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    // submete o job a execucao
    // wait: bloqueia o programa ate que a execucao termine
    // alternativa: utilizacao do metodo submit para submeter o
    // job a execucao sem causar o bloqueio do programa
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
