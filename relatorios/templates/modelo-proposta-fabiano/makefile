all: 
	latex modelo.tex
	bibtex modelo
	latex modelo.tex
	latex modelo.tex
	dvips modelo.dvi -o modelo.ps
run: clean all 
	gv modelo.ps &
html: clean all
	latex2html modelo.tex
clean:
	rm modelo.dvi modelo.ps *.aux *.bbl *.blg *.log *.toc
