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
  cutoff = args[19]
  #gwas.method = args[20] 

impute = "mean"
shrink = FALSE
gwas.assist = TRUE
cutoff = .05

# Load libraries
  list.of.packages = c("data.table", "magrittr", "rrBLUP")
  new.packages <- list.of.packages[!(list.of.packages%in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(rrBLUP)
  library(data.table)
  library(magrittr)

tryCatch({
  setwd(wd)
  # Subset Phenotype
  Y.data = fread(Y.path) %>% as.data.frame
  subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .)
  index.trait = which(subset == "Selected") 
  if(length(index.trait) == 1){
    Y = data.frame(y = Y.data[, index.trait + 1])
    names(Y) = names(Y.data)[1 + index.trait] 
  }else{
    Y = Y.data[, index.trait + 1]
  }
  # Assign Variables
  taxa = Y.data[,1]
  trait.names = names(Y) 
  # Format-free
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
              G = fread(sprintf("%s.dat", hmp)) %>% t() %>% as.data.frame()}, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(GD.path, 1, nchar(GD.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            G = fread(sprintf("%s.dat", vcf)) %>% t() %>% as.data.frame()},
    PLink_Binary = {
    },{
      # Numeric (Default)
      GD = fread(GD.path) %>% as.data.frame()
      GM = fread(GM.path) %>% as.data.frame()
      if(is.character(GD[,1])) GD = GD[,-1]
    }
  )
  # Covariate
    if(C.path != "NA"){
      C.data = fread(C.path) %>% as.data.frame()
      if(is.character(C.data[,1])) C.data = C.data[,-1]
      C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
      index.C = which(C.model.name == "Selected")
      if(length(index.C) == 1){
        name = names(C.data)[index.C]
        C = data.frame(c = C.data[, index.C])
        names(C) = name
      }else{
        C = C.data[, index.C]
      }
    }else{
      C = NULL
    }
  # rrBLUP
  G.impute =  A.mat(GD, shrink = shrink, impute.method = impute, return.imputed = TRUE, max.missing = ms)$imputed
  for(i in 1:ncol(Y)){
    ## GWAS_assist
    if(gwas.assist){
      ## Read GWAS result
      gwas = fread(sprintf("%s_%s_GWAS.txt", project, trait.names[i]))
      ## Merge GM and p-value
      names(GM)[1] = "SNP"
      map_gwas = merge(GM, gwas, "SNP", all.x = TRUE)
      map_gwas$P.value[is.na(map_gwas$P.value)] = 1
      ## Find Sig. SNP
      index.sig = map_gwas$P.value < (cutoff/nrow(gwas))
      ## Generate dataframe by number of QTNs
      if(sum(index.sig) == 0){
        ans = mixed.solve(Y[,i], K = tcrossprod(G.impute), return.Hinv = TRUE, SE = TRUE)
      }else if(sum(index.sig) == 1){
        C.gwas = data.frame(m = GD[,index.sig])
      }else{
        C.gwas = GD[,index.sig]
        ## LD Remove
        LD_remain = Blink.LDRemove(C.gwas, .7, 1:sum(index.sig), orientation = "col")
        C.gwas = C.gwas[,LD_remain]
      }
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

# A.mat(M,shrink=TRUE) -> for low density markers Vanraden
# Vu = estimator for 
# output add Project name
#  EM imputation algorithm for GBS data (Poland et al. 2012)
# Shrinkage estimation can improve the accuracy of genome-wide marker-assisted selection, partic- ularly at low marker density (Endelman and Jannink 2012).
# GM = read.table("/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/numeric/data.map", head = T)
# 
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

