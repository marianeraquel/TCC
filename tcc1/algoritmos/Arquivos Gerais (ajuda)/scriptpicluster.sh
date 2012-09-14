#!/bin/bash
# Mon Jul  4 16:14:27 BRT 2011

date >> RESULTADOS.txt


# Incremento
MAPS=100
INC=10
MAX=10000000000

# Ordem Atual
ORDEM=1

until [ $ORDEM -gt $MAX ]
do

echo -n 'Numero de Maps: '$MAPS'  Numero de Samples: '$ORDEM' '>> RESULTADOS.txt

for i in {1..5}
do
#CMDX="bin/hadoop jar MatrixMult.jar $ORDEM $EST $PROC | tail -n1 | sed 's/.*: \([0-9]*\).*/\1/'"
CMDX="bin/hadoop jar hadoop-0.20.2-examples.jar pi $MAPS $ORDEM | tail -n2 | sed 's/.*in \([0-9]*.[0-9]*\) s.*/Tempo em segundos: \1/' | sed 's/.*is \([0-9]*\)*/Valor do PI: \1/'" 
echo "Executando $CMDX"
A=`eval "$CMDX"`
echo -e "\n$A" >> RESULTADOS.txt

done
echo '********************FIM***************************' >> RESULTADOS.txt

ORDEM=$((ORDEM*INC))
done

date >> RESULTADOS.txt


#-----------------------------------------------------------------------------------------
# Incremento
MAPS=10
INC=10
MAX=10000000000

# Ordem Atual
ORDEM=10000000000

until [ $ORDEM -gt $MAX ]
do

echo -n 'Numero de Maps: '$MAPS'  Numero de Samples: '$ORDEM' '>> RESULTADOS.txt

for i in {1..5}
do
#CMDX="bin/hadoop jar MatrixMult.jar $ORDEM $EST $PROC | tail -n1 | sed 's/.*: \([0-9]*\).*/\1/'"
CMDX="bin/hadoop jar hadoop-0.20.2-examples.jar pi $MAPS $ORDEM | tail -n2 | sed 's/.*in \([0-9]*.[0-9]*\) s.*/Tempo em segundos: \1/' | sed 's/.*is \([0-9]*\)*/Valor do PI: \1/'" 
echo "Executando $CMDX"
A=`eval "$CMDX"`
echo -e "\n$A" >> RESULTADOS.txt

done
echo '********************FIM***************************' >> RESULTADOS.txt

ORDEM=$((ORDEM*INC))
done


date >> RESULTADOS.txt
#-----------------------------------------------------------------------------------------

# Incremento
MAPS=100

# Ordem Atual
ORDEM=100000000000

echo -n 'Numero de Maps: '$MAPS'  Numero de Samples: '$ORDEM' '>> RESULTADOS.txt


#CMDX="bin/hadoop jar MatrixMult.jar $ORDEM $EST $PROC | tail -n1 | sed 's/.*: \([0-9]*\).*/\1/'"
CMDX="bin/hadoop jar hadoop-0.20.2-examples.jar pi $MAPS $ORDEM | tail -n2 | sed 's/.*in \([0-9]*.[0-9]*\) s.*/Tempo em segundos: \1/' | sed 's/.*is \([0-9]*\)*/Valor do PI: \1/'" 
echo "Executando $CMDX"
A=`eval "$CMDX"`
echo -e "\n$A" >> RESULTADOS.txt

echo '********************FIM***************************' >> RESULTADOS.txt

date >> RESULTADOS.txt

#-----------------------------------------------------------------------------------------
# Incremento
MAPS=10

# Ordem Atual
ORDEM=100000000000

echo -n 'Numero de Maps: '$MAPS'  Numero de Samples: '$ORDEM' '>> RESULTADOS.txt


#CMDX="bin/hadoop jar MatrixMult.jar $ORDEM $EST $PROC | tail -n1 | sed 's/.*: \([0-9]*\).*/\1/'"
CMDX="bin/hadoop jar hadoop-0.20.2-examples.jar pi $MAPS $ORDEM | tail -n2 | sed 's/.*in \([0-9]*.[0-9]*\) s.*/Tempo em segundos: \1/' | sed 's/.*is \([0-9]*\)*/Valor do PI: \1/'" 
echo "Executando $CMDX"
A=`eval "$CMDX"`
echo -e "\n$A" >> RESULTADOS.txt

echo '********************FIM***************************' >> RESULTADOS.txt

date >> RESULTADOS.txt

