set terminal postscript 20 color 
set style data points
set grid
set encoding utf8

set output "| epstopdf --filter --outfile=Normal.pdf"
set key left top
set xlabel "Valor"
set ylabel "Frequência"
set title "Distribuição Normal"
plot "PlotNormal.txt" u 1:2 title ""



