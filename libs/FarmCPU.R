args = commandArgs(trailingOnly=TRUE)
GM.path = args[1]
GD.path = args[2]
Y.path = args[3]
C.path = args[4]
method.bin= args[5] #"optimum"
maxLoop= as.numeric(args[6])
MAF.calculate= as.logical(args[7])
maf.threshold= as.numeric(args[8])
wd = args[9]
lib = args[10]

GM.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Numeric/mdp_SNP_information.txt"
GD.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Numeric/mdp_numeric.txt"
Y.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Numeric/mdp_traits.txt"
C.path = "NULL"
method.bin = "optimum"
maxLoop=as.numeric("2")
MAF.calculate = as.logical("TRUE")
maf.threshold=as.numeric("0.2")
wd="/Users/Poissonfish/Desktop/test/farm"
lib = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/libs/"

setwd(lib)
list.of.packages <- c("bigmemory", "biganalytics", "data.table","MASS", "gplots", "compiler", "scatterplot3d", "R.utils")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
library(bigmemory)
library(biganalytics)
library(compiler) #this library is already installed in R 
library(data.table)
library(MASS) # required for ginv
library(multtest)
library(gplots)
library(scatterplot3d)
library(R.utils)
source("./*Function_EMMA.R")
source("./*Function_GAPIT.R")
source("./*Function_FarmCPU.R")
source("./*Function_LDRemove.R")

setwd(wd)
if(GM.path=="NULL"){GM=NULL}else{GM=read.table(GM.path, head=TRUE)}
if(GD.path=="NULL"){GD=NULL}else{GD=read.table(GD.path, head=TRUE)}
if(C.path=="NULL"){C=NULL}else{C=read.table(C.path, head=TRUE)}
print(length(args))

Y = read.table(Y.path, head=TRUE)
#Select Phenotype
trait = c()
if(length(args)>10){
  for (i in 11:length(args)){
    trait = c(trait, as.numeric(args[i])) 
  }
  Y = Y[,c(1,trait+1)]
}
trait_names = names(Y)[-1]

print('FarmCPU start')
tryCatch(
  {
    for (i in 1:length(trait_names)){
      x=FarmCPU(
        Y = Y[,c(1,1+i)],
        GM = GM,
        GD = GD,
        CV = C,
        method.bin= method.bin,
        bin.size= c(5e5,5e6,5e7), #default set of bin.size 
        bin.selection= seq(10,100,10), #default set of bin.selection
        maxLoop = maxLoop,
        MAF.calculate=MAF.calculate,
        maf.threshold=maf.threshold,
        memo= trait_names[i])
      #Prediction
      #Sorted by P-value
      SNP_p = data.frame(name = x$GWAS$SNP, p = x$GWAS$P.value)
      SNP_p = SNP_p[order(SNP_p$p),]
      #Select associated SNP
      sig = SNP_p$p < (.05) #length(SNPname)
      GD_sig = GD[,SNP_p$name[sig]]
      #LD Remove
      if(length(sig)>1) LD_remain = Blink.LDRemove(GD_sig, .7, 1:sum(sig), orientation = "col")
      GD_sig = GD_sig[,LD_remain] 
      #Check number
      if(!is.null(ncol(GD_sig))){ # prevent GD_sig has only one column
        if(nrow(Y)<ncol(GD_sig) && is.null(ncol(C))){
          redundant = ncol(GD_sig) - nrow(Y)
          GD_sig = GD_sig[,1:(ncol(GD_sig)-redundant)]
        }else if(!is.null(ncol(C)) && nrow(Y) < (ncol(GD_sig) + ncol(C))){
          redundant = ncol(GD_sig) + ncol(C) - nrow(Y)
          GD_sig = GD_sig[,1:(ncol(GD_sig)-redundant)]}
      }  
      #GAPIT do prediction
      if(is.null(ncol(C))){CV = cbind(Y[,1],GD_sig)}else{CV = cbind(Y[,1], C, GD_sig)}
      pred <- GAPIT(
        Y = Y[,c(1,1+i)],
        GM = GM,
        GD = GD,
        PCA.total=3,
        CV = CV,
        group.from=10000,
        group.to=10000,
        group.by=10,
        SNP.test=FALSE,
        memo= trait_names[i]
      )
    }
    },error = function(e){
    print(e)
  }
)
print(warnings())