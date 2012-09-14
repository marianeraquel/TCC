/* Arquivo TeraGen modificado.
 * Uso: geraDados <diretorio> <linhas> <arquivos>
 * Gera inteiros aleatórios
 */
package geradados;

//<editor-fold defaultstate="collapsed" desc="Bibliotecas">
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
//</editor-fold>

public class GeraDados extends Configured implements Tool {

    //<editor-fold defaultstate="collapsed" desc="Splitters">
    /**
     * Formato de entrada que designa intervalos de entrada
     */
    static class RangeInputFormat
            implements InputFormat<LongWritable, NullWritable> {

        /**
         * Uma divisão da entrada que é um intervalo sobre os números.
         */
        static class RangeInputSplit implements InputSplit {

            long firstRow;
            long rowCount;

            public RangeInputSplit() {
            }

            public RangeInputSplit(long offset, long length) {
                firstRow = offset;
                rowCount = length;
            }

            public long getLength() throws IOException {
                return 0;
            }

            public String[] getLocations() throws IOException {
                return new String[]{};
            }

            public void readFields(DataInput in) throws IOException {
                firstRow = WritableUtils.readVLong(in);
                rowCount = WritableUtils.readVLong(in);
            }

            public void write(DataOutput out) throws IOException {
                WritableUtils.writeVLong(out, firstRow);
                WritableUtils.writeVLong(out, rowCount);
            }
        }

        /**
         * Um leitor de registros que vai gerar o intervalo de números.
         */
        static class RangeRecordReader
                implements RecordReader<LongWritable, NullWritable> {

            long startRow;
            long finishedRows;
            long totalRows;

            public RangeRecordReader(RangeInputSplit split) {
                startRow = split.firstRow;
                finishedRows = 0;
                totalRows = split.rowCount;
            }

            public void close() throws IOException {
                // NOTHING
            }

            public LongWritable createKey() {
                return new LongWritable();
            }

            public NullWritable createValue() {
                return NullWritable.get();
            }

            public long getPos() throws IOException {
                return finishedRows;
            }

            public float getProgress() throws IOException {
                return finishedRows / (float) totalRows;
            }

            public boolean next(LongWritable key,
                    NullWritable value) {
                if (finishedRows < totalRows) {
                    key.set(startRow + finishedRows);
                    finishedRows += 1;
                    return true;
                } else {
                    return false;
                }
            }
        }

        public RecordReader<LongWritable, NullWritable> getRecordReader(InputSplit split, JobConf job,
                Reporter reporter) throws IOException {
            return new RangeRecordReader((RangeInputSplit) split);
        }

        /**
         * Cria o número desejado de arquivos, dividindo o número de linhas
         * total entre esses arquivos / mappers.
         */
        public InputSplit[] getSplits(JobConf job,
                int numSplits) {
            numSplits = getNumberOfFiles(job);
            long totalRows = getNumberOfRows(job);
            long rowsPerSplit = totalRows / numSplits;
            System.out.println("Generating " + totalRows + " using " + numSplits
                    + " maps with step of " + rowsPerSplit);
            InputSplit[] splits = new InputSplit[numSplits];
            long currentRow = 0;
            for (int split = 0; split < numSplits - 1; ++split) {
                splits[split] = new RangeInputSplit(currentRow, rowsPerSplit);
                currentRow += rowsPerSplit;
            }
            splits[numSplits - 1] = new RangeInputSplit(currentRow,
                    totalRows - currentRow);
            return splits;


        }
    }
    // </editor-fold>

    static void setNumberOfFiles(JobConf job, int numRows) {
        job.setInt("terasort.num-files", numRows);
    }

    static int getNumberOfFiles(JobConf job) {
        return job.getInt("terasort.num-files", 0);
    }

    static long getNumberOfRows(JobConf job) {
        return job.getLong("terasort.num-rows", 0);
    }

    static void setNumberOfRows(JobConf job, long numRows) {
        job.setLong("terasort.num-rows", numRows);
    }

