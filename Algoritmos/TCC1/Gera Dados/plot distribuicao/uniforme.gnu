set terminal postscript  color
set style data linespoints
set encoding utf8
set grid


set output "Uniforme.eps"
set key left top
set xlabel "números"
set ylabel "média"
set title "Distribuição Uniforme"
plot "UNIFORME.txt" u 1:2 title "Uniforme"



