# Input arguments
  args = commandArgs(trailingOnly=TRUE)
# Common args
  project = args[1]
  wd = args[2]
  lib = args[3]
  #format = args[4]
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
  file.fragment = as.numeric(args[17])
  model.s = as.logical(args[18])
  gwas.assist = as.logical(args[19])
  cutoff = as.numeric(args[20])

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
      C.final = NULL
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
  iPat.Genotype.View(myGD = data.frame(taxa, GD), filename = sprintf("iPat_%s", project))
  for (i in 1:length(trait.names)){   
  # GWAS-assist
    if(gwas.assist){
      cat("   Loading QTNs information ...")
      ## Read GWAS result
      gwas = fread(sprintf("iPat_%s_%s_GWAS.txt", project, trait.names[i]))
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
      ### 0 QTNs
      if(length(index.sig) == 0){
        C.gwas = NULL
      ### 1 QTNs
      }else if(length(index.sig) == 1){
        C.gwas = data.frame(m = GD[,index.sig])
       ### 1+ QTNs
      }else if(length(index.sig) > 1){
        C.gwas = GD[,index.sig]
        ## LD Remove
        LD_remain = Blink.LDRemove(C.gwas, .7, index.sig, orientation = "col")
        C.gwas = C.gwas[ ,LD_remain] 
      }
  ## Prevent c > n
      if(is.null(C)) index.C = NULL
      if(length(Y[ ,i]) < length(index.C) + length(index.sig)){
        diff = length(index.C) + ncol(C.gwas) - length(Y[,i])
        if(is.null(C))
          C.final = data.frame(taxa = taxa, C.gwas[ ,1 : (length(index.sig) - diff)])
        else
          C.final = data.frame(C, C.gwas[ , 1 : (length(index.sig) - diff)])
      }else{
        if(is.null(C)){
          if(is.null(C.gwas)) {
            C.final = NULL
          }else{
            C.final = data.frame(taxa = taxa, C.gwas)
          }
        }else{
          if(!is.null(C.gwas)) {
            C.final = data.frame(C, C.gwas)
          }
        }
      } 
      cat("Done\n")
    }
  # GAPIT
      x = GAPIT(
        Y = data.frame(taxa, Y[,i]),
        GM = GM,
        GD = data.frame(taxa, GD),
        KI = K,
        CV = C.final,
        #CV.Inheritance = C.inher,
        #PCA.total = PCA,
        group.from = 10000,
        group.to = 10000,
        group.by = 10,
        Model.selection = model.s,
        SNP.fraction = snp.fraction,
        SNP.test=FALSE,
        memo = sprintf("%s_%s", project, trait.names[i]))
      iPat.Phenotype.View(myY = data.frame(taxa, Y[,i]), filename = sprintf("iPat_%s_%s", project, trait.names[i]))
      write.table(x = data.frame(taxa = x$Pred$Taxa, Pred = x$Pred$Prediction, PEV = x$Pred$PEV),
            file = sprintf("iPat_%s_%s_EBV.txt", project, trait.names[i]),
            quote = F, row.names = F, sep = "\t")
      pdf(sprintf("iPat_%s_%s_GEBV_value.pdf", project, trait.names[i]), width = 5, height = 5)
      plot(Y[,i], x$Pred$Prediction, main = "Phenotype v.s. GEBV")
      dev.off()
      pdf(sprintf("iPat_%s_%s_GEBV_PEV.pdf", project, trait.names[i]), width = 5, height = 5)
      plot(Y[,i], x$Pred$PEV, main = "Phenotype v.s. PEV")
      dev.off()
      pdf(sprintf("iPat_%s_%s_GEBV_hist.pdf", project, trait.names[i]), width = 5, height = 5)
      hist(x$Pred$Prediction, main = "Distribution of GEBV")
      dev.off()
  }
  print(warnings())
}, error = function(e){
    stop(e)
})