# Input arguments
  args = commandArgs(trailingOnly=TRUE)
## Common args
  project = args[1]
  wd = args[2]
  lib = args[3]
  format = args[4]
  ms = as.numeric(args[5])
  maf  = as.numeric(args[6])
  Y.path = args[7]
  Y.index = args[8]
  GD.path = args[9]
  GM.path  = args[10]
  C.path = args[11]
  C.index = args[12]
  K.path  = args[13]
  FAM.path  = args[14]
  BIM.path  = args[15]
## Method specific args
  method.bin = args[16] #"optimum"
  maxLoop = as.numeric(args[17])

# Load libraries
  cat("=== FarmCPU ===\n")
  cat("   Loading libraries ...")
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
  source("./Function_iPat.R")
  source("./Function_GAPIT.R")
  source("./Function_FarmCPU.R")
  cat("Done\n")

tryCatch({  
  setwd(wd)
   # Subset Phenotype
    cat("   Loading phenotype ...")
    Y.data = fread(Y.path) %>% as.data.frame
    subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .)
    index.trait = which(subset == "Selected") 
    if(length(index.trait) == 1){
      Y = data.frame(y = Y.data[, index.trait + 1])
      names(Y) = names(Y.data)[1 + index.trait] 
    }else{
      Y = Y.data[, index.trait + 1]
    }
  # Assign Variables
    taxa = Y.data[,1]
    trait.names = names(Y) 
    cat("Done\n")
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
        GD = read.table(sprintf("%s.dat", hmp)) %>% t() %>% data.frame(taxa, .)
        GM = read.table(sprintf("%s.map", hmp), head = TRUE)}, 
      VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
        vcf = substring(GD.path, 1, nchar(GD.path)-4)
        sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
        sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
        GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(taxa, .)
        GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
      PLink_Binary = {

      }, {
        # Numeric (Default)
        GM = fread(GM.path) %>% as.data.frame()
        GD = fread(GD.path) %>% as.data.frame()
      })
      if(is.character(GD[,1])) GD = GD[,-1]
  # QC
    cat("   Quality control ...")
    # Missing rate
    if(!is.na(ms)){
      MS = is.na(GD) %>% apply(2, function(x) sum(x)/length(x))
      GD = GD[, MS <= ms]
      GM = GM[MS <= ms, ]
    }
    # MAF
    MAF.calculate = ifelse(maf == "No threshold", FALSE, TRUE)
    cat("Done\n")
  # Covariate
    if(C.path != "NA"){
      cat("   Loading covariates ...")
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
      index.C = which(C.model.name == "Selected")
      if(length(index.C) == 1){
        name = names(C.data)[index.C]
        C = data.frame(c = C.data[, index.C])
        names(C) = name
      }else{
        C = C.data[, index.C]
      }
    cat("Done\n")
    }else{
      C = NULL
    }
  # FarmCPU
    for (i in 1:length(trait.names)){
      x = FarmCPU(
            Y = data.frame(taxa, Y[,i]),
            GM = GM,
            GD = data.frame(taxa, GD),
            CV = C,
            method.bin = method.bin,
            bin.size = c(5e5, 5e6, 5e7), # Default set of bin.size 
            bin.selection = seq(10, 100, 10), # Default set of bin.selection
            maxLoop = maxLoop,
            MAF.calculate = MAF.calculate,
            maf.threshold = maf,
            memo = sprintf("%s_%s", project, trait.names[i]))
      write.table(x = data.frame(SNP = x$GWAS$SNP, P.value = x$GWAS$P.value),
                  file = sprintf("%s_%s_GWAS.txt", project, trait.names[i]),
                  quote = F, row.names = F, sep = "\t")
    }
  print(warnings())
},error = function(e){
  stop(e)
})

  # C.index = "SelectedsepExcludedsepSelectedsep"
  # C.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/covariates.txt"

   # # Sorted by P-value
   #  SNP_p = data.frame(name = x$GWAS$SNP, p = x$GWAS$P.value)
   #  SNP_p = SNP_p[order(SNP_p$p),]
   #  # Select associated SNPs
   #  sig = SNP_p$p < (.05)/nrow(SNP_p) #length(SNPname)
   #  GD_sig = GD[,SNP_p$name[sig]] 
   #  # Build covariates with associated SNPs
   #  if(is.null(ncol(GD_sig))){ # only 1 QTN detected
   #    marker = as.character(SNP_p$name[sig]) 
   #    if(is.null(ncol(C))) CV = data.frame(Y[,1], marker = GD_sig) else CV = cbind(Y[,1], marker = GD_sig, C)
   #    if(length(C.inher)==0) C.inher = NULL else C.inher = C.inher + 1
   #  }else if(ncol(GD_sig)==0){  # No QTN detected
   #    if(is.null(ncol(C))) CV = NULL else CV = cbind(Y[,1], C)
   #  }else{
   #    # LD Remove
   #    LD_remain = Blink.LDRemove(GD_sig, .7, 1:sum(sig), orientation = "col")
   #    GD_sig = GD_sig[,LD_remain] 
   #    # Check number
   #    if(is.null(ncol(C)) && nrow(Y) < ncol(GD_sig)){ # no C provided  
   #      redundant = ncol(GD_sig) - nrow(Y)
   #      GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
   #    }else if(!is.null(ncol(C)) && nrow(Y) < (ncol(GD_sig) + ncol(C))){ # C provided
   #      redundant = ncol(GD_sig) + ncol(C) - nrow(Y)
   #      GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
   #    }
   #    if(is.null(ncol(C))) CV = cbind(Y[,1],GD_sig) else CV = cbind(Y[,1], GD_sig, C)
   #    if(length(C.inher)==0) C.inher = NULL else C.inher = C.inher + ncol(GD_sig)
   #  }

   #  # GAPIT do prediction
   #  pred <- GAPIT(
   #    Y = Y[,c(1,1+i)],
   #    GM = GM,
   #    GD = GD,       
   #    PCA.total=3,
   #    CV = CV,
   #    CV.Inheritance = C.inher,
   #    group.from=10000,
   #    group.to=10000,
   #    group.by=10,
   #    SNP.test=FALSE,
   #    memo= trait_names[i]
   #  )
   #  

# project = "Project_1" 
# wd = "/Users/Poissonfish/Desktop/test/farm"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# format = "Numeric" 
# ms = as.numeric("No threshold") 
# maf = as.numeric(0.05) 
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.txt" 
# Y.index = "SelectedsepExcludedsepSelectedsep" 
# GD.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.dat" 
# GM.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.map" 
# C.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/covariates.txt"
# C.index = "SelectedsepExcludedsepSelectedsep"
# K.path = "NA"
# FAM.path = "NA"
# BIM.path = "NA"
# method.bin = "static" 
# maxLoop = as.numeric(10) 