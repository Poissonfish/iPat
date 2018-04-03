tryCatch({
# Library
  cat("=== PLINK ===\n")
  cat("   Loading libraries ...")
  library(magrittr)
  library(bigmemory)
  library(biganalytics)
  library(compiler) #this library is already installed in R
  library(data.table)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(scatterplot3d)
  library(R.utils)
  source("http://zzlab.net/iPat/Function_iPat.R")
  source("http://zzlab.net/GAPIT/gapit_functions.txt")
  cat("Done\n")

# Input arguments
  arg = commandArgs(trailingOnly=TRUE)
  for (i in 1 : length(arg)) {
    switch (arg[i],
      "-wd" = {
        i = i + 1
        wd = arg[i]
        setwd(wd)
      },
      "-project" = {
        i = i + 1
        project = arg[i]
      },
      "-pSelect" = {
        i = i + 1
        selectP = arg[i]
      },
      "-maf" = {
        i = i + 1
        maf = as.numeric(arg[i])
      },
      "-ms" = {
        i = i + 1
        ms = as.numeric(arg[i])
      },
      "-format" = {
        i = i + 1
        format = arg[i]
        if (format == "PLINKBIN")
          binary = TRUE
        else
          binary = FALSE
      },
      "-cSelect" = {
        i = i + 1
        selectC = ifelse(grepl("NA", arg[i]),
          NA, arg[i])
      },
      "-phenotype" = {
        cat("   Loading phenotype ...")
        i = i + 1
        if (grepl("/NA", arg[i]))
          Y.path = "NA"
        else
          Y.path = arg[i]
        cat("Done\n")
      },
      "-genotype" = {
        cat("   Loading genotype ...")
        i = i + 1
        GD.path = arg[i]
        cat("Done\n")
      },
      "-map" = {
        cat("   Loading map ...")
        i = i + 1
        GM.path = arg[i]
        cat("Done\n")
      },
      "-cov" = {
        cat("   Checking covariates ...")
        i = i + 1
        if (grepl("/NA", arg[i]))
          C.path = "NA"
        else
          C.path = arg[i]
        cat("Done\n")
      },
      "-kin" = {
        cat("   Checking kinship ...")
        i = i + 1
        if (grepl("/NA", arg[i]))
          kin = NULL
        else
          kin = fread(arg[i])
        cat("Done\n")
      },
      "-fam" = {
        i = i + 1
        FAM.path = arg[i]
      },
      "-bim" = {
        i = i + 1
        BIM.path = arg[i]
      },
      "-arg" = {
        i = i + 1
        ci = as.numeric(arg[i])
        i = i + 1
        model = arg[i]
        i = i + 1
        pathPLINK = arg[i]
      }
    )
  }
  # Subset Phenotype
    cat("   Loading phenotype ...")
    if(Y.path == "NA"){
      if(binary){
        Y.data = fread(FAM.path, na.strings = c("NA", "NaN")) %>% as.data.frame
        Y.path = paste0(FAM.path %>% substr(1, nchar(.) - 3), "plinktrait")
        Y = data.frame(FID = Y.data[,1], SID = Y.data[,2], trait = Y.data[,6])
        write.table(x = Y,
                    file = Y.path, quote = F, row.names = F, sep = '\t')
      }else{
        Y.data = fread(GD.path, na.strings = c("NA", "NaN")) %>% as.data.frame
        Y.path = paste0(GD.path %>% substr(1, nchar(.) - 3), "plinktrait")
        write.table(x = data.frame(FID = Y.data[,1], SID = Y.data[,2], trait = Y.data[,6]),
                    file = Y.path, quote = F, row.names = F, sep = '\t')
      }
      trait.name = "trait"
      trait_count = 1
      suffix = ".assoc"
    }else{
      Y.data = fread(Y.path, na.strings = c("NA", "NaN")) %>% as.data.frame
      if(toupper(names(Y.data)[1]) != "FID") {
        trait.name = Y.data[, -1] %>% names()
        taxa = Y.data[,1]
        Y.data = data.frame(FID = taxa, SID = taxa, Y.data[,-1])
        Y.path = paste0(Y.path %>% substr(1, nchar(.) - 3), "plinktrait")
        write.table(x = Y.data, file = Y.path, quote = F, row.names = F, sep = '\t')
      }else{
        trait.name = Y.data[, -c(1, 2)] %>% names()
      }
      Y = Y.data
      trait_count = (names(Y.data) %>% length()) - 2
      suffix = ".qassoc"
    }
    cat("Done\n")
  # Covariate
    if(C.path != "NA"){
      cat("   Loading covariates ...")
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
      index.C = which(C.model.name == "Selected")
      C.name = names(C.data)[index.C]
      cat("Done\n")
    }else{
      C.name = character()
    }
  # PLINK
  ## Basic
  if(model == "GLM"){
    method = "--assoc"
  }else{
    method = "--logistic"
    suffix = paste0(suffix, ".logistic")
  }
  if(binary){
    basic = sprintf("%s --bed %s --bim %s --fam %s %s --allow-no-sex --adjust -ci %s --pheno %s --all-pheno -out %s",
                    pathPLINK, GD.path, BIM.path, FAM.path, method, ci, Y.path, file.path(wd, project))
  }else{
    basic = sprintf("%s --ped %s --map %s %s --allow-no-sex --adjust -ci %s --pheno %s --all-pheno -out %s",
                    pathPLINK, GD.path, GM.path, method, ci, Y.path, file.path(wd, project))
  }
  ## QC
  if(!is.na(ms)) MS = sprintf("--geno %s", ms) else MS = character()
  if(!is.na(maf)) MAF = sprintf("--maf %s", maf) else MAF = character()
  ## COV and running BLINK
  if(length(C.name) > 0){
    cov = sprintf("--covar %s --covar-name %s", C.path, paste(C.name, collapse = ", "))
    paste(basic, MS, MAF, cov) %>% system()
  }else{
    paste(basic, MS, MAF) %>% system()
  }
  #Plotting
  setwd(wd)
  for (t in 1:trait_count){
    #Loading data
    cat(sprintf("   Plotting trait %s ...", t))
    data = fread(paste0(project, ".P", t, suffix),header=T)
    data = na.omit(data) %>% (function(x){x[order(x$CHR,x$BP),c("CHR", "SNP", "BP", "P")]}) %>% as.data.frame
    iPat.Manhattan(GI.MP = data[,c(1,3,4)], filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    iPat.QQ(data$P, filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    iPat.Phenotype.View(myY = data.frame(Y[,1], Y[, 2 + t]), filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    write.table(x = data.frame(SNP = data$SNP, Chromosom = data$CHR, Position = data$BP, P.value = data$P),
                file = sprintf("iPat_%s_%s_GWAS.txt", project, trait.name[t]),
                quote = F, row.names = F, sep = "\t")
    cat("Done\n")
  }
}, error = function(e){
  stop(e)
})
