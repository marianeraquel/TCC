set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=VariandoDistribuicaoParticoes.pdf'
set encoding iso_8859_1
set style data histogram #linespoints 
set key right top
set grid 
set ylabel "COV das Partições" 
set xlabel "Distribuição"

set boxwidth 0.5
set style fill solid
#set logscale y
#set yrange[0:25]
set xrange[-0.2:2.5]
set xtics ("Uniforme" 0, "Normal" 1, "Pareto" 2)
set xtics offset 1.2, 0

plot "QestatParticoes" u 8 title "Quicksort" , "SestatParticoes" u 8 title "SampleSort"