set terminal postscript 18 eps enhanced color 
set output 'MaquinasTempo.ps'
set encoding iso_8859_1
set style data linespoints
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Máquinas"
#set title "Distribuicao Pareto"
plot "maquinasTempos.txt" u 1:2 title "Uniforme" lw 4 ,  "maquinasTempos.txt" u 1:3 title "Normal" lw 4,  "maquinasTempos.txt" u 1:4 title "Pareto" lw 4



