# imports
devtools::install_github("Poissonfish/RiPat")
library(ape)
library(BGLR)
library(biganalytics)
library(bigmemory)
library(compiler) #this library is already installed in R
library(data.table)
library(devtools)
library(EMMREML)
library(genetics)
library(ggplot2)
library(gplots)
library(LDheatmap)
library(magrittr)
library(MASS) # required for ginv
library(multtest)
library(R.utils)
library(rrBLUP)
library(scatterplot3d)
source("http://zzlab.net/FarmCPU/FarmCPU_functions.txt")
source("http://zzlab.net/GAPIT/emma.txt")
source("http://zzlab.net/GAPIT/gapit_functions.txt")
source("http://zzlab.net/iPat/Function_iPat.R")

# get args
args = commandArgs(trailingOnly=TRUE)
pkgCalled = args[1]
isSERVER = FALSE

# SERVER SPECIFIC
for (i in 1:length(args)) {
  if (args[i] == "-cid") {
    i = i + 1
    SERIAL = args[i]
    isSERVER = TRUE
  } else if (args[i] == "-phenotype") {
    i = i + 1
    rawPhenotype = fread(args[i])
    args = c(args, "-pSelect", names(rawPhenotype)[-1] %>% paste(collapse="sep"))
  }
}

if (isSERVER) {
  if (pkgCalled == "GAPIT") {
    args = c(args, "-arg", "CMLM", "3")
  } else if (pkgCalled == "FarmCPU") {
    args = c(args, "-arg", "optimum", "10")
  } else if (pkgCalled == "rrBLUP") {
    args = c(args, "-gwas", "FALSE", "-gs", "FALSE", "5", "10")  
  } else if (pkgCalled == "BGLR") {
    args = c(args, "-gwas", "FALSE", "-gs", "FALSE", "5", "10")  
    args = c(args, "-arg", "BayesB", "10000", "1000")  
  }
}

# run RiPat
tryCatch({
    RiPat::runArgs(args)
  }, 
  error = function(e) {
    stop(e)
  }
)

# SERVER SPECIFIC
if (isSERVER) {
  # convert pdf to png
  print("Converting PDF to PNG ......")
  filesPDF = list.files() %>% grep(".pdf", ., value=T)
  for (pdf in filesPDF) {
    print(pdf)
    sprintf("pdftoppm %s %s -png -rx 300 -ry 300", pdf, gsub(".pdf", "", pdf)) %>% 
    system()
  }
  print("DONE")

  # pos
  print("Uploading files to database")
  sprintf("nohup python3 ../POS.py %s", SERIAL) %>% system()
  print("ALL DONE!")
}
