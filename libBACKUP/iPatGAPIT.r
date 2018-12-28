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
    # arg = c("-arg", "MLM", "3",
    #         "-wd", "/Users/jameschen/Desktop/Test/iPatDEMO",
    #         "-project", "GAPIT",
    #         "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.txt",
    #         "-pSelect", "y25sepy50sepy75sep",
    #         # "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/data.txt",
    #         # "-pSelect", "EarHTsepEarDiasep",
    #         "-cov", "NA",
    #         "-cSelect", "NA",
    #         # "-cov", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.cov",
    #         # "-cSelect", "C1sep",
    #         "-genotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.dat",
    #         "-kin", "NA",
    #         "-map", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.nmap")
    # # trait = dataP$name[1]
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
        rawGenotype = fread(arg[i])
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
          names(rawMap) = c("SNP", "Chromosome", "Position")
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
        nPC = as.numeric(arg[i])
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
  if (is.null(dataC$data)) {
    finalC = NULL
  } else {
    finalC = data.frame(taxa, datac$data)
  }
  cat("Done\n")

# GAPIT
  for (trait in dataP$name) {
    x = GAPIT(
          Y = data.frame(taxa, dataP$data[[trait]]),
          GM = data.frame(rawMap),
          GD = data.frame(taxa, rawGenotype),
          KI = rawKin,
          CV = finalC,
          PCA.total = nPC,
          file.output = F,
          model = model)
    dt_gwas = x$GWAS
    dt_gwas = merge(rawMap, dt_gwas[,-c(2, 3)], sort = F) %>% data.table(effect = x$mc)
    fwrite(x = dt_gwas, file = sprintf("iPat_%s_%s_GWAS.txt", project, trait), quote = F, row.names = F, sep = "\t")
    dt_out = dt_gwas[,c(2, 1, 3, 4)] %>% data.frame()
    names(dt_out) = c("CHR", "SNP", "BP", "P")
    iPat.Manhattan(GI.MP = dt_out[,-2], filename = sprintf("iPat_%s_%s", project, trait))
    iPat.QQ(dt_out$P, filename = sprintf("iPat_%s_%s", project, trait))
    iPat.Genotype.View(myGD = data.frame(taxa, rawGenotype), filename = sprintf("iPat_%s_%s", project, trait))
    iPat.Phenotype.View(myY = data.frame(taxa, dataP$data[[trait]]), filename = sprintf("iPat_%s_%s", project, trait))
  }
}, error = function(e) {
  stop(e)
})
