############################################################
# Comando: awk -f estat.awk dados.txt                      #
#							                                      #
# Dado com arquivo texto ordenado com n elementos Ai, este #
# programa calcula medidas de:                             #
#                                                          #
# (*) Tendencia Central                                    #
#                                                          #
# - media aritmética simples (x|): somatório de Ai dividi- #
#                                  do por n                #
# - mediana (Md): obtém o elemento do meio do conjunto. Se #
#                 n for par, obtém a média dos dois Ai     #
#                 mais centrais                            #
# - quartis (qi): divide a distribuição de freqüência em   #
#                 quartas partes (cada uma tem 25% dos da- #
#                 dos). São q1 (25%), q2 = Md (50%) e q3   #
#                 (75%)                                    #
# - percentis (pi): divide a distribuição de freqüência em #
#                   centésimas partes (cada uma tem 1% dos #
#                   dados). São p1 (1%), ..., p99 (99%)    #
#                                                          #
# (*) Dispersão                                            #
#                                                          #
# - variancia (sigma^2): somatório de (Ai - x|)^2 (desvio  #
#                        médio) dividido por n             #
# - desvio padrão (sigma): raiz quadrada da variancia      #
# - coeficiente de variação (V): desvio padrão dividido    #
#                                pela x|                   #
#                                                          #
# By Catia Garcia Morais (nov/2002) - catiam@terra.com.br  #
############################################################

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
	print "Menor: " A[1]
	print "Maior: " A[NR]

	media = soma_ai/NR
	print soma_ai 
	print NR
	print "Média: " media

	if (NR % 2 == 0)
		mediana = (A[NR/2] + A[(NR/2)+1]) / 2
	else
		mediana =  A[(NR+1)/2]
	print "Mediana: " mediana

	for(i = 1; i <= NR; i++)
		soma_desvio += (A[i] - media)^2
	variancia = soma_desvio / NR
	print "Variancia: " variancia

	print "Desvio padrão: " sqrt(variancia)

	print "Coeficiente de variação: " sqrt(variancia) / media

	# Identificação dos quartis

	pq1 = (NR + 1 )/4
	intpq1 = int(pq1)
	frac = pq1 - intpq1
	if( frac != 0 )
		if( frac < 0.5 )
			q1 = A[ intpq1 ]
		else
			if( frac > 0.5 )
				q1 = A[ intpq1 + 1 ]
			else
				q1 = (A[intpq1] + A[intpq1 + 1])/2
	else
	    q1 = A[ intpq1 ]

	pq3 = (3 * (NR + 1))/4
	intpq3 = int(pq3)
	frac = pq3 - intpq3
	if( frac != 0 )
		if( frac < 0.5 )
			q3 = A[ intpq3 ]
		else
			if( frac > 0.5 )
				q3 = A[ intpq3 + 1 ]
			else
				q3 = (A[intpq3] + A[intpq3 + 1])/2
	else
	    q3 = A[ intpq3 ]

	print "Q1: ", q1
	print "Q2: ", mediana
	print "Q3: ", q3


}
