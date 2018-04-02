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
  source("/Users/jameschen/IdeaProjects/iPat/target/classes/Function_iPat.R")
  source("/Users/jameschen/IdeaProjects/iPat/target/classes/Function_GAPIT.R")
  source("/Users/jameschen/IdeaProjects/iPat/target/classes/Function_FarmCPU.R")
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
        if (grepl("/NA", arg[i]))
          map = NULL
        else
          map = fread(arg[i])
        cat("Done\n")
      },
      "-cov" = {
        cat("   Checking covariates ...")
        i = i + 1
        if (grepl("/NA", arg[i]))
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
        if (grepl("/NA", arg[i]))
          kin = NULL
        else
          kin = data.frame(fread(arg[i]))
        cat("Done\n")
      },
      "-arg" = {
        i = i + 1
        model = arg[i]
        i = i + 1
        ki.c = arg[i]
        i = i + 1
        ki.g = arg[i]
        i = i + 1
        snp.fraction = as.numeric(arg[i])
        i = i + 1
        model.s = as.logical(arg[i])
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
    genotype = genotype[, MS <= ms, with = FALSE]
    map = map[MS <= ms]
  # MAF
    # No NA allowed in GAPIT
    genotype[is.na(genotype)] = 1
    MAF = apply(genotype, 2, mean) %>%
          as.matrix() %>%
          apply(1, function(x) min(1 - x/2, x/2))
    genotype = data.frame(taxa, genotype[, MAF >= maf, with = FALSE])
    map = data.frame(map[MAF >= maf])
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

# Model
  cat("   Model setup ...")
  switch(model,
    "GLM" = {
      g.from = 1
      g.to = 1
      g.by = 10},
    "MLM" = {
      g.from = 10000000
      g.to = 10000000
      g.by = 10},
    "CMLM" = {
      g.from = 1
      g.to = 10000000
      g.by = 10}
  )
  cat("Done\n")

# GAPIT
  for (trait in nameTraits) {
    x = GAPIT(
          Y = data.frame(taxa, phenotype[, trait, with = FALSE]),
          GM = map,
          GD = genotype,
          KI = kin,
          CV = cov,
          PCA.total = 3,
          kinship.cluster = ki.c,
          kinship.group = ki.g,
          group.from = g.from,
          group.to = g.to,
          group.by = g.by,
          Model.selection = model.s,
          SNP.fraction = snp.fraction,
          memo = sprintf("%s_%s", project, trait))
    write.table(x = data.frame(SNP = x$GWAS$SNP, P.value = x$GWAS$P.value),
              file = sprintf("%s_%s_GWAS.txt", project, trait),
              quote = F, row.names = F, sep = "\t")
  }

print(warnings())
}, error = function(e) {
  stop(e)
})
