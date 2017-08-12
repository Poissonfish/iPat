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
  source("./Function_iPat.R")
  source("./Function_FarmCPU.R")
  source("./Function_GAPIT.R") 
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
  }else
    Y = Y.data[, index.trait + 1]
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
            #CV.Inheritance = C.inher,
            #PCA.total = 3,
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

# #


project="Project_1"
wd="/Users/Poissonfish/Desktop/test/gapit"
lib="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/iPat.app/Contents/MacOS/libs/"
format="Hapmap"
ms=as.numeric("No_threshold")
maf=as.numeric("0.05")
Y.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Hapmap/data.txt"
Y.index="SelectedsepSelectedsepSelectedsep"
GD.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Hapmap/data_recode.dat"
GM.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Hapmap/data_recode.nmap"
C.path="NA"
C.index="NA"
K.path="NA"
FAM.path="NA"
BIM.path="NA"
model = "GLM"
ki.c = "average"
ki.g = "Mean"
snp.fraction = 1
file.fragment = NULL
model.s = as.logical("FALSE") 


# if(multi){
#   nT = ncol(Y.file) - 1
#   # Imputation by mean
#   for(i in 2:ncol(Y.file)){
#     mean = mean(Y.file[,i] %>% na.omit())
#     na_index = Y.file[,i] %>% is.na()
#     Y.file[na_index,i] = mean 
#   }
#   # PCA
#   Y.PCA = prcomp(Y.file[,-1])
#   Y.eigvec = Y.PCA$rotation
#   Y.eigval = (Y.PCA$sdev)^2
#   cum.var = cumsum(Y.eigval)/sum(Y.eigval)
#   pca.index = 1 : (which(cum.var > .9) %>% min)
#   Y.tran = Y.PCA$x[,pca.index] %>% as.matrix
#   Y = data.frame(Y.file[,1],Y.tran)
#   npc = ncol(Y) - 1
#   m = ifelse(is.null(G), ncol(GD) - 1, nrow(G) - 1)
#   array.effect = matrix(ncol = npc, nrow = m)
#   # GAPIT
#   for (i in 1 : npc){
#     x=GAPIT(
#       Y = Y[,c(1,1+i)],
#       G = G,
#       GM = GM,
#       GD = GD,
#       KI = K,
#       SNP.test = SNP.test,
#       CV = C,
#       CV.Inheritance = C.inher,
#       PCA.total = PCA,
#       kinship.cluster = ki.c,
#       kinship.group = ki.g,
#       group.from = g.from,
#       group.to = g.to,
#       group.by = g.by,
#       Model.selection = model.s,
#       SNP.fraction = snp.fraction,
#       file.fragment = file.fragment,
#       file.output = F
#     )
#     u.effect = x$effect.snp
#     u.effect[is.na(u.effect)] = 0
#     array.effect[,i] = u.effect
#   }
#   # Real effects recover
#   real.effect = array.effect %*% t(Y.eigvec[, pca.index])
#   for (i in 1 : nT){

#   }
# # Y.recover = (t(Y.tran %*% t(Y.eigvec)) + PCA$center) %>% t()
# }