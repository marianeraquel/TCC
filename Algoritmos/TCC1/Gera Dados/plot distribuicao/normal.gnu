set terminal postscript color 
set style data points
set grid


set output "Normal.eps"
set key left top
set xlabel "numero"
set ylabel "frequencia"
set title "Distribuicao Normal"
plot "NORMAL.txt" u 1:2 title "Normal"



