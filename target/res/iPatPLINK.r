tryCatch({
# Library
  cat("=== PLINK ===\n")
  cat("   Loading libraries ...")
  library(magrittr)
  library(data.table)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(compiler) #required for cmpfun
  library(scatterplot3d)
  library(R.utils)
  source("http://zzlab.net/iPat/Function_iPat.R")
  source("http://zzlab.net/GAPIT/gapit_functions.txt")
  cat("Done\n")

# Input arguments
  arg = commandArgs(trailingOnly=TRUE)
  # ======= Test Code ====== #
  # arg = c("-arg", "0.95", "GLM", "/Users/jameschen/IdeaProjects/iPat/target/lib/plink",
  #         "-wd", "/Users/jameschen/Desktop/Test/iPatDEMO",
  #         "-project", "PLINK",
  #         "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.txt",
  #         "-pSelect", "y75sepy25sep",
  #         "-maf", "0.05",
  #         "-ms", "0.20",
  #         # "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/data.txt",
  #         # "-pSelect", "EarHTsepEarDiasep",
  #         # "-cov", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.cov",
  #         # "-cSelect", "C1sep",
  #         "-cov", "NA",
  #         "-cSelect", "NA",
  #         "-genotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.ped",
  #         "-kin", "NA",
  #         "-map", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.map")
  # arg = c(
  # "-arg", "0.95", "GLM", "/Users/jameschen/Dropbox/iPat/wrapper/jar/res/plink",
  # "-cSelect", "NA", "-wd", "/Users/jameschen/53w5", "-project", "testttt",
  # "-phenotype", "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt",
  # "-pSelect", "y25sepy50sepy75sep", "-cov", "NA", "-kin", "NA",
  # "-genotype", "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.ped",
  # "-map", "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.map")

  # ======= Test Code ====== #
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
      "-cSelect" = {
        i = i + 1
        selectC = ifelse(grepl("NA", arg[i]),
          NA, arg[i])
      },
      "-phenotype" = {
        cat("   Loading phenotype ...")
        i = i + 1
        if (grepl("NA", arg[i]))
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
        if (grepl("NA", arg[i]))
          C.path = "NA"
        else
          C.path = arg[i]
        cat("Done\n")
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
    # If no phenotype provided, which would be in
    if(Y.path == "NA"){
      Y.data = fread(GD.path, na.strings = c("NA", "NaN")) %>% as.data.frame()
      Y.path = paste0(GD.path %>% substr(1, nchar(.) - 3), "_trait.txt")
      write.table(x = data.frame(FID = Y.data[,1], IID = Y.data[,2], trait = Y.data[,6]),
                  file = Y.path, quote = F, row.names = F, sep = '\t')
      trait.name = "trait"
      trait_count = 1
      suffix = ".assoc"
    # If phenotype provided
    }else{
      Y.data = fread(Y.path, na.strings = c("NA", "NaN"))
      G.data = fread(GD.path, na.strings = c("NA", "NaN")) %>% as.data.frame()
      # wrong format for PLINK
      FID = G.data[,1]
      IID = G.data[,2]
      taxa = IID
      # get selected data
      trait.name = selectP %>% strsplit(split = "sep") %>% do.call(c, .)
      Y.data = data.frame(FID = FID, IID = IID, Y.data[ ,..trait.name])
      names(Y.data) = c("FID", "IID", trait.name)
      Y.path = paste0(Y.path %>% substr(1, nchar(.) - 4), "_trait.txt")
      trait_count = (names(Y.data) %>% length()) - 2
      suffix = ".qassoc"
    }
    cat("Done\n")
  # Covariate
    if(C.path != "NA"){
      cat("   Loading covariates ...")
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.name = selectC %>% strsplit(split = "sep") %>% do.call(c, .)
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

  basic = sprintf("%s --ped %s --map %s %s --allow-no-sex --adjust -ci %s --pheno %s --all-pheno --prune -out %s",
                  paste0('"', pathPLINK, '"'), paste0('"', GD.path, '"'),
                  paste0('"', GM.path, '"'),
                  method, ci,
                  paste0('"', Y.path, '"'),
                  paste0('"', file.path(wd, project), '"'))
  ## QC
  if(ms != 1) MS = sprintf("--geno %s", ms) else MS = character()
  if(maf != 0) MAF = sprintf("--maf %s", maf) else MAF = character()
  #Plotting
  setwd(wd)
  for (t in 1:trait_count){
    ## Rewrite Phenotype
    if(Y.path != "NA"){
      tCol = t + 2
      updateY = Y.data[,c(1:2, tCol)]
      updateY[is.na(updateY[,3]),3] = -9
      fwrite(x = updateY, file = Y.path, quote = F, row.names = F, sep = '\t')
    }
    ## COV and running PLINK
    if(length(C.name) > 0){
      cov = sprintf("--covar %s --covar-name %s", paste0('"', C.path, '"'), paste(C.name, collapse = ", "))
      paste(basic, MS, MAF, cov) %>% system(input = "notepad")
    }else{
      paste(basic, MS, MAF) %>% system(input = "notepad")
    }
    #Loading data
    cat(sprintf("   Plotting trait %s ...", t))
    dt_gwas = fread(paste0(project, ".", trait.name[t], suffix), header = T)
    names(dt_gwas) = c("Chromosome", "SNP", "Position", "NMISS", "effect", "SE", "R2", "T", "P.value")
    dt_gwas[,c(2, 1, 3, 9, 5)] %>% fwrite(file = sprintf("iPat_%s_%s_GWAS.txt", project, trait.name[t]), quote = F, row.names = F, sep = "\t")
    dt_out = dt_gwas[,c(1:3, 9)] %>% data.frame()
    names(dt_out) = c("CHR", "SNP", "BP", "P")
    iPat.Manhattan(GI.MP = dt_out[,-2], filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    iPat.QQ(dt_out$P, filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    iPat.Phenotype.View(myY = data.frame(taxa, updateY[,3]), filename = sprintf("iPat_%s_%s", project, trait.name[t]))
    cat("Done\n")
  }
  print(warnings())
}, error = function(e){
  stop(e)
})
