

bin/hadoop dfs -rmr /user/raquel

HADOOP="/home/marianehadoop/hadoop"
POTENCIA=6
CENARIOS=10
ARQUIVOS=$(echo "10^($POTENCIA-6)" | bc)
DISTRIB=3
DIST="Pareto" 
MAQUINAS=4
DIR="/user/raquel/OrdenacaoPorAmostragem/Conjuntos/"$DIST$POTENCIA


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
SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragem/Conjuntos"$DIST
FILE1=$DIST$MAQUINAS"_out1.txt"
FILE2=$DIST$MAQUINAS"_out2.txt"

#bin/hadoop jar OrdenacaoPorAmostragem.jar <dir-dfs-entrada><particoes><freq_min><freq_max><freq_passo><amostras_min><amostras_max><anostras_passo><execucoes><dir-resultados><arq-resultados>
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"


# 4. VER RESULTADOS
#bin/hadoop dfs -text /user/raquel/OrdenacaoPorAmostragem/Conjuntos/Uniforme6_Folder1-0.9-10000-1/part-00000 | head
CMD="bin/hadoop dfs -text "$DIR"/Folder1-"$FREQ-$AMOSTRA"-1/File1 | head"
#echo -e "\n$CMD"
#eval "$CMD"
