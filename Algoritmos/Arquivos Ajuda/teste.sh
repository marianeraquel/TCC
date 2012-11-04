folder="/usr/local/hadoop/marianehadoop/hadoop"
CMD="$folder/bin/hadoop jar $folder/hadoop-*-examples.jar pi 10 1000"
echo "Executando $CMD"
eval "$CMD"


folder="/home/marianehadoop"
CMD="ls $folder"
echo "Executando $CMD"
eval "$CMD"
