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
          kin = data.frame(fread(arg[i]))
        cat("Done\n")
      },
      "-gwas" = {
        i = i + 1
        gwasAssist = as.logical(arg[i])
        i = i + 1
        cutoff = as.numeric(arg[i])
      },
      "-arg" = {
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
  # if it's PLINK's phenotype
  if (toupper(names(phenotype)[1]) == "FID") {
    indexP = indexP + 1
    nameTraits = nameTraits[ ,-1]
  }
  nameTraits = names(phenotype)[indexP]
  phenotype = phenotype[, ..indexP]
  sizeN = nrow(phenotype)
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
    genotype = genotype[, MAF >= maf, with = FALSE]
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
    sizeCov = ncol(cov)
  } else
    sizeCov = 0
  cat("Done\n")

# Analysis trait iteratively
  for (trait in nameTraits) {
    # GWAS-Assisted
      if (gwasAssist) {
        cat("   Loading QTNs information ...")
        ## Read GWAS result
          gwas = fread(sprintf("%s_%s_GWAS.txt", project, trait))
        ## Merge GM and p-value
          names(map)[1] = "SNP"
          mapGWAS = data.table(map)
          mapGWAS[ ,P.value := gwas$P.value[match(map$SNP, gwas$SNP)]]
          mapGWAS$P.value[is.na(mapGWAS$P.value)] = 1
        ## Order p-value
          snpOrder = order(mapGWAS$P.value)
          mapGWAS = mapGWAS[snpOrder]
          genotype = genotype[ ,..snpOrder]
        ## Find QTNs
          indexSig = which(mapGWAS$P.value < (cutoff/nrow(gwas)))
        ## Generate a dataframe by number of QTNs
        ### 0 QTNs
          if (length(indexSig) == 0) {
            cGWAS = NULL
            sizeQTN = 0
        ### 1 QTNs
          } else if (length(indexSig) == 1) {
            cGWAS = data.frame(m = genotype[ ,..indexSig])
            sizeQTN = 1
        ### 1+ QTNs
          } else {
            cGWAS = genotype[ ,..indexSig]
            ## LD Remove
            LD_remain = Blink.LDRemove(cGWAS, .7, indexSig, orientation = "col")
            cGWAS = data.frame(cGWAS[ ,..LD_remain])
            sizeQTN = length(LD_remain)
          }
          cat("Done\n")
      } else {
        cGWAS = NULL
        sizeQTN = 0
      }
    # Prevent c > n
      ## if c + qtn > n
        if (sizeN < sizeCov + sizeQTN) {
          diff = sizeCov + sizeQTN - sizeN
          cov = data.frame(cov, cGWAS[ ,1 : (sizeQTN - diff)])
      ## neither cov nor qtn has size greater than 0
        } else if (sizeCov == 0 && sizeQTN == 0) {
          cov = NULL
      ## if c + qtn <= n
        } else {
          cov = data.frame(cov, cGWAS)
        }
  # GAPIT
    x = GAPIT(
      Y = data.frame(taxa, phenotype[[trait]]),
      GD = data.frame(taxa, genotype),
      GM = map,
      KI = kin,
      CV = cov,
      group.from = 10000,
      group.to = 10000,
      group.by = 10,
      Model.selection = model.s,
      SNP.fraction = snp.fraction,
      SNP.test = FALSE,
      memo = sprintf("%s_%s", project, trait)
    )
    iPat.Phenotype.View(myY = data.frame(taxa, phenotype[[trait]]), filename = sprintf("iPat_%s_%s", project, trait))
    write.table(x = data.frame(taxa = x$Pred$Taxa, Pred = x$Pred$Prediction, PEV = x$Pred$PEV),
          file = sprintf("iPat_%s_%s_EBV.txt", project, trait),
          quote = F, row.names = F, sep = "\t")
    pdf(sprintf("iPat_%s_%s_GEBV_value.pdf", project, trait), width = 5, height = 5)
    plot(phenotype[[trait]], x$Pred$Prediction, main = "Phenotype v.s. GEBV")
    dev.off()
    pdf(sprintf("iPat_%s_%s_GEBV_PEV.pdf", project, trait), width = 5, height = 5)
    plot(phenotype[[trait]], x$Pred$PEV, main = "Phenotype v.s. PEV")
    dev.off()
    pdf(sprintf("iPat_%s_%s_GEBV_hist.pdf", project, trait), width = 5, height = 5)
    hist(x$Pred$Prediction, main = "Distribution of GEBV")
    dev.off()
  }
  print(warnings())
}, error = function(e) {
  stop(e)
})
