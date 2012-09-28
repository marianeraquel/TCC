set terminal  postscript 20 color
set style data points
set grid

set encoding utf8
set output "| epstopdf --filter --outfile=Pareto.pdf"
set key right top
set xlabel "Valor"
set ylabel "Frequência"
set title "Distribuição Pareto"
set logscale
set yrange[0.000008:0.002]
plot "PlotParetoSort.txt" u 1:2:xtic(3) title ""
