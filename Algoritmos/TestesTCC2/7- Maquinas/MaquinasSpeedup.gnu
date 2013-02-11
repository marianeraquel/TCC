set terminal postscript 18 eps enhanced color 
set output '| epstopdf --filter --outfile=MaquinasSpeedup.pdf'
set encoding iso_8859_1
set style data linespoints 
set key left top
set grid 
set ylabel "Speedup"
set xlabel "Processadores"
set xtics 1

set style line 1 lt 1 lw 6 pt 2 ps 1.3 lc rgb "#32CD32"
set style line 2 lt 1 lw 5 pt 7 ps 1.7 lc rgb "red"
set style line 3 lt 1 lw 5 pt 13 ps 2.1 lc rgb "#4169E1"


plot  "<awk '{A[NR] = $1; print $1*2, $1/A[1]}' Qestat" u 1:2 title "Ideal" ls 1, \
"<awk '{A[NR] = $4; print $1*2, A[1]/$4}' Qestat" u 1:2 title "Quicksort" ls 2, \
"<awk '{A[NR] = $4; print $1*2, A[1]/$4}' Sestat" u 1:2 title "Samplesort" ls 3