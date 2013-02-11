set terminal postscript 18 eps enhanced color 
 set output '| epstopdf --filter --outfile=EstabilidadeQPart.pdf'
set encoding iso_8859_1
set style data points #linespoints
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Proximidade dos elementos"
#set title "Distribuicao Pareto"
set boxwidth 0.5
set style fill solid

set yrange[0:25]
set xrange[0:1]
plot "QParticoes" u 2:1 title "QuickSort" pt 3 ps 2

set output '| epstopdf --filter --outfile=EstabilidadeSPart.pdf'
plot "SParticoes" u 2:1 title "SampleSort" pt 3 ps 2


