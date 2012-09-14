clear

if [ $# -ne 1 ]
then
    echo "Uso: ./file Potencia"
    exit
fi

#bin/start-all.sh 

HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$1
PASTA="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados"$POTENCIA

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

#bin/stop-all.sh 

