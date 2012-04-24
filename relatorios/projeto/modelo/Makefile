NAME=tcc

all: compile clean
compile: $(NAME).tex
	@pdflatex $(NAME).tex
	@bibtex $(NAME)
	@pdflatex $(NAME).tex
	pdflatex $(NAME).tex
	
clean:
	rm *.log *.toc *.bbl *.aux *.blg *.lof *.lot *.out



