args = commandArgs(trailingOnly=TRUE)
G.path = args[1]
GM.path = args[2]
GD.path = args[3]
Y.path = args[4]
K.path = args[5] 
SNP.test = as.logical(args[6])
C.path = args[7]
PCA = as.numeric(args[8])
C.inher = as.numeric(args[9])
ki.c = args[10]
ki.g = args[11]
g.from = as.numeric(args[12])
g.to = as.numeric(args[13])
g.by = as.numeric(args[14])
model.s = as.logical(args[15])
snp.fraction = as.numeric(args[16])
file.fragment = as.numeric(args[17])
wd = args[18]
lib = args[19]
format = args[20]
arg_length = 20

# G.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/VCF/data.vcf"
# GM.path = "NULL"
# GD.path = "NULL"
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/VCF/data.txt"
# K.path = "NULL"
# SNP.test = TRUE
# C.path = "NULL"
# PCA = 3
# ki.c = 'average'
# ki.g = 'Mean'
# g.from = 1
# g.to = 1
# g.by = 10
# model.s = FALSE
# snp.fraction = 1
# file.fragment = 512
# wd = "/Users/Poissonfish/Desktop/test/gapit"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# format = "VCF"
# args = c(1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3)

setwd(lib)
list.of.packages <- c("MASS", "data.table", "magrittr", "gplots", "compiler", "scatterplot3d", "R.utils")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
if(!'multtest'%in% installed.packages()[,"Package"]){
  source("http://www.bioconductor.org/biocLite.R") 
  biocLite("multtest")
}
library(MASS) # required for ginv
library(multtest)
library(gplots)
library(compiler) #required for cmpfun
library(scatterplot3d)
library(R.utils)
library(data.table)
library(magrittr)
source("./*Function_EMMA.R")
source("./*Function_FarmCPU.R")
source("./*Function_GAPIT.R")

setwd(wd)
# Select Phenotype
Y.file = read.table(Y.path, head=TRUE)
trait = c()
if(length(args)>arg_length){
  for (i in (arg_length+1):length(args)){
    trait = c(trait, as.numeric(args[i])) 
  }
  print(trait)
  Y.file = Y.file[,c(1,trait+1)]
}

# Format free
switch(format, 
  VCF = {
    sprintf("chmod 777 %s/blink", lib) %>% system()
    vcf = substring(G.path, 1, nchar(G.path)-4)
    sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
    sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
    GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y.file[,1], .)
    GM = read.table(sprintf("%s.map", vcf), head = TRUE)
    G = NULL
  },
  PLink_ASCII = {

  },
  PLink_Binary = {

  }, {
    if(G.path=="NULL"){G=NULL}else{G=read.delim(G.path, head=FALSE)}
    if(GM.path=="NULL"){GM=NULL}else{GM=read.table(GM.path, head=TRUE)}
    if(GD.path=="NULL"){GD=NULL}else{GD=read.table(GD.path, head=TRUE)}
  }
)
if(C.path=="NULL"){C=NULL}else{C=read.table(C.path, head=TRUE)}
if(K.path=="NULL"){K=NULL}else{K=read.table(D.path, head=FALSE)}
if(is.na(C.inher)) C.inher = NULL else C.inher = C.inher

print('GAPIT start')
tryCatch(
  {x=GAPIT(
    Y = Y.file,
    G = G,
    GM = GM,
    GD = GD,
    KI = K,
    SNP.test = SNP.test,
    CV = C,
    CV.Inheritance = C.inher,
    PCA.total = PCA,
    kinship.cluster = ki.c,
    kinship.group = ki.g,
    group.from = g.from,
    group.to = g.to,
    group.by = g.by,
    Model.selection = model.s,
    SNP.fraction = snp.fraction,
    file.fragment = file.fragment
  )},error = function(e){
    print(e)
  }
)
print(warnings())