set terminal postscript enhanced 18 color 
#set style data points
#set grid
set encoding iso_8859_15 
set output "| epstopdf --filter --outfile=3d.pdf"
set xlabel "Tamanho (MB)"
set ylabel	"Máquinas"
set zlabel	"Tempo" offset 4, 5
set title "Terasort, Sort e Ordenação Por Amostragem até 10^{10}"

#Configurações de exibição
set ytics 1
set lmargin 2


set dgrid3d

set samples 20
set isosamples 21
set contour base
set cntrparam bspline


splot "plot3D.txt" using 1:2:3 title "" #with points pt 7

#set key left top
#set zlabel	"Tempo" offset graph 0.1,0,0.7
