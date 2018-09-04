tryCatch({
# Library
  cat("=== BGLR ===\n")
  cat("   Loading libraries ...")
  library(magrittr)
  library(data.table)
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(compiler) #required for cmpfun
  library(scatterplot3d)
  library(R.utils)
	library(BGLR)
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
        model = arg[i]
				i = i + 1
        response = arg[i]
				i = i + 1
        nIter = as.numeric(arg[i])
				i = i + 1
        burnIn = as.numeric(arg[i])
				i = i + 1
        thin = as.numeric(arg[i])
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

# Define ETA in BGLR
	ETA_base = list(list(X = data.frame(genotype), model = model))

# Put covariates
	if (!is.null(cov)) {
		cat("   Loading covariates ...")
		sizeCov = 0
		nameC = selectC %>%
			strsplit(split = "sep") %>%
			do.call(c, .)
		for (i in 1 : length(nameC)) {
			if (nameC[i] != "OMIT IT") {
				sizeCov = sizeCov + 1
				length(ETA_base) = length(ETA_base) + 1
				ETA_base[[length(ETA_base)]] = list(X = data.frame(cov[ ,..i]), model = nameC[i])
			}
		}
		cat("Done\n")
  } else
    sizeCov = 0

# Put kinship
	if (!is.null(kin)) {
		cat("   Loading Kinship ...")
		length(ETA_base) = length(ETA_base) + 1
		ETA_base[[length(ETA_base)]] = list(K = K, model = "RKHS")
		cat("Done\n")
	}

# Analysis trait iteratively
  for (trait in nameTraits) {
		ETA = ETA_base
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
						length(ETA) = length(ETA) + 1
						ETA[[length(ETA)]] = list(X = C.gwas, model = "FIXED")
            sizeQTN = 1
        ### 1+ QTNs
          } else {
            cGWAS = genotype[ ,..indexSig]
            ## LD Remove
            LD_remain = Blink.LDRemove(cGWAS, .7, indexSig, orientation = "col")
            cGWAS = data.frame(cGWAS[ ,..LD_remain])
						length(ETA) = length(ETA) + 1
						ETA[[length(ETA)]] = list(X = C.gwas, model = "FIXED")
            sizeQTN = length(LD_remain)
          }
          cat("Done\n")
      } else {
        cGWAS = NULL
        sizeQTN = 0
      }
    # BGLR
		cat(sprintf("   BGLR is computing for trait %s ...", trait))
		blr = BGLR(y = phenotype[[trait]], ETA = ETA, response_type = response,
				   nIter = nIter, burnIn = burnIn, thin = thin, verbose = FALSE,
				   saveAt = sprintf("BGLR_%s_%s_", project, trait))
		write.table(data.frame(taxa = taxa, Pred = blr$yHat, SD = blr$SD.yHat),
                sprintf("iPat_%s_%s_EBV.dat", project, trait),
                row.names = F, col.names = F, quote = F, sep = '\t')
		pdf(sprintf("iPat_%s_%s_GEBV_value.pdf", project, trait), width = 5, height = 5)
		plot(blr$y, blr$yHat, main = "Phenotype v.s. GEBV")
		dev.off()
		pdf(sprintf("iPat_%s_%s_GEBV_SD.pdf", project, trait), width = 5, height = 5)
		plot(blr$y, blr$SD.yHat, main = "Phenotype v.s. SD of GEBV")
		dev.off()
		pdf(sprintf("iPat_%s_%s_GEBV_hist.pdf", project, trait), width = 5, height = 5)
		hist(blr$yHat, main = "Distribution of GEBV")
		dev.off()
	  cat("Done\n")
  }

print(warnings())
}, error = function(e) {
  stop(e)
})

# ETA<-list(list(K=KI,model='RKHS'))
# fm<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm$yHat[sam], Y.valid)
# ETA = list(list(K = KI, model = 'RKHS'), list(X=G, model='BL'))
# fm2<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm2$yHat[sam], Y.valid)
