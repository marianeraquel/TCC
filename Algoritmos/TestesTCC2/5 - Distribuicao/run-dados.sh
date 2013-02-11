#Variando a distribuição de 10^8 dados para 5 máquinas
# run: ./run-dados.sh Q
# run : ./run-dados.sh S
clear


E=$1
SAIDA1="${E:0:1}estat"
echo "# TAMANHO Menor  Maior    Média     Mediana     Variancia     Desvio Padrão     COV" >> $SAIDA1
rm $SAIDA1

for i in "Uniforme" "Normal" "Pareto"
do
ARQUIVO="$E$i"
N=8
CMD="awk '{print \$1}' $ARQUIVO$N | sort -g | awk -f ../estatMOD.awk | awk '{print \"$i\", \$0}' >> $SAIDA1"
echo -e "\n$CMD"
eval "$CMD"

done
