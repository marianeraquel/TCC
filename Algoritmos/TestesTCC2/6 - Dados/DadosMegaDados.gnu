set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=DadosMegaDados.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "Tempo médio (s)"
set xlabel "Quantidade de 10^6 Dados"
set logscale x 
#set yrange[0:25]
#set xrange[-0.2:10.5]

set format x "10^{%L}"

set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"

plot "<awk '{print $1/10^6, $4*10^6/$1}' Qestat" u 1:2 title "Quicksort" ls 2, \
"<awk '{print $1/10^6, $4*10^6/$1}' Sestat" u 1:2 title "SampleSort" ls 3