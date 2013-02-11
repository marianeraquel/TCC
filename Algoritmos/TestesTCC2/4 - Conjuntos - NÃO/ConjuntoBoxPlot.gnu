#set terminal postscript 18 eps enhanced color 
#set output '| epstopdf --filter --outfile=EstabilidadeQPart.pdf'
set terminal pdf
set output 'EstabilidadeBoxPlot.pdf'
set encoding iso_8859_1
set style fill solid 0.5 border -1
set style boxplot outliers pointtype 7
set style data boxplot
set boxwidth  0.1
set pointsize 0.5

unset key
set border 2

#set xtics ("QuickSort" 1) scale 0.0
set xtics nomirror
set ytics nomirror
#set yrange [0:240]

#set grid 
#set ylabel "Tempo médio (s)"
#set xlabel "Proximidade dos elementos"
#set title "Distribuicao Pareto"

#plot "QEstabilidade8" u (1):1  
#set xtics ("SampleSort" 1) scale 0.0
#plot "SEstabilidade8" u (1):1


set multiplot
set size 0.5,1
set origin 0,0
set xtics ("QuickSort" 1) scale 0.0
plot "QEstabilidade8" u (1):1  lw 2
set origin 0.5,0
set xtics ("SampleSort" 1) scale 0.0
plot "SEstabilidade8" u (1):1  lw 2
unset multiplot


#pt 3 ps 2
#set output '| epstopdf --filter --outfile=EstabilidadeSPart.pdf'
#plot "SParticoes" u 2:1 title "SampleSort" pt 3 ps 2
#print "*** Boxplot demo ***"
#plot 'silver.dat' using (1):2, '' using (2):(5*$3)

set multiplot
set size 0.5,1
set origin 0,0
set xtics ("QuickSort" 1) scale 0.0
plot "QEstabilidade8" u (1):(2):(3)  lw 2
set origin 0.5,0
set xtics ("SampleSort" 1) scale 0.0
plot "SEstabilidade8" u (1):1  lw 2
unset multiplot
