# floats
awk '{h[sprintf("%.3f",$1)]++} END {for (i in h) {print i, h[i]/NR}}' entrada > saida
