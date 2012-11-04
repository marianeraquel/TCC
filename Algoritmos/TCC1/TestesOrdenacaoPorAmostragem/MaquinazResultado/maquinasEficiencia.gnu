set terminal postscript 18 eps enhanced color 
set output 'MaquinasEficiencia.ps'
set encoding iso_8859_1
set style data linespoints
set key right bottom
set grid 
set ylabel "Eficiência"
set xlabel "Processadores"
#set xrange[4:11]
set yrange[80:100]
#set title "Distribuicao Pareto"
plot "maquinasEficiencia.txt" u 1:2 title "Uniforme" lw 4 ,  "maquinasEficiencia.txt" u 1:3 title "Normal" lw 4,  "maquinasEficiencia.txt" u 1:4 title "Pareto" lw 4



