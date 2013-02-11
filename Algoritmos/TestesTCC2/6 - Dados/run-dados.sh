#Variando a quantidade de dados máquinas para 5 máquinas
# run: ./run-dados.sh SDados 
# run : ./run-dados.sh QDadosUniforme
clear


E=$1
SAIDA1="${E:0:1}estat"
rm $SAIDA1

SAIDA2="${E:0:1}estatParticoes"
rm $SAIDA2

SAIDA3=$SAIDA1"MegaDado"

for i in {6..10}
do
ARQUIVO="$E$i"
CMD="awk '{print \$1}' $ARQUIVO | sort -g | awk -f ../estatMOD.awk | awk '{print 10^$i, \$0}' >> $SAIDA1"
#echo -e "\n$CMD"
eval "$CMD"


CMD="awk '{\$1=\"\"; print \$0}' $ARQUIVO | awk '{ for (i=1; i<=NF; i++) print \$i }' | sort -g | awk -f ../estatMOD.awk | awk '{print 10^$i, \$0}' >> $SAIDA2"
#echo -e "\n$CMD"
eval "$CMD"

awk '{print $1/10^6, $4*10^6/$1}' $SAIDA1 > $SAIDA3


done