    /**
     * Mapper para gerar números Uniformes
     */
    public static class UniformMapper extends MapReduceBase
            implements Mapper<LongWritable, NullWritable, DoubleWritable, Text> {

        private DoubleWritable key = new DoubleWritable();
        private Text value = new Text();

        public void map(LongWritable row, NullWritable ignored,
                OutputCollector<DoubleWritable, Text> output,
                Reporter reporter) throws IOException {

            // Gera chave
            key.set(Math.abs(RandomGenerator.uniform()));
            // Não tem dados valor
            value.clear();
            output.collect(key, value);
        }
    }

    /**
     * Mapper para gerar números distribuicao Normal
     */
    public static class NormalMapper extends MapReduceBase
            implements Mapper<LongWritable, NullWritable, DoubleWritable, Text> {

        private DoubleWritable key = new DoubleWritable();
        private Text value = new Text();

        public void map(LongWritable row, NullWritable ignored,
                OutputCollector<DoubleWritable, Text> output,
                Reporter reporter) throws IOException {

            // Gera chave
            key.set(Math.abs(RandomGenerator.gaussian(5.0, 1.0)));
            // Não tem dados valor
            value.clear();
            output.collect(key, value);
        }
    }

    /**
     * Mapper para gerar números distribuicao Pareto
     */
    public static class ParetoMapper extends MapReduceBase
            implements Mapper<LongWritable, NullWritable, DoubleWritable, Text> {

        private DoubleWritable key = new DoubleWritable();
        private Text value = new Text();

        public void map(LongWritable row, NullWritable ignored,
                OutputCollector<DoubleWritable, Text> output,
                Reporter reporter) throws IOException {

            // Gera chave
            key.set(RandomGenerator.pareto(0.9));
            // Não tem dados valor
            value.clear();
            output.collect(key, value);
        }
    }

    /**
     * @param args the cli arguments
     */
    public int run(String[] args) throws IOException {
        JobConf job = (JobConf) getConf();

        if (args.length != 4) {
            System.err.println("geradados <output dir> <num rows> <num-files> <1.uniform 2.normal 3.exponencial>");
            return 2;
        }

        Path outputDir = new Path(args[0]);
        if (outputDir.getFileSystem(getConf()).exists(outputDir)) {
            throw new IOException("Output directory " + outputDir
                    + " already exists.");
        }
        // Número de linhas a ser gerado
        setNumberOfRows(job, (long) Math.pow(10, Long.parseLong(args[1])));

        // Número de arquivos
        setNumberOfFiles(job, Integer.parseInt(args[2]));

        // Tipo de distribuição
        if (args[3].equals("3")) {
            job.setMapperClass(ParetoMapper.class);
        } else if (args[3].equals("2")) {
            job.setMapperClass(NormalMapper.class);
        } else {
            job.setMapperClass(UniformMapper.class);
        }
        job.setOutputKeyClass(DoubleWritable.class);
        FileOutputFormat.setOutputPath(job, outputDir);
        job.setJobName("GeraDados");
        job.setJarByClass(GeraDados.class);
        job.setNumReduceTasks(0);

        job.setOutputValueClass(Text.class);
        job.setInputFormat(RangeInputFormat.class);
        job.setOutputFormat(SequenceFileOutputFormat.class);

        // seta o tipo de compressao e tratamento dos arquivos binarios
        SequenceFileOutputFormat.setCompressOutput(job, true);
        SequenceFileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);
        SequenceFileOutputFormat.setOutputCompressionType(job, CompressionType.BLOCK);

        // Gera os arquivos
        Date startTime = new Date();
        System.out.println("Job started: " + startTime);
        JobClient.runJob(job);
        Date endTime = new Date();
        System.out.println("Job ended: " + endTime);
        System.out.println("The job took "
                + (endTime.getTime() - startTime.getTime()) / 1000
                + " seconds.");
        return 0;
    }

    public static void main(String[] args) {
        int res = 0;
        try {
            res = ToolRunner.run(new JobConf(), new GeraDados(), args);
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
        }
        System.exit(res);
    }
}
