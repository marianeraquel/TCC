set terminal postscript 18 eps enhanced color 
set output 'ConjuntoTempo.ps'
set encoding iso_8859_1
set style data linespoints
set key right bottom
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Conjunto de dados"
#set title "Distribuicao Pareto"
set yrange[0:45]
plot "conjuntosTempos.txt" u 1:2 title "Uniforme" lw 4, "conjuntosTempos.txt" u 1:3 title "Normal" lw 4,  "conjuntosTempos.txt" u 1:4 title "Pareto" lw 4



