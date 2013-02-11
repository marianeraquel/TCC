############################################################
# Comando: awk -f estat.awk dados.txt                      #
#							                                      #
# Dado com arquivo texto ordenado com n elementos Ai, este #
# programa calcula medidas de:                             #
#                                                          #
# (*) Tendencia Central                                    #
#                                                          #
# - media aritm�tica simples (x|): somat�rio de Ai dividi- #
#                                  do por n                #
# - mediana (Md): obt�m o elemento do meio do conjunto. Se #
#                 n for par, obt�m a m�dia dos dois Ai     #
#                 mais centrais                            #
# - quartis (qi): divide a distribui��o de freq��ncia em   #
#                 quartas partes (cada uma tem 25% dos da- #
#                 dos). S�o q1 (25%), q2 = Md (50%) e q3   #
#                 (75%)                                    #
# - percentis (pi): divide a distribui��o de freq��ncia em #
#                   cent�simas partes (cada uma tem 1% dos #
#                   dados). S�o p1 (1%), ..., p99 (99%)    #
#                                                          #
# (*) Dispers�o                                            #
#                                                          #
# - variancia (sigma^2): somat�rio de (Ai - x|)^2 (desvio  #
#                        m�dio) dividido por n             #
# - desvio padr�o (sigma): raiz quadrada da variancia      #
# - coeficiente de varia��o (V): desvio padr�o dividido    #
#                                pela x|                   #
#                                                          #
# By Catia Garcia Morais (nov/2002) - catiam@terra.com.br  #
############################################################

BEGIN{
   # Armazena a soma de todos as observa��es
	soma_ai=0
	# Armazena a soma do desvio m�dio de cada Ai ao quadrado
	soma_desvio=0
}

{
	# NR indica um n�mero de linha
	A[NR] = $1
	soma_ai+=$1
}

END{
	#NR indica um n�mero de linha (aqui cont�m o n�mero da �ltima)
	print "Menor: " A[1]
	print "Maior: " A[NR]

	media = soma_ai/NR
	print soma_ai 
	print NR
	print "M�dia: " media

	if (NR % 2 == 0)
		mediana = (A[NR/2] + A[(NR/2)+1]) / 2
	else
		mediana =  A[(NR+1)/2]
	print "Mediana: " mediana

	for(i = 1; i <= NR; i++)
		soma_desvio += (A[i] - media)^2
	variancia = soma_desvio / NR
	print "Variancia: " variancia

	print "Desvio padr�o: " sqrt(variancia)

	print "Coeficiente de varia��o: " sqrt(variancia) / media

	# Identifica��o dos quartis

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
