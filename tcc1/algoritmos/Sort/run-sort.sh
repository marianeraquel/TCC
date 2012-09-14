HADOOP="/home/marianehadoop/hadoop"

# 1) eliminar se já existirem pastas

CMD="$HADOOP/bin/hadoop  dfs -rmr /user/raquel"
echo "$CMD"
eval "$CMD"

# 2) RandomWriter 
#bin/hadoop jar hadoop-examples.jar randomwriter <in-dir> 

CMD="$HADOOP/bin/hadoop jar $HADOOP/hadoop-*-examples.jar randomwriter $HADOOP/user/raquel/sort-in"
echo "Executando $CMD"
eval "$CMD"

# verificar arquivos gerados
#bin/hadoop fs -text user/raquel/sort-in/part-00000 | tail

# 3) Sort

#bin/hadoop jar hadoop-examples-*.jar sort [-m <#maps>] [-r <#reduces>] <in-dir> <out-dir> 
CMD="$HADOOP/bin/hadoop jar $HADOOP/hadoop-*-examples.jar sort $HADOOP/user/raquel/sort-in $HADOOP/user/raquel/sort-out "
echo "$CMD"
eval "$CMD"

#verificar arquivos ordenados
#bin/hadoop fs -text /user/raquel/sort-out/part-00000 | tail
