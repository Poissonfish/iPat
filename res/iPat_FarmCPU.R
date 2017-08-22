# Input arguments
  args = commandArgs(trailingOnly=TRUE)
# Common args
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
  print("   Loading libraries ...")
  setwd(lib) 
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
    if(toupper(names(Y.data)[1]) == "FID") {Y.data = Y.data[,-1]}
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
    # Genptype
    cat("   Loading genotype ...")
    GD = fread(GD.path) %>% as.data.frame()
    GM = fread(GM.path) %>% as.data.frame()
    if(is.character(GD[,1])) GD = GD[,-1]
    cat("Done\n")
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

project="Project_2"
wd="C:\\Users\\Poissonfish"
lib="C:\\Users\\Poissonfish\\git\\iPat\\res"
format="Hapmap"
ms=as.numeric("No_threshold")
maf=as.numeric("0.05")
Y.path="C:\\Users\\Poissonfish\\Desktop\\demo_data\\Hapmap\\data.txt"
Y.index="SelectedsepSelectedsepSelectedsep"
GD.path="C:\\Users\\Poissonfish\\Desktop\\demo_data\\Hapmap\\data_recode.dat"
GM.path="C:\\Users\\Poissonfish\\Desktop\\demo_data\\Hapmap\\data_recode.nmap"
C.path="NA"
C.index="NA"
K.path="NA"
FAM.path="NA"
BIM.path="NA"
 method.bin = "static"
  maxLoop = 10


