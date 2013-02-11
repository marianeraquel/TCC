set encoding iso_8859_1
set style fill solid 0.5 border -1
set style boxplot outliers pointtype 7
set style data boxplot
set boxwidth  0.1
set pointsize 0.5

unset key

set style line 2 lt 1 lw 2 pt 7  lc rgb "red"
set style line 3 lt 1 lw 2 pt 13 ps 1.4 lc rgb "#4169E1"
set format y "%.1sx10^{%S}"

# OPCAO 1
set terminal postscript 18 enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes1.pdf'

set multiplot
#set size 0.5, 1

set yrange[40000000:60000000]
set origin 0,0
set xtics ("QuickSort" 1)
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1 ls 2 


set yrange[5000000:25000000]
#set origin 0.5,0
set xtics ("SampleSort" 2)
plot "<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (2):1  ls 3

unset multiplot

# OPCAO 2
set terminal postscript 18 enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes2.pdf'

set format y "%.1sx10^{%S}"
set multiplot
#set size 0.5,1
set origin 0,0
set xtics ("QuickSort" 1)
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1 ls 2 


set yrange[9000000:11500000]
set origin 0.5,0
set xtics ("SampleSort" 2)
plot "<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (2):1  ls 3

unset multiplot

# OPCAO 3
set terminal postscript 18  enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes3.pdf'


set logscale y
#set size 0.5,1
set yrange[9000000:100000000]
set xtics ("QuickSort" 1, "SampleSort" 2)
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1 ls 2 , \
"<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (2):1  ls 3


#set multiplot
#set size 0.4,1.5
#set xtics ("QuickSort" 1, "SampleSort" 1.5)
#plot "QEstabilidade8" u (1):1 lc rgb "red" , "SEstabilidade8" u (1.5):1  lc rgb "#4169E1"
#unset multiplot

#set grid 
#set yrange [0:240]
#set ylabel "Tempo médio (s)"
#set xlabel "Proximidade dos elementos"
#set title "Distribuicao Pareto"
#plot "QEstabilidade8" u (1):1  
#set xtics ("SampleSort" 1) scale 0.0
#plot "SEstabilidade8" u (1):1
#set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
#set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"

#set multiplot
#set size 0.5,1
#set origin 0,0
#set xtics ("QuickSort" 1) scale 0.0
#plot "QEstabilidade8" u (1):1  lw 2 lc rgb "red"
#set origin 0.5,0
#set xtics ("SampleSort" 1) scale 0.0
#plot "SEstabilidade8" u (1):1  lw 2 lc rgb "#4169E1"
#unset multiplot

#set border 4
#set xtics nomirror
#set ytics nomirror
#pt 3 ps 2
#set output '| epstopdf --filter --outfile=EstabilidadeSPart.pdf'
#plot "SParticoes" u 2:1 title "SampleSort" pt 3 ps 2
#print "*** Boxplot demo ***"
#plot 'silver.dat' using (1):2, '' using (2):(5*$3)


 
