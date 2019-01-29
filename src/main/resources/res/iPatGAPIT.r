tryCatch({
# Library
  cat("=== GAPIT ===\n")
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
    # rm(list=ls())
    # arg = c("-arg", "MLM",
    #         "-wd", "/Users/jameschen/Desktop/Test/iPatDEMO",
    #         "-project", "GAPIT",
    #         "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.txt",
    #         "-pSelect", "y75sepy25sep",
    #         # "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/data.txt",
    #         # "-pSelect", "EarHTsepEarDiasep",
    #         "-cov", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.cov",
    #         "-cSelect", "C1sep",
    #         "-genotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.dat",
    #         "-kin", "NA",
    #         "-map", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.map")
    # trait = dataP$name[1]
    # X = finalG
    # Y = finalP
    # # C = finalC
    # C = NULL
    # iter = 1
    # fold = 1
    # K = finalK
    # YTemp = yTemp
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
      "-phenotype" = {
        cat("   Loading phenotype ...")
        i = i + 1
        rawPhenotype = fread(arg[i])
        taxa = rawPhenotype[ ,1]
        sizeN = nrow(rawPhenotype)
        cat("Done\n")
      },
      "-pSelect" = {
        i = i + 1
        selectP = arg[i]
      },
      "-genotype" = {
        cat("   Loading genotype ...")
        i = i + 1
        genotype = fread(arg[i])
        # If have taxa column
        if (is.character(rawGenotype[[1]]))
          rawGenotype = rawGenotype[ ,-1]
        cat("Done\n")
      },
      "-map" = {
        cat("   Loading map ...")
        i = i + 1
        if (grepl("NA", arg[i])) {
          rawMap = NULL
        } else {
          rawMap = fread(arg[i])
        }
        cat("Done\n")
      },
      "-cov" = {
        cat("   Checking covariates ...")
        i = i + 1
        if (grepl("NA", arg[i])) {
          rawCov = NULL
        } else {
          rawCov = fread(arg[i])
        }
        # If have taxa column
        if (is.character(rawCov[[1]]))
          rawCov = rawCov[ ,-1]
        cat("Done\n")
      },
      "-cSelect" = {
        i = i + 1
        selectC = ifelse(grepl("NA", arg[i]),
          NA, arg[i])
      },
      "-kin" = {
        cat("   Checking kinship ...")
        i = i + 1
        if (grepl("NA", arg[i])) {
          rawKin = NULL
        } else {
          rawKin = fread(arg[i]) %>% as.data.frame()
        }
        # If have taxa column
        if (is.character(rawKin[[1]]))
          rawKin = rawKin[ ,-1]
        cat("Done\n")
      },
      "-arg" = {
        i = i + 1
        model = arg[i]
        i = i + 1
        nPC = arg[i]
      }
    )
  }

# Subset Phenotype
  cat("   Subsetting phenotype ...")
  dataP = getSelected(rawPhenotype, selectP)
  cat("Done\n")

# Subset Covariates
  cat("   Subsetting covariates ...")
  dataC = getSelected(rawCov, selectC)
  cat("Done\n")

# GAPIT
  for (trait in nameTraits) {
    x = GAPIT(
          Y = data.frame(taxa, dataP$data[[trait]]),
          GM = data.frame(rawMap),
          GD = data.frame(taxa, rawGenotype),
          KI = rawKin,
          CV = data.frame(taxa, dataC$data),
          PCA.total = nPC,
          model = model,
          memo = sprintf("%s_%s.txt", project, trait))
    write.table(x = data.frame(SNP = x$GWAS$SNP, P.value = x$GWAS$P.value),
              file = sprintf("%s_%s_GWAS.txt", project, trait),
              quote = F, row.names = F, sep = "\t")
  }
  print(warnings())
}, error = function(e) {
  stop(e)
})
