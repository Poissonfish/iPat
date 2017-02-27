args = commandArgs(trailingOnly=TRUE)

list.of.packages <- c("bigmemory", "biganalytics")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")

library(bigmemory)
library(biganalytics)
library(compiler) #this library is already installed in R 
source("http://zzlab.net/GAPIT/gapit_functions.txt")
source("http://zzlab.net/FarmCPU/FarmCPU_functions.txt")

GM.path = args[1]
GD.path = args[2]
Y.path = args[3]
C.path = args[4]
method.bin= args[5] #"optimum"
maxLoop= as.numeric(args[6])
MAF.calculate= as.logical(args[7])
maf.threshold= as.numeric(args[8])
wd = args[9]

# GM.path="/Users/Poissonfish/aaafolder/mdp_SNP_information.txt"
# GD.path="/Users/Poissonfish/aaafolder/mdp_numeric.txt"
# Y.path="/Users/Poissonfish/aaafolder/mdp_traits_validation.txt"
# C.path = "NULL" 
# method.bin = "static" 
# maxLoop=as.numeric("10") 
# MAF.calculate = as.logical("FALSE") 
# maf.threshold=as.numeric("0.05") 
# wd="/Users/Poissonfish/Desktop/output"

setwd(wd)
if(GM.path=="NULL"){GM=NULL}else{GM=read.table(GM.path, head=TRUE)}
if(GD.path=="NULL"){GD=NULL}else{GD=read.table(GD.path, head=TRUE)}
if(C.path=="NULL"){C=NULL}else{C=read.table(C.path, head=TRUE)}
print(length(args))

print('FarmCPU start')
tryCatch(
  {x=FarmCPU(
    Y = read.table(Y.path, head=TRUE),
    GM = GM,
    GD = GD,
    CV = C,
    method.bin= method.bin,
    bin.size= c(5e5,5e6,5e7), #default set of bin.size 
    bin.selection= seq(10,100,10), #default set of bin.selection
    maxLoop = maxLoop,
    MAF.calculate=MAF.calculate,
    maf.threshold=maf.threshold
  )},error = function(e){
    print(e)
  }
)
print(warnings())