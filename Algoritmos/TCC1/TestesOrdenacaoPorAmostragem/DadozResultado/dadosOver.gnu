set terminal postscript 18 eps enhanced color 
set output 'DadosOverhead.ps'
set encoding iso_8859_1
set style data linespoints
set key right top
set grid 
set logscale x
set ylabel "Tempo médio (s)"
set xlabel "Conjuntos de 10^6 dados"
#set title "Distribuicao Pareto"
#set yrange[0:45]
plot "dadosOver.txt" u 2:3 title "Uniforme" lw 4, "dadosOver.txt" u 2:4 title "Normal" lw 4,  "dadosOver.txt" u 2:5:xtic(1) title "Pareto" lw 4



