args = commandArgs(trailingOnly=TRUE)

list.of.packages <- c("MASS", "gplots", "compiler", "scatterplot3d", "R.utils")
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
source("http://www.zzlab.net/GAPIT/emma.txt")
source("http://www.zzlab.net/GAPIT/gapit_functions.txt")

G.path = args[1]
GM.path = args[2]
GD.path = args[3]
Y.path = args[4]
K.path = args[5] 
SNP.test = as.logical(args[6])
C.path = args[7]
PCA = as.numeric(args[8])
ki.c = args[9]
ki.g = args[10]
g.from = as.numeric(args[11])
g.to = as.numeric(args[12])
g.by = as.numeric(args[13])
model.s = as.logical(args[14])
snp.fraction = as.numeric(args[15])
file.fragment = as.numeric(args[16])
wd = args[17]

setwd(wd)
if(G.path=="NULL"){G=NULL}else{G=read.delim(G.path, head=FALSE)}
if(GM.path=="NULL"){GM=NULL}else{GM=read.table(GM.path, head=FALSE)}
if(GD.path=="NULL"){GD=NULL}else{GD=read.table(GD.path, head=FALSE)}
if(C.path=="NULL"){C=NULL}else{C=read.table(C.path, head=TRUE)}
if(K.path=="NULL"){K=NULL}else{K=read.table(D.path, head=FALSE)}
###rscript ./libs/Gapit.R '/Users/Poissonfish/all demofile/G.txt' NULL NULL '/Users/Poissonfish/all demofile/P.txt' 'NULL' TRUE 'NULL' 3 average Mean 1 10000 10000 FALSE 1 512 '/Users/Poissonfish/Desktop/output2'

print('GAPIT start')
tryCatch(
  {x=GAPIT(
    Y = read.table(Y.path, head=TRUE),
    G = G,
    GM = GM,
    GD = GD,
    KI = K,
    SNP.test = SNP.test,
    CV = C,
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