#Variando a quantidade de 4 máquinas para 10⁸ dados

clear

if [ $# -ne 2 ]
then
    echo "Uso: ./file Potencia Cenarios"
    exit
fi

for i in {6..10}
do

bin/hadoop dfs -rmr /user/raquel


HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$i
CENARIOS=1
ARQUIVOS=$(echo "10^($POTENCIA-6)" | bc)
MAQUINAS=4
PARTICOES=$(echo "$MAQUINAS*2" | bc)
FREQ=0.9
FREQPASSO=0.1
AMOSTRA=10000
AMOSTRAPASSO=1
EXE=1

# 1. UNIFORME
DISTRIB=1
DIST="Uniforme" 
DIR="/user/raquel/OrdenacaoPorAmostragem/Dados/"$DIST$POTENCIA
CMD="bin/hadoop jar exe/GeraDados.jar $DIR $POTENCIA $CENARIOS $ARQUIVOS $DISTRIB"
echo -e "\n$CMD"
eval "$CMD"

wait

SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragem/Dados"$DIST
FILE1=$DIST$POTENCIA"_out1.txt"
FILE2=$DIST$POTENCIA"_out2.txt"
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragemUniforme.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"

wait


bin/hadoop dfs -rmr /user/raquel

# 2. NORMAL
DISTRIB=2
DIST="Normal" 
DIR="/user/raquel/OrdenacaoPorAmostragem/Dados/"$DIST$POTENCIA
CMD="bin/hadoop jar exe/GeraDados.jar $DIR $POTENCIA $CENARIOS $ARQUIVOS $DISTRIB"
echo -e "\n$CMD"
eval "$CMD"

wait

SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragem/Dados"$DIST
FILE1=$DIST$POTENCIA"_out1.txt"
FILE2=$DIST$POTENCIA"_out2.txt"
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"

wait


bin/hadoop dfs -rmr /user/raquel

# 3. PARETO
DISTRIB=3
DIST="Pareto" 
DIR="/user/raquel/OrdenacaoPorAmostragem/Dados/"$DIST$POTENCIA
CMD="bin/hadoop jar exe/GeraDados.jar $DIR $POTENCIA $CENARIOS $ARQUIVOS $DISTRIB"
echo -e "\n$CMD"
eval "$CMD"

wait

SAIDA="/home/marianehadoop/Dropbox/9Periodo/TCC/cluster/TestesOrdenacaoPorAmostragem/Dados"$DIST
FILE1=$DIST$POTENCIA"_out1.txt"
FILE2=$DIST$POTENCIA"_out2.txt"
CMD="bin/hadoop jar exe/OrdenacaoPorAmostragem.jar $DIR $PARTICOES $FREQ $FREQ $FREQPASSO $AMOSTRA $AMOSTRA $AMOSTRAPASSO $EXE $SAIDA $FILE1 $FILE2"
echo -e "\n$CMD"
eval "$CMD"

wait

done
