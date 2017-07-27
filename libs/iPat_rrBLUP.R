# Input arguments
  args = commandArgs(trailingOnly=TRUE)
# Common args
  project = args[1]
  wd = args[2]
  lib = args[3]
  format = args[4]
  ms = as.numeric(args[5])
  maf  = as.numeric(args[6])
  Y.path = args[7]
  Y.index = args[8]
  GD.path = args[9]
  GM.path  = args[10]
  C.path = args[11]
  C.index = args[12]
  K.path  = args[13]
  FAM.path  = args[14]
  BIM.path  = args[15]
# Method specific args
  impute = args[16]
  shrink = as.logical(args[17])
  gwas.assist = as.logical(args[18])
  cutoff = as.numeric(args[19])
  
# Load libraries
  cat("=== rrBLUP ===\n")
  cat("   Loading libraries ...")
  list.of.packages = c("data.table", "magrittr", "rrBLUP")
  new.packages <- list.of.packages[!(list.of.packages%in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(rrBLUP)
  library(data.table)
  library(magrittr)
  cat("Done\n")

tryCatch({
  setwd(wd)
  # Subset Phenotype
  cat("   Loading phenotype ...")
  Y.data = fread(Y.path) %>% as.data.frame
  subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .)
  index.trait = which(subset == "Selected") 
  if(length(index.trait) == 1){
    Y = data.frame(y = Y.data[, index.trait + 1])
    names(Y) = names(Y.data)[1 + index.trait] 
  }else{
    Y = Y.data[, index.trait + 1]
  }
  cat("Done\n")
  # Assign Variables
  taxa = Y.data[,1]
  trait.names = names(Y) 
  # Format-free
  cat("   Loading genotype and do conversion if need ...")
  OS.Windows = FALSE
  switch(Sys.info()[['sysname']],
    Windows= {OS.Windows = TRUE}, # Windows
    Linux  = { }, # Linux
    Darwin = { }) # MacOS
  switch(format, 
    Hapmap = {if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
              hmp = substring(GD.path, 1, nchar(GD.path)-4)
              sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
              sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
              GD = fread(sprintf("%s.dat", hmp)) %>% t() %>% as.data.frame()}, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(GD.path, 1, nchar(GD.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            GD = fread(sprintf("%s.dat", vcf)) %>% t() %>% as.data.frame()},
    PLink_Binary = {
    },{
      # Numeric (Default)
      GD = fread(GD.path) %>% as.data.frame()
      GM = fread(GM.path) %>% as.data.frame()
    }
  )
  if(is.character(GD[,1])) GD = GD[,-1]
  cat("Done\n")
  # QC
    cat("   Quality control ...")
    # Missing rate
    if(!is.na(ms)){
      MS = is.na(GD) %>% apply(2, function(x) sum(x)/length(x))
      GD = GD[, MS <= ms]
      GM = GM[MS <= ms, ]}
    # MAF
    if(!is.na(maf)){
      MAF = apply(GD, 2, mean) %>% 
            as.matrix() %>% 
            apply(1, function(x) min(1 - x/2, x/2))
      GD = GD[, MAF >= maf]
      GM = GM[MAF >= maf, ]}
  cat("Done\n")
  # Covariate
    if(C.path != "NA"){
      cat("   Loading covariates ...")
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
      index.C = which(C.model.name == "Selected")
      # 1 c
      if(length(index.C) == 1){
        name = names(C.data)[index.C]
        C = data.frame(c = C.data[, index.C])
        names(C) = name
      # 0 or More than 1 c
      }else{
        C = C.data[, index.C]
      }
      cat("Done\n")
    }else{
      C = NULL
    }
  # rrBLUP
  ## Generate kjinship
  if(K.path == "NA"){
    G.impute =  A.mat(GD, shrink = shrink, impute.method = impute, return.imputed = TRUE, max.missing = ms)$imputed
    K = tcrossprod(G.impute)
  }else{
    K = fread(K.path) %>% as.data.frame()
  }
  for(i in 1:ncol(Y)){
    ## GWAS-assist
    if(gwas.assist){
      cat("   Loading QTNs information ...")
      ## Read GWAS result
      gwas = fread(sprintf("%s_%s_GWAS.txt", project, trait.names[i]))
      ## Merge GM and p-value
      names(GM)[1] = "SNP"
      map_gwas = data.frame(GM, P.value = gwas$P.value[match(GM$SNP, gwas$SNP)])
      map_gwas$P.value[is.na(map_gwas$P.value)] = 1
      ## Order p-value
      snp_order = order(map_gwas$P.value) 
      map_gwas = map_gwas[snp_order,]
      GD = GD[,snp_order]
      ## Find QTNs
      index.sig = which(map_gwas$P.value < (cutoff/nrow(gwas)))
      ## Generate a dataframe by number of QTNs
       ### 0 QNTs
      cat("Done\n")
      cat(sprintf("   rrBLUP is computing for trait %s", trait.names[i]))
      if(length(index.sig) == 0){
        ans = mixed.solve(Y[,i], X = C, K = K, return.Hinv = TRUE, SE = TRUE)
       ### 1 QTNs
      }else if(length(index.sig) == 1){
        C.gwas = data.frame(m = GD[,index.sig])
        ans = mixed.solve(Y[,i], X = cbind(C, C.gwas), K = K, return.Hinv = TRUE, SE = TRUE)
       ### 1+ QTNs
      }else{
        C.gwas = GD[,index.sig]
        ## LD Remove
        LD_remain = Blink.LDRemove(C.gwas, .7, index.sig, orientation = "col")
        C.gwas = C.gwas[,LD_remain]
        ## Prevent c > n
        if(length(Y[,i]) < length(index.C) + ncol(C.gwas)){
          diff = length(index.C) + ncol(C.gwas) - length(Y[,i])
          C.gwas = C.gwas[ ,1 : (ncol(C.gwas) - diff)]
          ans = mixed.solve(Y[,i], X = cbind(C, C.gwas), K = K, return.Hinv = TRUE, SE = TRUE)
        }else{
          ans = mixed.solve(Y[,i], X = cbind(C, C.gwas), K = K, return.Hinv = TRUE, SE = TRUE)
        }
      }
      cat("Done\n")
    }else{
      cat(sprintf("   rrBLUP is computing for trait %s ...", trait.names[i]))
      ans = mixed.solve(Y[,i], X = C, K = K, return.Hinv = TRUE, SE = TRUE)
      cat("Done\n")
    }
    write.table(data.frame(Stat = c("Vu", "Ve", "beta", "beta.SE", "LL"),
                           Value = c(ans$Vu, ans$Ve, ans$beta, ans$beta.SE, ans$LL)),
                sprintf("rrBLUP_%s_%s_stat.txt", project, trait_names[i]),
                row.names = F, quote = F, sep = '\t')
    write.table(data.frame(u = ans$u, u.SE = ans$u.SE), 
                sprintf("rrBLUP_%s_%s_EBV.txt", project, trait_names[i]),
                row.names = F, quote = F, sep = '\t')
    write.table(ans$Hinv, 
                sprintf("rrBLUP_%s_%s_InverseH.txt", project, trait_names[i]),
                row.names = F, quote = F, sep = '\t')
  }
  print(warnings())
}, error = function(e){
  stop(e)
})

# impute = "mean"
# shrink = FALSE
# gwas.assist = TRUE
# cutoff = .05

# A.mat(M,shrink=TRUE) -> for low density markers Vanraden
# Vu = estimator for 
# output add Project name
#  EM imputation algorithm for GBS data (Poland et al. 2012)
# Shrinkage estimation can improve the accuracy of genome-wide marker-assisted selection, partic- ularly at low marker density (Endelman and Jannink 2012).
# GM = read.table("/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/numeric/data.map", head = T)
# # validation rrBLUP
# sam = sample(nrow(Y), round(nrow(Y)*.2))
# Y.train = Y
# Y.train[sam,] = NA
# Y.valid = Y[sam,]
# pca = prcomp(G)
# CO = pca$x[,1:3]
# KI= GAPIT.kinship.VanRaden(snps=as.matrix(G))
# A = A.mat(G)
# B = tcrossprod(G)
# G.impute =  A.mat(G, shrink = TRUE, impute.method = "EM", return.imputed = TRUE, max.missing = ms)$imputed
# ans1 <- mixed.solve(Y.train[,1], K =, return.Hinv = TRUE, SE = TRUE)
# ans2 <- mixed.solve(Y.train[,1], K = A.mat(cbind(G, CO)), return.Hinv = TRUE, SE = TRUE)
# ans3 <- mixed.solve(Y.train[,1], X = CO, K = A.mat(G), return.Hinv = TRUE, SE = TRUE)
# ans4 <- mixed.solve(Y.train[,1], K = KI, return.Hinv = TRUE, SE = TRUE)
# ans5 <- mixed.solve(Y.train[,1], K = tcrossprod(G.impute), return.Hinv = TRUE, SE = TRUE)
# ans6 <- mixed.solve(Y.train[,1], X = CO, K = tcrossprod(G.impute), return.Hinv = TRUE, SE = TRUE)
# cor(ans1$u[sam], Y.valid)
# cor(ans2$u[sam], Y.valid)
# cor(ans3$u[sam], Y.valid)
# cor(ans4$u[sam], Y.valid)
# cor(ans5$u[sam], Y.valid)
# cor(ans6$u[sam], Y.valid)

