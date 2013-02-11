
set encoding iso_8859_1
set style fill solid 0.5 border -1
set style boxplot outliers pointtype 7
set style data boxplot
set boxwidth  0.1
set pointsize 0.5
set style line 2 lt 1 lw 2 pt 7  lc rgb "red"
set style line 3 lt 1 lw 2 pt 13 ps 1.4 lc rgb "#4169E1"
unset key
set format y "%.1sx10^{%S}"


# OPCAO 1
set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes1.pdf'
set multiplot
set size 0.5,1

set origin 0,0
set xtics ("QuickSort" 1) scale 0.0
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1  ls 2

set origin 0.5,0
set yrange [9000000:11500000]
set xtics ("SampleSort" 1) scale 0.0
plot "<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1  ls 3
unset multiplot


# OPCAO 2

set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes2.pdf'
set multiplot
set size 0.5,1

set origin 0,0
set yrange[40000000:60000000]
set xtics ("QuickSort" 1) scale 0.0
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1  ls 2

set origin 0.5,0
set yrange[5000000:25000000]
set xtics ("SampleSort" 1) scale 0.0
plot "<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1  ls 3
unset multiplot


# OPCAO 3
set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=EstabilidadeParticoes3.pdf'
set multiplot
set size 0.5,1
set origin 0,0

set logscale y
set yrange[9000000:100000000]
set xtics ("QuickSort" 1, "SampleSort" 2)
plot "<awk '{$1=\"\"; print $0}' QEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (1):1 ls 2 , \
"<awk '{$1=\"\"; print $0}' SEstabilidade8 | awk '{ for (i=1; i<=NF; i++) print $i }'" u (2):1  ls 3
unset multiplot