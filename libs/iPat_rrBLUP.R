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
  subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .) %>% as.numeric
  Y = Y.data[, subset+1]

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
      G = fread(GD.path) %>% as.data.frame()
    }
  )

  # Remove first column if it is taxa name
  if(is.character(G[,1])){
    G = G[,-1]
  }

  # rrBLUP
  G.impute =  A.mat(G, shrink = shrink, impute.method = impute, return.imputed = TRUE, max.missing = ms)$imputed
  if(ncol(Y) == 1){
    ans = mixed.solve(Y, K = tcrossprod(G.impute), return.Hinv = TRUE, SE = TRUE)
    out1 = data.frame(Stat = c("Vu", "Ve", "beta", "beta.SE", "LL"),
                      Value = c(ans$Vu, ans$Ve, ans$beta, ans$beta.SE, ans$LL))
    out2 = data.frame(u = ans$u, u.SE = ans$u.SE)
    out3 = ans$Hinv
    write.table(out1, sprintf("rrBLUP_%s_%s_stat.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
    write.table(out2, sprintf("rrBLUP_%s_%s_EBV.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
    write.table(out3, sprintf("rrBLUP_%s_%s_InverseH.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
  }else{
    for(i in 1:ncol(Y)){
      ans = mixed.solve(Y[,i], K = tcrossprod(G.impute), return.Hinv = TRUE, SE = TRUE)
      out1 = data.frame(Stat = c("Vu", "Ve", "beta", "beta.SE", "LL"),
                        Value = c(ans$Vu, ans$Ve, ans$beta, ans$beta.SE, ans$LL))
      out2 = data.frame(u = ans$u, u.SE = ans$u.SE)
      out3 = ans$Hinv
      write.table(out1, sprintf("rrBLUP_%s_%s_stat.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
      write.table(out2, sprintf("rrBLUP_%s_%s_EBV.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
      write.table(out3, sprintf("rrBLUP_%s_%s_InverseH.txt", project, trait_names),
                row.names = F, quote = F, sep = '\t')
    }
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

