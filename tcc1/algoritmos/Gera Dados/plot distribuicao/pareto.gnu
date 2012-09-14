set terminal postscript color
set style data points
set grid


set output "Pareto.eps"
set key right top
set xlabel "numero"
set ylabel "frequencia"
set title "Distribuicao Pareto"
set logscale
plot "PARETO09.txt" u 1:2 title "Pareto 0.9"
#plot "PARETO10.txt" u 1:2 title "Pareto 1.0"
#plot "PARETO11.txt" u 1:2 title "Pareto 1.1"



