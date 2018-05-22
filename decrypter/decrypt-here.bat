ECHO OFF

for /r %%f in (*.pdf) do <YOUR_PATH_TO_UNZIPPED_QPDF>\qpdf-<version>\bin\qpdf.exe --decrypt "%%f" "%%~pf%%~nf-decrypted.pdf"