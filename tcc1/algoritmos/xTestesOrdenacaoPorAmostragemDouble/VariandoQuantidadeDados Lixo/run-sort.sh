# Variando a quantidade de dados (4 máquinas)
clear

if [ $# -ne 3 ]
then
    echo "Uso: ./file Potencia Cenarios"
    exit
fi

#bin/start-all.sh 
#bin/hadoop dfs -rmr /user/raquel/OrdenacaoPorAmostragem/

for i in {6..9}
do
HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$i #$1
CENARIOS=1 #$2
ARQUIVOS=$(echo "10^($POTENCIA-5)" | bc)
PASTA="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados"$POTENCIA
#bin/hadoop dfs -rmr $PASTA

# 1. GERAR DADOS

# bin/hadoop jar GeraDados.jar <path> <potencia> <cenarios> <arquivos>
CMD="bin/hadoop jar exe/GeraDados.jar $PASTA $POTENCIA $CENARIOS $ARQUIVOS"
echo ""
echo "$CMD"
eval "$CMD"

wait

# 2. VER DADOS
#CMD="bin/hadoop dfs -text $PASTA/Uniforme"$POTENCIA"_Folder1/Uniforme"$POTENCIA"_Folder1-File1 | head "
#echo ""
#echo "$CMD"
#eval "$CMD"

# 3. ORDENAR
PARTICOES=8
FREQ=0.9
FREQPASSO=0.1
AMOSTRA=10000
AMOSTRAPASSO=1
EXE=1
SAIDA="/home/marianehadoop/houtput"
FILE1="Uniforme"$POTENCIA"_out1.txt"
FILE2="Uniforme"$POTENCIA"_out2.txt"

#bin/hadoop jar OrdenacaoPorAmostragem.jar <dir-dfs-entrada><particoes><freq_min><freq_max><freq_passo><amostras_min><amostras_max><anostras_passo><execucoes><dir-resultados><arq-resultados>
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $PASTA $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo ""
echo "$CMD"
eval "$CMD"


# 4. VER RESULTADOS
#bin/hadoop dfs -text /user/raquel/OrdenacaoPorAmostragem/QuantidadeDados/Uniforme6_Folder1-0.9-10000-1/part-00000 | head
#CMD="bin/hadoop dfs -text "$PASTA"/Uniforme"$POTENCIA"_Folder1-"$FREQ-$AMOSTRA"-1/part-00000 | head "
#echo ""
#echo "$CMD"
#eval "$CMD"

done

#bin/stop-all.sh 

