set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=DadosTempo.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Quantidade de Dados"
set logscale
set format x "10^{%L}"
set format y "10^{%L}"

set xrange[1000000:10000000000.0]

set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"

plot "Qestat" u 1:4 title "Quicksort" ls 2, "Sestat" u 1:4 title "SampleSort" ls 3