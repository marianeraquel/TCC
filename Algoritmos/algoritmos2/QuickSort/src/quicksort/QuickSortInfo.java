package quicksort;

/** Representa uma partição **/
public class QuickSortInfo {

	// Número esperado de partições
	int numberPartitions;
	// Chaves
	int numberKeys;
	// Partição
	int depth;
	// Arquivos entrada e saída
	String inputPath, outputPath;

	public QuickSortInfo() {
	}


	public QuickSortInfo(int numberPartitions, int numberKeys, int depth, String inputPath, String outputPath) {
		super();
		this.numberPartitions = numberPartitions;
		this.numberKeys = numberKeys;
		this.depth = depth;
		this.inputPath = inputPath;
		this.outputPath = outputPath;
	}


	public void print() {
		System.out.println();
		System.out.println("PARTIÇÃO: " + depth);
		System.out.println("Número de partições desejadas: " + numberPartitions);
		System.out.println("Número de chaves: " + numberKeys);
		System.out.println("Diretório Entrada: " + inputPath);
		System.out.println();
	}
}
