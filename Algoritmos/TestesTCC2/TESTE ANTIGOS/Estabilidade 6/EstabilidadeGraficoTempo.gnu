set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeTempo.pdf'
set encoding iso_8859_1
set style data histogram #data linespoints
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Execução"
#set title "Distribuicao Pareto"
set boxwidth 0.5
set style fill solid

set yrange[0:25]
set xrange[-0.2:10.5]
plot "QEstabilidade6" u 1 title "QuickSort" , "SEstabilidade6" u 1 title "SampleSort"


