tryCatch({
# Library
  cat("=== FarmCPU ===\n")
  cat("   Loading libraries ...")
  library(bigmemory)
  library(biganalytics)
  library(compiler)
  library(data.table)
  library(magrittr)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(scatterplot3d)
  library(R.utils)
  library(ape)
  library(magrittr)
  library(data.table)
  source("http://zzlab.net/iPat/Function_iPat.R")
  source("http://zzlab.net/GAPIT/gapit_functions.txt")
  source("http://zzlab.net/FarmCPU/FarmCPU_functions.txt")
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
      },
      "-cSelect" = {
        i = i + 1
        selectC = ifelse(grepl("NA", arg[i]),
          NA, arg[i])
      },
      "-phenotype" = {
        cat("   Loading phenotype ...")
        i = i + 1
        phenotype = fread(arg[i])
        taxa = phenotype[ ,1]
        cat("Done\n")
      },
      "-genotype" = {
        cat("   Loading genotype ...")
        i = i + 1
        genotype = fread(arg[i])
        if (is.character(genotype[[1]]))
          genotype = genotype[ ,-1]
        cat("Done\n")
      },
      "-map" = {
        cat("   Loading map ...")
        i = i + 1
        if (grepl("NA", arg[i]))
          map = NULL
        else
          map = fread(arg[i])
        cat("Done\n")
      },
      "-cov" = {
        cat("   Checking covariates ...")
        i = i + 1
        if (grepl("NA", arg[i]))
          cov = NULL
        else
          cov = fread(arg[i])
        if (is.character(cov[[1]]))
          cov = cov[ ,-1]
        cat("Done\n")
      },
      "-kin" = {
        cat("   Checking kinship ...")
        i = i + 1
        if (grepl("NA", arg[i]))
          kin = NULL
        else
          kin = fread(arg[i])
        cat("Done\n")
      },
      "-arg" = {
        i = i + 1
        method.bin = arg[i]
        i = i + 1
        maxLoop = as.numeric(arg[i])
      }
    )
  }

# Subset Phenotype
  cat("   Subsetting phenotype ...")
  indexP = selectP %>%
    strsplit(split = "sep") %>%
    do.call(c, .) %>%
    (function(x){which(x == "Selected") + 1})
  nameTraits = names(phenotype)[indexP]
  phenotype = phenotype[, ..indexP]
  cat("Done\n")

# QC
  cat("   Quality control ...")
  # Missing rate
    MS = is.na(genotype) %>%
      apply(2, function(x) sum(x)/length(x))
    genotype = data.frame(taxa, genotype[, MS <= ms, with = FALSE])
    map = data.frame(map[MS <= ms])
    # impute?
    genotype[is.na(genotype)] = 1
  cat("Done\n")

# Subset Covariates
  cat("   Subsetting covariates ...")
  if (!is.null(cov)) {
    indexC = selectC %>%
      strsplit(split = "sep") %>%
      do.call(c, .) %>%
      (function(x){which(x == "Selected")})
    cov = data.frame(cov[, ..indexC])
  }
  cat("Done\n")

# FarmCPU
  for (trait in nameTraits) {
    x = FarmCPU(
          Y = data.frame(taxa, phenotype[, trait, with = FALSE]),
          GM = map,
          GD = genotype,
          CV = cov,
          method.bin = method.bin,
          bin.size = c(5e5, 5e6, 5e7), # Default set of bin.size
          bin.selection = seq(10, 100, 10), # Default set of bin.selection
          maxLoop = maxLoop,
          MAF.calculate = TRUE,
          maf.threshold = maf,
          memo = sprintf("%s_%s", project, trait))
    write.table(x = data.frame(SNP = x$GWAS$SNP, P.value = x$GWAS$P.value),
                file = sprintf("%s_%s_GWAS.txt", project, trait),
                quote = F, row.names = F, sep = "\t")
  }

print(warnings())
}, error = function(e) {
  stop(e)
})
