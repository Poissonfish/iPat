tryCatch({
# Library
  cat("=== rrBLUP ===\n")
  cat("   Loading libraries ...")
  library(rrBLUP)
  library(data.table)
  library(magrittr)
  library(ggplot2)
  library(gridExtra)
  library(grid)
  source("http://zzlab.net/iPat/Function_iPat.R")
  cat("Done\n")

arg = c("-wd", "~/Desktop/Test/",
        "-project", "project1",
        "-phenotype", "data.txt",
        "-pSelect", "t2sepphenotypesep",
        "-genotype", "data.dat",
        "-cov", "data.cov",
        "-cSelect", "cov2sepcov1sep",
        "-gwas", "FALSE", "3",
        "-gs", "TRUE", "TRUE", "5", "10")

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
        if (grepl("NA", arg[i]))
          rawMap = NULL
        else
          rawMap = fread(arg[i])
        cat("Done\n")
      },
      "-cov" = {
        cat("   Checking covariates ...")
        i = i + 1
        if (grepl("NA", arg[i]))
          rawCov = NULL
        else
          rawCov = fread(arg[i])
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
        i = i + 1
        isRaw = as.logical(arg[i])
        i = i + 1
        countFold = as.numeric(arg[i])
        i = i + 1
        countIter = as.numeric(arg[i])
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

# Iterate over traits
  for (trait in dataP$name) {
    cat(sprintf("   rrBLUP is computing for trait %s ...", trait))
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
          CVByRRBLUP(finalP, finalG, finalC, isRaw,
                    countFold, countIter, project, trait)
    # No validation
      } else {
          finalP = dataP$data[[trait]]
          finalG = rawGenotype
          finalC = Cov
          RunByRRBLUP(finalP, finalG, finalC, taxa, project, trait)
      }
  }
  print(warnings())
}, error = function(e){
  stop(e)
})
