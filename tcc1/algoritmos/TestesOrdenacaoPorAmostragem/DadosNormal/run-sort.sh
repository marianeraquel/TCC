# Variando a quantidade de dados (4 máquinas)
clear

#bin/start-all.sh 

for i in {6..10}
do

HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$i
CENARIOS=3
ARQUIVOS=$(echo "10^($POTENCIA-6)" | bc)
DISTRIB=2
DIST="Normal" 
DIR="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados/"$DIST$POTENCIA

bin/hadoop dfs -rmr $DIR

# 1. GERAR DADOS

# bin/hadoop jar GeraDados.jar <output dir> <num rows> <cenarios> <num-files> <1.uniform 2.normal 3.pareto>
CMD="bin/hadoop jar exe/GeraDados.jar $DIR $POTENCIA $CENARIOS $ARQUIVOS $DISTRIB"
echo -e "\n$CMD"
eval "$CMD"

wait

# 2. VER DADOS
CMD="bin/hadoop dfs -text $DIR/Folder1/File1 | head "
#echo -e "\n$CMD"
#eval "$CMD"

# 3. ORDENAR
PARTICOES=8
FREQ=0.9
FREQPASSO=0.1
AMOSTRA=10000
AMOSTRAPASSO=1
EXE=1
SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragem/Dados"$DIST
FILE1=$DIST$POTENCIA"_out1.txt"
FILE2=$DIST$POTENCIA"_out2.txt"

#bin/hadoop jar OrdenacaoPorAmostragem.jar <dir-dfs-entrada><particoes><freq_min><freq_max><freq_passo><amostras_min><amostras_max><anostras_passo><execucoes><dir-resultados><arq-resultados>
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"


# 4. VER RESULTADOS
#bin/hadoop dfs -text /user/raquel/OrdenacaoPorAmostragem/QuantidadeDados/Uniforme6_Folder1-0.9-10000-1/part-00000 | head
CMD="bin/hadoop dfs -text "$DIR"/Folder1-"$FREQ-$AMOSTRA"-1/File1 | head"
#echo -e "\n$CMD"
#eval "$CMD"

done

#bin/stop-all.sh 

