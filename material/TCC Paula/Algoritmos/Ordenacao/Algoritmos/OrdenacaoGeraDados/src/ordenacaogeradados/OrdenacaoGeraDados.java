/**
 * @file    OrdenacaoGeraDados.java
 * @brief   Gera valores inteiros para ordenacao.
 * @author  Paula Pinhao
 * @year    2011
 * @version 1.0
*/

package ordenacaogeradados;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class OrdenacaoGeraDados
{
     // <editor-fold defaultstate="collapsed" desc="Gera dados">
    /** Gera um arquivo com os dados inteiros a serem ordenados
     * @param path diretorio onde o arquivo deve ser gerado
     * @param num_dados numero de dados a serem gerados
     * @param valor_max valor maximo dos valores gerados
     * @throws IOException
     */
    protected void geraDados(String path, int num_dados, int valor_max) throws IOException
    {
        File dir = new File(path);
        dir.mkdir();

        File arq = new File(dir, "1000inteiros.txt");

        FileOutputStream saida = new FileOutputStream(arq);
        String valores_inteiros;

        for(int i = 0; i < num_dados; i++)
        {
            valores_inteiros = (int)((Math.random() * valor_max) + 1) + " ";

            saida.write(valores_inteiros.getBytes());

            System.out.print(valores_inteiros);
	}

        System.out.println("");
        saida.close();
    }
    // </editor-fold>

     // <editor-fold defaultstate="collapsed" desc="Funcao Principal">
    /** Funcao principal
     * @param args[0] nome do arquivo de saida
     * @param args[1] numero de dados a serem gerados
     * @param args[2] valor maximo do conjunto a ser gerado
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        if (args.length != 3)
        {
            System.err.printf("Uso: <entrada> <num_dados> <valor_maximo> \n");
        } else
        {
            OrdenacaoGeraDados gera_dados = new OrdenacaoGeraDados();
            gera_dados.geraDados(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }
    }// </editor-fold>
}