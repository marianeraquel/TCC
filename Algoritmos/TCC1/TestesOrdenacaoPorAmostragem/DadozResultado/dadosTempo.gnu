set terminal postscript 18 eps enhanced color 
set output 'DadosTempo.ps'
set encoding iso_8859_1
set style data linespoints
set key left top
set grid 
set logscale
set ylabel "Tempo médio (s)"
set xlabel "Quantidade de dados"
#set title "Distribuicao Pareto"
#set yrange[0:45]
plot "dadosTempo.txt" u 2:3 title "Uniforme" lw 4, "dadosTempo.txt" u 2:4 title "Normal" lw 4,  "dadosTempo.txt" u 2:5:xtic(1) title "Pareto" lw 4



