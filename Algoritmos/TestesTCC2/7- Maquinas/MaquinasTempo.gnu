set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=MaquinasTempo.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Número de Máquinas"

#set logscale
#set format x "10^{%L}"
set yrange[0:450]
#set xrange[-0.2:10.5]
set xtics 1

set style line 1 lt 1 lw 6 pt 2 ps 1.3 lc rgb "#32CD32"
set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"


plot "Qestat" u 1:4 title "Quicksort" ls 2, "Sestat" u 1:4 title "SampleSort" ls 3
