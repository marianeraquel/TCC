set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=VariandoMaquinasParticoes.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "COV"
set xlabel "Número de Máquinas"
set yrange [0:0.2]
#set logscale y
#set format y "10^{%L}" 

plot "QestatParticoes" u 1:8 title "Quicksort" lw 7, "SestatParticoes" u 1:8 title "SampleSort" lw 7