############################################################
# Comando: awk -f estat.awk dados.txt                      #
############################################################
# comando com dados não ordenados: awk -f estat.awk dados.txt | sort -g

BEGIN{
   # Armazena a soma de todos as observações
	soma_ai=0
	# Armazena a soma do desvio médio de cada Ai ao quadrado
	soma_desvio=0
}

{
	# NR indica um número de linha
	A[NR] = $1
	soma_ai+=$1
}

END{
	#NR indica um número de linha (aqui contém o número da última)
	menor = A[1]
	maior - A[NR]	
	media = soma_ai/NR
	if (NR % 2 == 0)
		mediana = (A[NR/2] + A[(NR/2)+1]) / 2
	else
		mediana =  A[(NR+1)/2]
	for(i = 1; i <= NR; i++)
		soma_desvio += (A[i] - media)^2
	variancia = soma_desvio / NR

	print "Menor: " A[1]
	print "Maior: " A[NR]
	print "Média: " media
	print "Mediana: " mediana
	print "Variancia: " variancia
	print "Desvio padrão: " sqrt(variancia)
	print "Coeficiente de variação: " sqrt(variancia) / media
	print "Maior",	 "Media", 	"Mediana", 	"COV"
	print A[NR],	 media,	 	mediana, 	sqrt(variancia)/media
}
