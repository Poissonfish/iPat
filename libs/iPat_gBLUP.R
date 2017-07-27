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
  snp.fraction = as.numeric(args[16])
  model.s = as.logical(args[17])
  gwas.assist = as.logical(args[18])
  cutoff = as.numeric(args[19])

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
  source("./Function_EMMA.R")
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
  }else{
    Y = Y.data[, index.trait + 1]
  }
  # Assign Variables
  taxa = Y.data[,1]
  trait.names = names(Y) 
  cat("Done\n")
  # Format free
  cat("   Loading genotype and do conversion if need ...")
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
              GD = fread(sprintf("%s.dat", hmp)) %>% t() %>% as.data.frame()
              GM = fread(sprintf("%s.map", hmp)) %>% t() %>% as.data.frame()}, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(GD.path, 1, nchar(GD.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)
            GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
    PLink_Binary = {

    }, {
      # Numeric (Default)
      GD = fread(GD.path) %>% as.data.frame()
      GM = fread(GM.path) %>% as.data.frame()
    }
  )
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
      MAF = apply(GD, 2, mean) %>% 
            as.matrix() %>% 
            apply(1, function(x) min(1 - x/2, x/2))
      GD = GD[, MAF >= maf]
      GM = GM[MAF >= maf, ]}
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
        C = data.frame(c = C.data[, index.C])
        names(C) = name
      # More than 1 c
      }else{
        C = C.data[, index.C]
      }
      cat("Done\n")
    }else{
      C = NULL
    }
  # GWAS-assist
    if(gwas.assist){
      cat("   Loading QTNs information ...")
      ## Read GWAS result
      gwas = fread(sprintf("%s_%s_GWAS.txt", project, trait.names[i]))
      ## Merge GM and p-value
      names(GM)[1] = "SNP"
      map_gwas = data.frame(GM, P.value = gwas$P.value[match(GM$SNP, gwas$SNP)])
      map_gwas$P.value[is.na(map_gwas$P.value)] = 1
      ## Order p-value
      snp_order = order(map_gwas$P.value) 
      map_gwas = map_gwas[snp_order, ]
      GD = GD[ ,snp_order]
      ## Find QTNs
      index.sig = which(map_gwas$P.value < (cutoff/nrow(gwas)))
      ## Generate a dataframe by number of QTNs
      ### 1 QTNs
      if(length(index.sig) == 1){
        C.gwas = data.frame(m = GD[,index.sig])
       ### 1+ QTNs
      }else{
        C.gwas = GD[,index.sig]
        ## LD Remove
        LD_remain = Blink.LDRemove(C.gwas, .7, index.sig, orientation = "col")
        C.gwas = C.gwas[ ,LD_remain] 
      }
      cat("Done\n")}
  ## Prevent c > n
    if(length(Y[ ,i]) < length(index.C) + ncol(C.gwas)){
      diff = length(index.C) + ncol(C.gwas) - length(Y[,i])
      if(is.null(C))
        C = data.frame(C.gwas[ ,1 : (ncol(C.gwas) - diff)])
      else
        C = data.frame(C, C.gwas[ ,1 : (ncol(C.gwas) - diff)])
    }else{
      if(is.null(C))
        C = data.frame(C.gwas)
      else
        C = data.frame(C, C.gwas)
    }
  
  #if(is.na(C.inher)) C.inher = NULL else C.inher = C.inher
  # GAPIT
    for (i in 1:length(trait.names)){   
      x = GAPIT(
        Y = data.frame(taxa, Y[,i]),
        GM = GM,
        GD = data.frame(taxa, GD),
        KI = K,
        CV = data.frame(taxa, C),
        #CV.Inheritance = C.inher,
        #PCA.total = PCA,
        group.from = 10000,
        group.to = 10000,
        group.by = 10,
        Model.selection = model.s,
        SNP.fraction = snp.fraction,
        SNP.test=FALSE,
        memo = sprintf("%s_%s", project, trait.names[i]))
    }
  print(warnings())
}, error = function(e){
    stop(e)
})