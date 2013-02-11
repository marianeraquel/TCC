#Variando a quantidade de dados máquinas para 5 máquinas
# run: ./run-dados.sh SDados 
# run : ./run-dados.sh QDadosUniforme
clear


E=$1
SAIDA1="${E:0:1}estat"
rm $SAIDA1

SAIDA2="${E:0:1}estatParticoes"
rm $SAIDA2


for i in {2..5}
do
ARQUIVO="$E$i"
awk '{print $1}' $ARQUIVO | sort -g
CMD="awk '{print \$1}' $ARQUIVO | sort -g | awk -f ../estatMOD.awk | awk '{print $i, \$0}' >> $SAIDA1"
echo -e "\n$CMD"
eval "$CMD"

awk '{$1=""; print $0}' $ARQUIVO | awk '{ for (i=1; i<=NF; i++)  if ($i != "\r") print $i }' | sort -g

CMD="awk '{\$1=\"\"; print \$0}' $ARQUIVO | awk '{ for (i=1; i<=NF; i++)  if (\$i != \"\\r\") print \$i }' | sort -g | awk -f ../estatMOD.awk | awk '{print $i, \$0}' >> $SAIDA2"
echo -e "\n$CMD"
eval "$CMD"


done
