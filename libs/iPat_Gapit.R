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
  source("./Function_EMMA.R")
  source("./Function_FarmCPU.R")
  source("./Function_GAPIT.R")

tryCatch({  
  setwd(wd)
  # Subset Phenotype
  Y.data = fread(Y.path) %>% as.data.frame
  subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .) %>% as.numeric
  Y = Y.data[, subset+1]

  # Assign Variables
  taxa = Y.data[,1]
  trait.names = names(Y) 

  # Format free
  OS.Windows = FALSE
  switch(Sys.info()[['sysname']],
    Windows= {OS.Windows = TRUE}, # Windows
    Linux  = { }, # Linux
    Darwin = { }) # MacOS
  switch(format, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(GD.path, 1, nchar(GD.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            G = NULL
            GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)
            GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
    PLink_Binary = {

    }, {
      # Hapmap or Numeric (Default)
      if(G.path=="NA"){G=NULL}else{G=read.delim(G.path, head=FALSE)}
      if(GM.path=="NA"){GM=NULL}else{GM=read.table(GM.path, head=TRUE)}
      if(GD.path=="NA"){GD=NULL}else{GD=read.table(GD.path, head=TRUE)}
    }
  )

  # Covariates
  if(C.path == "NA"){C = NULL}else{C = fread(C.path) %>% as.data.frame()}
  if(K.path == "NA"){K = NULL}else{K = fread(K.path) %>% as.data.frame()}
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
    for (i in 1:length(trait_names)){   
        x=GAPIT(
          Y = Y[,c(1,1+i)],
          G = G,
          GM = GM,
          GD = GD,
          KI = K,
          CV = C,
          #CV.Inheritance = C.inher,
          #PCA.total = PCA,
          kinship.cluster = ki.c,
          kinship.group = ki.g,
          group.from = g.from,
          group.to = g.to,
          group.by = g.by,
          Model.selection = model.s,
          SNP.fraction = snp.fraction,
          file.fragment = file.fragment,
          memo= sprintf("GAPIT_%s_%s_", project, trait_names[i]))
    }
  print(warnings())
}, error = function(e){
    stop(e)
})

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