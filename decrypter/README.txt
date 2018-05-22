Automated Stripping/Decrypting of PDF Security

1. Download qpdf-<version>-bin-msvc64.zip from https://github.com/qpdf/qpdf/releases
1a. May need to install qpdf library dependencies, check the qpdf README if you experience errors
2. Replace path in decrypt-here.bat to wherever you unzipped the download from Step 1
3. Copy the decrypt-here.bat into root of where you want to decrypt pdfs, it will decrypt anything with .pdf extension recursively
4. Run decrypt-here.bat in desired location