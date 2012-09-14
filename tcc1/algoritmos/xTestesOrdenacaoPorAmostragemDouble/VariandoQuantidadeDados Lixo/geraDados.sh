if [ $# -ne 3 ]
then
    echo "Uso: ./file Potencia Cenarios"
    exit
fi

#bin/start-all.sh 

HADOOP="/home/marianehadoop/hadoop"
POTENCIA=$1
CENARIOS=$2
ARQUIVOS=$3 #(echo "10^($POTENCIA-6)" | bc)
PASTA="/user/raquel/OrdenacaoPorAmostragem/QuantidadeDados"$POTENCIA
bin/hadoop dfs -rmr $PASTA

# 1. GERAR DADOS
# bin/hadoop jar GeraDados.jar <path> <potencia> <cenarios> <arquivos>
CMD="bin/hadoop jar exe/GeraDados.jar $PASTA $POTENCIA $CENARIOS $ARQUIVOS"
echo ""
echo "$CMD"
eval "$CMD"

# 2. VER DADOS
#CMD="bin/hadoop dfs -text $PASTA/Uniforme"$POTENCIA"_Folder1/Uniforme"$POTENCIA"_Folder1-File1 | head "
#echo ""
#echo "$CMD"
#eval "$CMD"

#bin/stop-all.sh 
