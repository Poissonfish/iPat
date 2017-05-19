# Input arguments
args = commandArgs(trailingOnly=TRUE)
GM.path = args[1]
GD.path = args[2]
Y.path = args[3]
C.path = args[4]
C.inher = as.numeric(args[5])
method.bin= args[6] #"optimum"
maxLoop= as.numeric(args[7])
MAF.calculate= as.logical(args[8])
maf.threshold= as.numeric(args[9])
wd = args[10]
lib = args[11]
format = args[12]
arg_length = 12
# GM.path="NULL"
# GD.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.hmp"
# Y.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.txt"
# C.path = "NULL"
# C.inher = "NULL"%>%as.numeric
# method.bin = "optimum"
# maxLoop=as.numeric("10")
# MAF.calculate = as.logical("TRUE")
# maf.threshold=as.numeric("0.05")
# wd="/Users/Poissonfish/Desktop/test/farm"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# format = "Hapmap"
# args = c(1,2,3,4,5,6,7,8,9,0,1,2,3, 1) 
# arg_length = 12

tryCatch({
  # Load libraries
  setwd(lib)
  list.of.packages <- c("bigmemory", "biganalytics", "data.table", "magrittr", "MASS", "gplots", "compiler", "scatterplot3d", "R.utils", "ape")
  new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(bigmemory)
  library(biganalytics)
  library(compiler) 
  library(data.table)
  library(magrittr)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(scatterplot3d)
  library(R.utils)
  library(ape)
  source("./Function_EMMA.R")
  source("./Function_GAPIT.R")
  source("./Function_FarmCPU.R")
  source("./Function_LDRemove.R")

  setwd(wd)
  # Subset Phenotype
  Y = read.table(Y.path, head=TRUE)
  trait = c()
  if(length(args) > arg_length){
    for (i in (arg_length+1):length(args)){
      trait = c(trait, as.numeric(args[i])) 
    }
    Y = Y[,c(1,trait+1)]
  }
  trait_names = names(Y)[-1]

  # Format-free
  OS.Windows = FALSE
  switch(Sys.info()[['sysname']],
    Windows= {OS.Windows = TRUE}, # Windows
    Linux  = { }, # Linux
    Darwin = { }) # MacOS
  switch(format, 
    Hapmap = {if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
              hmp = substring(GD.path, 1, nchar(GD.path)-4)
              sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
              sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
              GD = read.table(sprintf("%s.dat", hmp)) %>% t() %>% data.frame(Y[,1], .)
              GM = read.table(sprintf("%s.map", hmp), head = TRUE)}, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(GD.path, 1, nchar(GD.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)
            GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
    PLink_ASCII = {

    },
    PLink_Binary = {

    }, {
      # Numeric (Default)
      if(GM.path=="NULL"){GM=NULL}else{GM=read.table(GM.path, head=TRUE)}
      if(GD.path=="NULL"){GD=NULL}else{GD=read.table(GD.path, head=TRUE)}
    }
  )

  # Covariate
  if(C.path=="NULL"){C=NULL}else{C=read.table(C.path, head=TRUE)}
  if(is.na(C.inher)) C.inher = NULL else C.inher = C.inher

  # FarmCPU
  for (i in 1:length(trait_names)){
    x=FarmCPU(
      Y = Y[,c(1,1+i)],
      GM = GM,
      GD = GD,
      CV = C,
      method.bin= method.bin,
      bin.size= c(5e5,5e6,5e7), # Default set of bin.size 
      bin.selection= seq(10,100,10), # Default set of bin.selection
      maxLoop = maxLoop,
      MAF.calculate=MAF.calculate,
      maf.threshold=maf.threshold,
      memo= trait_names[i])

    # Sorted by P-value
    SNP_p = data.frame(name = x$GWAS$rs, p = x$GWAS$P.value)
    SNP_p = SNP_p[order(SNP_p$p),]
    # Select associated SNPs
    sig = SNP_p$p < (.05)/nrow(SNP_p) #length(SNPname)
    GD_sig = GD[,SNP_p$name[sig]] 
    # Build covariates with associated SNPs
    if(is.null(ncol(GD_sig))){ # only 1 QTN detected
      marker = as.character(SNP_p$name[sig]) 
      if(is.null(ncol(C))) CV = data.frame(Y[,1], marker = GD_sig) else CV = cbind(Y[,1], marker = GD_sig, C)
      if(length(C.inher)==0) C.inher = NULL else C.inher = C.inher + 1
    }else if(ncol(GD_sig)==0){  # No QTN detected
      if(is.null(ncol(C))) CV = NULL else CV = cbind(Y[,1], C)
    }else{
      # LD Remove
      LD_remain = Blink.LDRemove(GD_sig, .7, 1:sum(sig), orientation = "col")
      GD_sig = GD_sig[,LD_remain] 
      # Check number
      if(is.null(ncol(C)) && nrow(Y) < ncol(GD_sig)){ # no C provided  
        redundant = ncol(GD_sig) - nrow(Y)
        GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
      }else if(!is.null(ncol(C)) && nrow(Y) < (ncol(GD_sig) + ncol(C))){ # C provided
        redundant = ncol(GD_sig) + ncol(C) - nrow(Y)
        GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
      }
      if(is.null(ncol(C))) CV = cbind(Y[,1],GD_sig) else CV = cbind(Y[,1], GD_sig, C)
      if(length(C.inher)==0) C.inher = NULL else C.inher = C.inher + ncol(GD_sig)
    }

    # GAPIT do prediction
    pred <- GAPIT(
      Y = Y[,c(1,1+i)],
      GM = GM,
      GD = GD,       
      PCA.total=3,
      CV = CV,
      CV.Inheritance = C.inher,
      group.from=10000,
      group.to=10000,
      group.by=10,
      SNP.test=FALSE,
      memo= trait_names[i]
    )
  }
  print(warnings())
},error = function(e){
    stop(e)
})