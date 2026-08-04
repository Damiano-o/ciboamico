$pdf_mode = 1;
$out_dir = 'out';
$pdflatex = 'pdflatex -interaction=nonstopmode -synctex=1 -halt-on-error %O %S';
# Copia il PDF nella cartella sorgente dopo la build (convenzione pi)
END { system('cmd /c copy /Y out\main.pdf main.pdf >nul 2>&1') if defined $out_dir; }
