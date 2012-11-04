import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.util.*;

public class TestMatrixMultiply {

	private static final String DATA_DIR_PATH = "/home/labores/Desktop/TestesExperimentos";
	private static final String INPUT_PATH_A = DATA_DIR_PATH + "/A";
	private static final String INPUT_OATH_B = DATA_DIR_PATH + "/B";
	private static final String OUTPUT_DIR_PATH = DATA_DIR_PATH + "/out";
	private static final String TEMP_DIR_PATH = DATA_DIR_PATH;

	private static final int NUM_RANDOM_SPARSE_TESTS = 10;
	private static final int NUM_RANDOM_DENSE_TESTS = 10;
	private static final int NUM_RANDOM_BIG_TESTS = 10;

        private static long inicio;
        private static long fim;
        private static long totalEmMilis;
        private static long tam;

	private static Configuration conf = new Configuration();
	private static FileSystem fs;

	private static int[][] A;
	private static int[][] B;

	private static Random random = new Random();

	public static void writeMatrix (int[][] matrix, int rowDim, int colDim, String pathStr)	throws IOException{

		Path path = new Path(pathStr);
		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, path,
			MatrixMultiply.IndexPair.class, IntWritable.class,
			SequenceFile.CompressionType.NONE);
		MatrixMultiply.IndexPair indexPair = new MatrixMultiply.IndexPair();
		IntWritable el = new IntWritable();
		for (int i = 0; i < rowDim; i++) {
			for (int j = 0; j < colDim; j++) {
				int v = matrix[i][j];
				if (v != 0) {
					indexPair.index1 = i;
					indexPair.index2 = j;
					el.set(v);
					writer.append(indexPair, el);
				}
			}
		}
		writer.close();
	}

	private static void fillMatrix (int[][] matrix, Path path) throws IOException{

		SequenceFile.Reader reader;
                reader = new SequenceFile.Reader(fs, path, conf);


		MatrixMultiply.IndexPair indexPair = new MatrixMultiply.IndexPair();
		IntWritable el = new IntWritable();
		while (reader.next(indexPair, el)) {
			matrix[indexPair.index1][indexPair.index2] = el.get();
		}
		reader.close();
	}

	public static int[][] readMatrix (int rowDim, int colDim, String pathStr) throws IOException{

		Path path = new Path(pathStr);

                Path test = new Path(OUTPUT_DIR_PATH + "/part-r-00000");

		int[][] result = new int[rowDim][colDim];
		for (int i = 0; i < rowDim; i++)
			for (int j = 0; j < colDim; j++)
				result[i][j] = 0;

                fillMatrix(result, test);

//		if (fs.isFile(path)) {
//			fillMatrix(result, path);
//		} else {
//			FileStatus[] fileStatusArray = fs.listStatus(path);
//
//			for (FileStatus fileStatus : fileStatusArray) {
//                                System.out.println(fileStatus.getPath());
//				fillMatrix(result, fileStatus.getPath());
//			}
//		}

		return result;
	}

	private static int[][] multiply (int[][] A, int[][] B, int I, int K, int J) {
		int[][] C = new int[I][J];
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				int sum = 0;
				for (int k = 0; k < K; k++) {
					sum += A[i][k] * B[k][j];
				}
				C[i][j] = sum;
			}
		}
		return C;
	}

	public static void checkAnswer (int[][] A, int[][] B, int I, int K, int J) throws Exception{

                boolean resp = true;
		int[][] X = multiply(A, B, I, K, J);
		int[][] Y = readMatrix(I, J, OUTPUT_DIR_PATH);
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				if (X[i][j] != Y[i][j]) {
					//throw new Exception("Bad answer!");
                                    resp = false;   //Resposta errada
				}
                                //System.out.print(Y[i][j]);
                                //System.out.print(" ");
			}

                        //System.out.println();
		}

                if(resp)  System.out.println("Boa garoto!");

                else System.out.println("Po! Resposta errada!");
	}

	private static void zero (int[][] matrix, int rowDim, int colDim) {
		for (int i = 0; i < rowDim; i++)
			for (int j= 0; j < colDim; j++)
				matrix[i][j] = 0;
	}

	private static void fillRandom (int[][] matrix, int rowDim, int colDim, boolean sparse) {
		if (sparse) {
			zero(matrix, rowDim, colDim);
			for (int n = 0; n < random.nextInt(10); n++)
				matrix[random.nextInt(rowDim)][random.nextInt(colDim)] = random.nextInt(1000);
		} else {
			for (int i = 0; i < rowDim; i++) {
				for (int j = 0; j < colDim; j++) {
					matrix[i][j] = random.nextInt(1000);
				}
			}
		}
	}

        private static void fillRandSmall (int[][] matrix, int rowDim, int colDim, boolean sparse) {
		if (sparse) {
			zero(matrix, rowDim, colDim);
			for (int n = 0; n < random.nextInt(10); n++)
				matrix[random.nextInt(rowDim)][random.nextInt(colDim)] = random.nextInt(10);
		} else {
			for (int i = 0; i < rowDim; i++) {
				for (int j = 0; j < colDim; j++) {
					matrix[i][j] = random.nextInt(10);
				}
			}
		}
	}

	public static void runOneTest (int strategy, int R1, int R2, int I, int K, int J, int IB, int KB, int JB) throws Exception{

                inicio = System.currentTimeMillis();
		MatrixMultiply.runJob(conf, INPUT_PATH_A, INPUT_OATH_B, OUTPUT_DIR_PATH, TEMP_DIR_PATH,
			strategy, R1, R2, I, K, J, IB, KB, JB);
                fim = System.currentTimeMillis();

		//checkAnswer(A, B, I, K, J);
	}

	private static void testIdentity () throws Exception	{

		A = new int[][] { {1,0}, {0,1}};
		B = new int[][] { {1,0}, {0,1}};
		writeMatrix(A, 2, 2, INPUT_PATH_A);
		writeMatrix(B, 2, 2, INPUT_OATH_B);
		System.out.println();
		System.out.println("Identity test");
		runOneTest(1, 1, 1, 2, 2, 2, 2, 2, 2);
	}

	private static void testTwoByTwo () throws Exception	{

		A = new int[][] { {1,2}, {3,4}};
		B = new int[][] { {5,6}, {7,8}};
		writeMatrix(A, 2, 2, INPUT_PATH_A);
		writeMatrix(B, 2, 2, INPUT_OATH_B);
		for (int strategy = 1; strategy <= 4; strategy++) {
			for (int IB = 1; IB <= 2; IB++) {
				for (int KB = 1; KB <= 2; KB++) {
					for (int JB = 1; JB <= 2; JB++) {
						System.out.println();
						System.out.println("Two by two test");
						System.out.println("   strategy = " + strategy);
						System.out.println("   IB = " + IB);
						System.out.println("   KB = " + KB);
						System.out.println("   JB = " + JB);
						runOneTest(strategy, 1, 1, 2, 2, 2, IB, KB, JB);
					}
				}
			}
		}
	}

	private static void testThreeByThree () throws Exception {

		A = new int[][] { {1,2,3}, {4,5,6}, {7,8,9}};
		B = new int[][] { {10,11,12}, {13,14,15}, {16,17,18}};
		writeMatrix(A, 3, 3, INPUT_PATH_A);
		writeMatrix(B, 3, 3, INPUT_OATH_B);
		for (int strategy = 1; strategy <= 4; strategy++) {
			for (int IB = 1; IB <= 3; IB++) {
				for (int KB = 1; KB <= 3; KB++) {
					for (int JB = 1; JB <= 3; JB++) {
						System.out.println();
						System.out.println("Three by three test");
						System.out.println("   strategy = " + strategy);
						System.out.println("   IB = " + IB);
						System.out.println("   KB = " + KB);
						System.out.println("   JB = " + JB);
						runOneTest(strategy, 1, 1, 3, 3, 3, IB, KB, JB);
					}
				}
			}
		}
	}

	private static void testVerySparse () throws Exception {

		A = new int[10][7];
		B = new int[7][12];
		zero(A, 10, 7);
		zero(B, 7, 12);
		A[5][6] = 1;
		B[6][7] = 1;
		writeMatrix(A, 10, 7, INPUT_PATH_A);
		writeMatrix(B, 7, 12, INPUT_OATH_B);
		System.out.println();
		System.out.println("Very sparse test");
		runOneTest(1, 1, 1, 10, 7, 12, 3, 3, 3);
	}

	private static void testRandom (boolean sparse, boolean big) throws Exception {

		int strategy = random.nextInt(4) + 1;
		int dimMin = big ? 100 : 10;
		int dimRandom = big ? 100 : 10;
		int I = random.nextInt(dimRandom) + dimMin;
		int K = random.nextInt(dimRandom) + dimMin;
		int J = random.nextInt(dimRandom) + dimMin;
		int IB = random.nextInt(I) + 1;
		int KB = random.nextInt(K) + 1;
		int JB = random.nextInt(J) + 1;
		A = new int[I][K];
		B = new int[K][J];

                //fillRandSmall(A, I, K, sparse);
                //fillRandSmall(B, K, J, sparse);

		fillRandom(A, I, K, sparse);
		fillRandom(B, K, J, sparse);

		writeMatrix(A, I, K, INPUT_PATH_A);
		writeMatrix(B, K, J, INPUT_OATH_B);

                System.out.println("Teste");
		System.out.println("   strategy = " + strategy);
		System.out.println("   I = " + I);
		System.out.println("   K = " + K);
		System.out.println("   J = " + J);
		System.out.println("   IB = " + IB);
		System.out.println("   KB = " + KB);
		System.out.println("   JB = " + JB);

                System.out.println("Matriz A " + I + " x " + K) ;
                System.out.println("Matriz B " + K + " x " + J) ;
                System.out.println("\nMatriz Resposta " + I + " x " + J) ;
		runOneTest(strategy, 1, 1, I, K, J, IB, KB, JB);
	}

        private static void meuTeste (int estrategia) throws Exception {

		A = new int[][] { {1,2,3,4,6,9,1,3,4,5},
                                  {1,7,2,9,0,3,4,7,8,3},
                                  {2,3,1,6,5,8,8,3,7,3},
                                  {3,0,9,3,1,4,5,2,1,9},
                                  {3,9,1,7,8,9,7,3,1,2},
                                  {2,8,3,5,4,1,8,3,7,3},
                                  {9,0,9,2,6,1,7,3,1,2},
                                  {4,5,3,4,9,1,6,2,1,9},
                                  {3,4,6,7,0,1,2,7,8,3},
                                  {1,2,5,1,9,0,3,3,4,5}  };

		B = new int[][] { {2,3,9,0,1,2,8,3,7,3},
                                  {9,4,1,8,2,3,7,3,1,2},
                                  {2,9,0,2,3,4,5,2,1,9},
                                  {1,2,7,6,3,3,4,7,8,3},
                                  {1,2,3,4,5,8,1,2,3,9},
                                  {1,2,9,0,4,3,2,3,4,5},
                                  {3,1,3,4,4,2,4,7,8,3},
                                  {2,6,0,7,2,1,8,3,7,3},
                                  {4,2,4,8,9,1,5,2,1,9},
                                  {4,0,9,2,3,9,7,3,1,2}  };
		writeMatrix(A, 10, 10, INPUT_PATH_A);
		writeMatrix(B, 10, 10, INPUT_OATH_B);

                runOneTest(estrategia, 1, 1, 10, 10, 10, 5, 5, 5);
	}

        public static void meuTest(int estrategia, int dimensao)throws Exception{
                int strategy = estrategia; //random.nextInt(4) + 1;
                int tam = dimensao;
		int I = tam, K = tam, J = tam;
		A = new int[I][K];
		B = new int[K][J];

                fillRandSmall(A, I, K, false);
                fillRandSmall(B, K, J, false);

		//fillRandom(A, I, K, false);
		//fillRandom(B, K, J, false);

		writeMatrix(A, I, K, INPUT_PATH_A);
		writeMatrix(B, K, J, INPUT_OATH_B);

                System.out.println("Teste");
		System.out.println("   strategy = " + strategy);

                System.out.println("Matriz A " + I + " x " + K) ;
                System.out.println("Matriz B " + K + " x " + J) ;
                System.out.println("Matriz Resposta " + I + " x " + J + "\n") ;
		runOneTest(strategy, 1, 1, I, K, J, I, I, I);
        }

        public static void teste2() throws Exception {
                A = new int[][] { {1,2}, {3,4}};
		B = new int[][] { {5,6}, {7,8}};



		writeMatrix(A, 2, 2, INPUT_PATH_A);
		writeMatrix(B, 2, 2, INPUT_OATH_B);
                runOneTest(1, 1, 1, 2, 2, 2, 2, 2, 2);
        }

        public static void testeGrafico(int tamanho, int estrategia) throws Exception{
                int tam = tamanho;
                int estrategy = estrategia;
                A = new int[tamanho][tamanho];
		B = new int[tamanho][tamanho];

                fillRandom(A, tam, tam, false);
		fillRandom(B, tam, tam, false);

                writeMatrix(A, tam, tam, INPUT_PATH_A);
		writeMatrix(B, tam, tam, INPUT_OATH_B);

                runOneTest(estrategy, 1, 1, tam, tam, tam, tam, tam, tam);
        }

        public static void testeMarcus(int estrategia) throws Exception{
                int estrategy = estrategia;
                long tempo[] = {0,0,0,0,0,0,0,0};



                for(int tamanho = 1000, i = 0; tamanho <= 8000; tamanho = tamanho + 1000, i++){
                A = new int[tamanho][tamanho];
		B = new int[tamanho][tamanho];

                fillRandom(A, tamanho, tamanho, false);
		fillRandom(B, tamanho, tamanho, false);

                writeMatrix(A, tamanho, tamanho, INPUT_PATH_A);
		writeMatrix(B, tamanho, tamanho, INPUT_OATH_B);

                inicio = 0;
                fim = 0;

                runOneTest(estrategy, 1, 1, tamanho, tamanho, tamanho, tamanho, tamanho, tamanho);

                tempo[i] = fim - inicio;
                }

                System.out.println("Estrategia: " + estrategy + "\n");
                for(int j = 0; j < tempo.length; j++){
                    System.out.println("Tamanho " + (j+1)*1000 + "   Tempo: " + tempo[j]);
                }


        }

	public static void main (String[] args)	throws Exception{

		new GenericOptionsParser(conf, args);
		fs = FileSystem.get(conf);
		fs.mkdirs(new Path(DATA_DIR_PATH));
                int taman = 3000;
                int est = 3;

		try {
                        testeGrafico(taman, est);
                        totalEmMilis = fim - inicio;
                        System.out.println("Tamanho da Matriz: " + taman + " Tempo em milisegundos: " + totalEmMilis);

                        //meuTeste(1);
                        //teste2();                        
                        //meuTest(2, 10);
                        //testIdentity ();
                        //testVerySparse ();
                        //testTwoByTwo ();
                        //testThreeByThree ();

                    //for(int i=0; i < 10; i++)
			//testRandom(false, false);
		} finally {
			fs.delete(new Path(DATA_DIR_PATH), true);
		}
	}
}