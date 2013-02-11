set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=VariandoDadosParticoes.pdf'
set encoding iso_8859_1
set style data linespoints 
set key right top
set grid 
set ylabel "Tamanho das Partições"
set xlabel "Quantidade de Dados"
set logscale 
set format x "10^{%L}" 
set format y "10^{%L}" 
plot "QestatParticoes" u 1:4 title "Quicksort" lw 7, "SestatParticoes" u 1:4 title "SampleSort" lw 7