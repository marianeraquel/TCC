set terminal postscript 18 eps monochrome#enhanced color 
set output 'dd.eps' #'| epstopdf --filter --outfile=DistribuicaoTempoe.pdf'
set encoding iso_8859_1
set style data histogram # linespoints 
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Distribuição"


set boxwidth 0.4
set style fill solid
set yrange[0:250]
set xrange[-0.1:2.5]
set xtics ("Uniforme" 0, "Normal" 1, "Pareto" 2)
set xtics offset 1.2, 0


set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"

plot "Qestat" u 4 title "Quicksort" ls 2, "Sestat" u 4 title "SampleSort" ls 3

