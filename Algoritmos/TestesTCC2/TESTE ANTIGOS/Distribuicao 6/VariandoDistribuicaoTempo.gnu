set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=VariandoDistribuicaoTempo.pdf'
set encoding iso_8859_1
set style data histogram # linespoints 
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Distribuição"

set boxwidth 0.5
set style fill solid
set yrange[0:25]
set xrange[-0.2:2.5]
set xtics ("Uniforme" 0, "Normal" 1, "Pareto" 2)
set xtics offset 1.2, 0
plot "Qestat" u 4 title "Quicksort" , "Sestat" u 4 title "SampleSort" 

