set terminal postscript 18 eps enhanced color 
set output 'MaquinasSpeedup.ps'
set encoding iso_8859_1
set style data linespoints
set key left top
set grid 
set ylabel "Speedup"
set xlabel "Processadores"
set xrange[4:11]
set yrange[0:3]
#set title "Distribuicao Pareto"
plot "maquinasSpeedup.txt" u 1:2 title "Uniforme" lw 4 ,  "maquinasSpeedup.txt" u 1:3 title "Normal" lw 4,  "maquinasSpeedup.txt" u 1:4 title "Pareto" lw 4, "maquinasSpeedup.txt" u 1:5 title "Ideal" lw 4



