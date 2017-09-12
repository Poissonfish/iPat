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
# Method specific args
  model = args[16]
  ki.c = args[17]
  ki.g = args[18]
  snp.fraction = as.numeric(args[19])
  file.fragment = as.numeric(args[20])
  model.s = as.logical(args[21])
# Load libraries
  cat("=== GAPIT ===\n")
  cat("   Loading libraries ...")
  setwd(lib)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(compiler) #required for cmpfun
  library(scatterplot3d)
  library(R.utils)
  library(data.table)
  library(magrittr)
  source("./Function_iPat.R")
  source("./Function_FarmCPU.R")
  source("./Function_GAPIT.R") 
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
  if(is.character(Y[,1])) Y = apply(Y, 2, as.numeric) 
  cat("Done\n")
  # Genptype
    cat("   Loading genotype ...")
    GD = fread(GD.path) %>% as.data.frame()
    GM = fread(GM.path) %>% as.data.frame()
    if(is.character(GD[,1])) GD = GD[,-1]
    if(is.character(GD[,1])) GD = apply(GD, 2, as.numeric)
    cat("Done\n")
  # QC
    cat("   Quality control ...")
    # Missing rate
    if(!is.na(ms)){
      MS = is.na(GD) %>% apply(2, function(x) sum(x)/length(x))
      GD = GD[, MS <= ms]
      GM = GM[MS <= ms, ]}
    # MAF
    if(!is.na(maf)){
      GD_temp = GD
      GD_temp[is.na(GD)] = 1
      MAF = apply(GD_temp, 2, mean) %>% 
            as.matrix() %>% 
            apply(1, function(x) min(1 - x/2, x/2))
      GD = GD[, MAF >= maf]
      GM = GM[MAF >= maf, ]}
    # No NA allowed in GAPIT
      GD[is.na(GD)] = 1
    cat("Done\n")
  # Covariate
    if(C.path != "NA"){
      cat("   Loading covariates ...")
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
      index.C = which(C.model.name == "Selected")
      # 0 c
      if(length(index.C) == 0){
        C = NULL
      # 1 c
      }else if(length(index.C) == 1){
        name = names(C.data)[index.C]
        C = data.frame(taxa = taxa, c = C.data[, index.C])
        names(C) = name
      # More than 1 c
      }else{
        C = C.data[, index.C]
        C = data.frame(taxa = taxa, C)
      }
      cat("Done\n")
    }else{
      C = NULL
    }
  # Kinship
    if(K.path == "NA"){
      K = NULL
    }else{
      cat("   Loading Kinship ...")
      K = fread(K.path) %>% as.data.frame()
      if(is.character(K[,1])) K = K[,-1]
      K = data.frame(taxa = taxa, K)
      cat("Done\n")
    }
    #if(is.na(C.inher)) C.inher = NULL else C.inher = C.inher

  # Model
  switch(model, 
    GLM = {
      g.from = 1
      g.to = 1
      g.by = 10},
    MLM = {
      g.from = 10000000
      g.to = 10000000
      g.by = 10},
    CMLM = {
      g.from = 1
      g.to = 10000000
      g.by = 10}
  )
  # GAPIT
    for (i in 1:length(trait.names)){   
      x = GAPIT(
            Y = data.frame(taxa, Y[,i]),
            GM = GM,
            GD = data.frame(taxa, GD),
            KI = K,
            CV = C,
            kinship.cluster = ki.c,
            kinship.group = ki.g,
            group.from = g.from,
            group.to = g.to,
            group.by = g.by,
            Model.selection = model.s,
            SNP.fraction = snp.fraction,
            memo = sprintf("%s_%s", project, trait.names[i]))
      write.table(x = data.frame(SNP = x$GWAS$SNP, P.value = x$GWAS$P.value),
                file = sprintf("%s_%s_GWAS.txt", project, trait.names[i]),
                quote = F, row.names = F, sep = "\t")
    }
  print(warnings())
}, error = function(e){
    stop(e)
})