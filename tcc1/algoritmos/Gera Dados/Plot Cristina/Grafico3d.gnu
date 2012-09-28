set terminal postscript enhanced 18 color 
set style data points
set grid
set encoding iso_8859_15 

set output "| epstopdf --filter --outfile=3d.pdf"
#set key left top
set xlabel "Tamanho (MB)"
set ylabel	"Máquinas"
set zlabel	"Tempo" offset graph 0.1,0,0.7
set title "Terasort, Sort e Ordenação Por Amostragem até 10^{10}"
set rmargin 20

set ticslevel 0.8
set logscale xz
set ytics 1
set view 60,15
#set zrange[0.1:30000]
#set contour base
#set contour
#set cntrparam levels 10
set dgrid3d
set pm3d

#set hidden3d

#set pm3d
#set view 45,60,1,1
splot "plot3D.txt" using 1:2:3 title "" with lines

#plot "PlotNormal.txt" u 1:2 



