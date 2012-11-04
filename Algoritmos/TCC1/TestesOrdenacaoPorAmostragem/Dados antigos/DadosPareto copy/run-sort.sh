# Variando a quantidade de dados (4 máquinas)
clear

if [ $# -ne 1 ]
then
    echo "Uso: ./file Potencia"
    exit
fi

#bin/start-all.sh 

for i in {1..3}
do
HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$1
CENARIOS=$i
ARQUIVOS=$(echo "10^($POTENCIA-6)" | bc)
DISTRIB=3
DIST="Pareto" 
DIR="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados"$DIST$POTENCIA
PASTA="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados"$DIST$POTENCIA"/Folder"$CENARIOS

bin/hadoop dfs -rmr $PASTA

# 1. GERAR DADOS

# bin/hadoop jar GeraDados.jar <output dir> <num rows> <num-files> <1.uniform 2.normal 3.pareto>
CMD="bin/hadoop jar exe/GeraDados.jar $PASTA $POTENCIA $ARQUIVOS $DISTRIB"
echo -e "\n$CMD"
eval "$CMD"

wait

# Remover partes não desejadas _logs e _sucess
bin/hadoop dfs -rmr $PASTA"/_*"

done 

# 2. VER DADOS
CMD="bin/hadoop dfs -text $PASTA/part-00000 | head "
#echo -e "\n$CMD"
#eval "$CMD"

# 3. ORDENAR
PARTICOES=8
FREQ=0.9
FREQPASSO=0.1
AMOSTRA=10000
AMOSTRAPASSO=1
EXE=1
SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragemDouble/Dados"$DIST
FILE1=$DIST$POTENCIA"_out1.txt"
FILE2=$DIST$POTENCIA"_out2.txt"

#bin/hadoop jar OrdenacaoPorAmostragem.jar <dir-dfs-entrada><particoes><freq_min><freq_max><freq_passo><amostras_min><amostras_max><anostras_passo><execucoes><dir-resultados><arq-resultados>
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"


# 4. VER RESULTADOS
#bin/hadoop dfs -text /user/raquel/OrdenacaoPorAmostragem/QuantidadeDados/Uniforme6_Folder1-0.9-10000-1/part-00000 | head
CMD="bin/hadoop dfs -text "$PASTA"-"$FREQ-$AMOSTRA"-1/part-00000 | head "
#echo -e "\n$CMD"
#eval "$CMD"

#bin/stop-all.sh
