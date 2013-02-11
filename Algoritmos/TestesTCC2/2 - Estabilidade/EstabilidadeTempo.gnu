set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeTempo.pdf'
set encoding iso_8859_1
set style data histogram #data linespoints
set key right top
set grid 
set ylabel "Tempo (s)"
set xlabel "Execução"
#set title "Distribuicao Pareto"
set boxwidth 0.5
set style fill solid

set yrange[0:300]
set xrange[-0.1:9.35]

set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"

plot "QEstabilidade8" u 1 title "Quicksort" ls 2, \
"SEstabilidade8" u 1 title "SampleSort" ls 3