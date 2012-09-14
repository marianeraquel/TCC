all: 
	latex modelo.tex
	bibtex modelo
	latex modelo.tex
	latex modelo.tex
	dvips modelo.dvi -o modelo.ps
        ps2pdf modelo.ps
run: clean all 
	acroread modelo.pdf &
clean: 
       rm modelo.ps *.lot *.lof *.aux *.bbl *.blg *.log *.toc
