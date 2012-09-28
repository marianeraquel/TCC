set terminal  postscript 20 color
set style data points
set grid

set encoding utf8
set output "| epstopdf --filter --outfile=Uniforme.pdf"
set key right top
set xlabel "Valor"
set ylabel "Frequência"
set yrange [0:0.005]
set title "Distribuição Uniforme"
plot "PlotUniforme.txt" u 1:2 title ""



