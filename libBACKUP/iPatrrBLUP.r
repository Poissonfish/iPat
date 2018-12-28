tryCatch({
# Library
  cat("=== rrBLUP ===\n")
  cat("   Loading libraries ...")
  library(rrBLUP)
  library(data.table)
  library(magrittr)
  library(ggplot2)
  source("http://zzlab.net/iPat/Function_iPat.R")
  cat("Done\n")
  ANALYSIS = "rrBLUP"

# Input arguments
  arg = commandArgs(trailingOnly=TRUE)
  # ======= Test Code ====== #
  # rm(list=ls())
  # arg = c("-gs", "TRUE", "5", "1",
  #         "-gwas", "TRUE", "0.05",
  #         "-arg", "BRR", "1200", "500",
  #         "-wd", "/Users/jameschen/Desktop/Test/iPatDEMO",
  #         "-project", "farm",
  #         "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.txt",
  #         "-pSelect", "y25sepy50sepy75sep",
  #         # "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/data.txt",
  #         # "-pSelect", "EarHTsepEarDiasep",
  #         "-cov", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.cov",
  #         "-cSelect", "C1sep",
  #         "-genotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.dat",
  #         "-kin", "NA",
  #         "-map", "/Users/jameschen/Desktop/Test/iPatDEMO/demo_recode.nmap")
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
      "-gwas" = {
        i = i + 1
        isGWASAssist = as.logical(arg[i])
        cutoff = 0.05
      },
      "-gs" = {
        i = i + 1
        isValid = as.logical(arg[i])
        # i = i + 1
        # isRaw = as.logical(arg[i])
        isRaw = TRUE
        i = i + 1
        countFold = as.numeric(arg[i])
        i = i + 1
        countIter = as.numeric(arg[i])
      }
    )
  }
  rawKin = NULL

# Subset Phenotype
  cat("   Subsetting phenotype ...")
  dataP = getSelected(rawPhenotype, selectP)
  cat("Done\n")

# Subset Covariates
  cat("   Subsetting covariates ...")
  dataC = getSelected(rawCov, selectC)
  cat("Done\n")

# Iterate over traits
  for (trait in dataP$name) {
    cat(sprintf("   rrBLUP is computing for trait %s ...\n", trait))
    # Collect covariates
      Cov = getCovFromGWAS(isGWASAssist, cutoff,
        sizeN = sizeN, dataCov = dataC,
        nameProject = project, nameTrait = trait,
        rawGenotype, rawMap)
    # Validation
      if (isValid) {
        # In case have any missing data
          idxNonNA = !is.na(dataP$data[[trait]])
          finalP = dataP$data[[trait]][idxNonNA]
          finalG = rawGenotype[idxNonNA, ]
          finalC = Cov[idxNonNA, ]
        # Run Validation
          runCrossValidation(finalP, finalG, finalC, isRaw,
                    countFold, countIter, project, trait)
    # No validation
      } else {
          finalP = dataP$data[[trait]]
          finalG = rawGenotype
          finalC = Cov
          runRRBLUP(finalP, finalG, finalC, taxa, project, trait)
      }
      iPat.Genotype.View(myGD = data.frame(taxa, rawGenotype), filename = sprintf("iPat_%s_%s", project, trait))
      iPat.Phenotype.View(myY = data.frame(taxa, dataP$data[[trait]]), filename = sprintf("iPat_%s_%s", project, trait))
    cat("Done\n")
  }
  # print(warnings())
}, error = function(e){
  stop(e)
})
