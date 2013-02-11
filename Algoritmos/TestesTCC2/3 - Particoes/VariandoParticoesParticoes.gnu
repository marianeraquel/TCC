set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=VariandoParticoesParticoes.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "COV das Partições"
set xlabel "Número de Partições"
#set logscale y
#set format x "10^{%L}" 
#set format y "10^{%L}" 
set yrange[0:0.5]
#set xrange[0]
set xtics 2
plot "QestatParticoes" u 1:8 title "Quicksort" lw 7, "SestatParticoes" u 1:8 title "SampleSort" lw 7