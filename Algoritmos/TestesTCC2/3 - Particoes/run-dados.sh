#Variando a quantidade de partições para 5 máquinas e 10^8 dados
# run: ./run-dados.sh ENTRADA 

clear

for i in  2 4 6 8
do

ARQUIVO="$1$i"
SAIDA="${ARQUIVO:0:1}estat"

#echo "# TAMANHO Menor  Maior    Média     Mediana     Variancia     Desvio Padrão     COV"  >> $SAIDA
CMD="awk '{print \$1}' $ARQUIVO | sort -g | awk -f ../estatMOD.awk | awk '{print $i, \$0}' >> $SAIDA"
echo -e "\n$CMD"
eval "$CMD"

SAIDA="${ARQUIVO:0:1}estatParticoes"
#echo "#TAMANHO Menor  Maior    Média     Mediana     Variancia     Desvio Padrão     COV"  >> $SAIDA
CMD="awk '{\$1=\"\"; print \$0}' $ARQUIVO | awk '{ for (i=1; i<=NF; i++) if (\$i != \"\\r\") print \$i }' | sort -g | awk -f ../estatMOD.awk | awk '{print $i, \$0}' >> $SAIDA"
#echo -e "\n$CMD"
eval "$CMD"


done
