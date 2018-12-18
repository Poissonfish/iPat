tryCatch({
# Library
  cat("=== BGLR ===\n")
  cat("   Loading libraries ...")
  library(BGLR)
  library(data.table)
  library(magrittr)
  library(ggplot2)
  source("http://zzlab.net/iPat/Function_iPat.R")
  cat("Done\n")
  ANALYSIS = "BGLR"

  # ======= Test Code ====== #
    # rm(list=ls())
    # arg = c("-gs", "TRUE", "5", "1",
    #         "-gwas", "FALSE", "0",
    #         "-arg", "BRR", "1200", "500",
    #         "-wd", "/Users/jameschen/Desktop/Test/iPatDEMO",
    #         "-project", "BGLR",
    #         "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.txt",
    #         "-pSelect", "y75sepy25sep",
    #         # "-phenotype", "/Users/jameschen/Desktop/Test/iPatDEMO/data.txt",
    #         # "-pSelect", "EarHTsepEarDiasep",
    #         "-cov", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.cov",
    #         "-cSelect", "C1sep",
    #         "-genotype", "/Users/jameschen/Desktop/Test/iPatDEMO/demo.dat",
    #         "-kin", "NA",
    #         "-map", "NA")
    # trait = dataP$name[1]
  # ======= Test Code ====== #

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
        i = i + 1
        cutoff = as.numeric(arg[i])
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
      },
      "-arg" = {
        i = i + 1
        modelBGLR = arg[i]
				i = i + 1
        nIter = as.numeric(arg[i])
				i = i + 1
        burnIn = as.numeric(arg[i])
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
    cat(sprintf("   BGLR is computing for trait %s ...", trait))
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
          runBGLR(finalP, finalG, finalC, taxa, project, trait)
      }
    cat("Done\n")
  }
  print(warnings())
}, error = function(e){
  stop(e)
})


#
# set.seed(99163)
# idx.test = sample(nrow(phenotype), round(nrow(phenotype) * 0.2))
# y = phenotype[idx.test, 2] %>% as.matrix() %>% c()
# phenotype[idx.test, 2] = NA

# Input arguments
# arg = c(
#   "-wd", "~/Desktop/jay",
#   "-project", "bglr",
#   "-pSelect", "Selected",
#   "-maf", "0.05",
#   "-ms", "0.20",
#   "-format", "numeric",
#   "-cSelect", "NA",
#   "-phenotype", "~/Desktop/jay/data.txt",
#   "-genotype", "~/Desktop/jay/data.dat",
#   "-map", "NA",
#   "-cov", "NA",
#   "-kin", "NA",
#   "-gwas", "FALSE", "0.0001",
#   "-arg", "BL", "gaussian", "500", "500", "3"
# )
#
# -project, bglr,
# -cSelect, NA,
# -pSelect, Selectedsep,
# -maf, 0.05, -ms, 0.2,
# -format, Numeric,
# -gwas, FALSE, 0.00001,
# -arg, BL, gaussian, 500, 500, 5, -wd, /Users/jameschen, -project, bglr,
# -cSelect, NA,
# -phenotype, /Users/jameschen/Desktop/jay/data.txt,
# -cov, /Applications/iPat.app/Contents/MacOS/NA,
#  -kin, /Applications/iPat.app/Contents/MacOS/NA,
#  -genotype, /Users/jameschen/Desktop/jay/data.dat,
#  -map, /Applications/iPat.app/Contents/MacOS/NA
# cor(blr$yHat[idx.test], y)
