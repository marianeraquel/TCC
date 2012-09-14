NAME=tcc
AUXNAME=$(NAME).aux

all: compile clean2

compile2: $(NAME).tex
	@pdflatex -shell-escape -file-line-error $(NAME) | grep -i ".*:[0-9]*:.*\|warning" 
	@bibtex $(AUXNAME)
	@pdflatex -shell-escape -file-line-error $(NAME) | grep -i ".*:[0-9]*:.*\|warning"
	@pdflatex -shell-escape -file-line-error $(NAME) | grep -i ".*:[0-9]*:.*\|warning"

compile: $(NAME).tex
	pdflatex $(NAME).tex
	bibtex $(NAME)
	pdflatex $(NAME).tex
	pdflatex $(NAME).tex
	
clean:
	rm *.log *.toc *.bbl *.aux *.blg *.toc *.tof

clean2:
	rm ./*/*.aux
