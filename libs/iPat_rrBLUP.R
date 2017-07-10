# Input arguments
args = commandArgs(trailingOnly=TRUE)
G.path = args[1]
Y.path = args[2]
wd = args[3]
lib = args[4]
format = args[5]
arg_length = 5
G.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.hmp"
Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.txt"
wd = "/Users/Poissonfish/Desktop/test/rr"
lib = "/Users/Poissonfish/git/iPat/libs/"
format = "Hapmap"
args = c(1,2,3,4,5,2,1)

tryCatch({
  # Load libraries
  list.of.packages = c("data.table", "magrittr", "rrBLUP")
  new.packages <- list.of.packages[!(list.of.packages%in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(rrBLUP)
  library(data.table)
  library(magrittr)

  setwd(wd)
  # Read traits
  Y = fread(Y.path) %>% as.data.frame
  # First col can't contain inidvidual name, extract taxa names
  if(is.character(Y[,1])){
        taxa = Y[,1]
        Y = Y[,-1]
  }else{
        taxa = 1:ncol(Y)
  } 
  # Select Phenotype
  trait = c()
  if(length(args)>arg_length){
    for (i in (arg_length+1):length(args)){
      trait = c(trait, as.numeric(args[i])) 
    }
    if(length(trait) == 1){
      trait_names = names(Y)[trait]
      Y = Y[,trait] %>% as.matrix(ncol = 1)
    }else{
      Y = Y[,trait] 
      trait_names = names(Y)
    }
  }

  # Format-free
  OS.Windows = FALSE
  switch(Sys.info()[['sysname']],
    Windows= {OS.Windows = TRUE}, # Windows
    Linux  = { }, # Linux
    Darwin = { }) # MacOS
  switch(format, 
    Hapmap = {if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
              hmp = substring(G.path, 1, nchar(G.path)-4)
              sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
              sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
              G = read.table(sprintf("%s.dat", hmp)) %>% t()}, 
    VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
            vcf = substring(G.path, 1, nchar(G.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            G = read.table(sprintf("%s.dat", vcf)) %>% t()},
    PLink_Binary = {
    },{
      # Numeric (Default)
      G = fread(G.path) %>% as.data.frame
    }
  )

  # Remove first column if it is taxa name
  if(is.character(G[,1])){
    G = G[,-1]
  }


# A.mat(M,shrink=TRUE) -> for low density markers Vanraden
# Vu = estimator for 
# output add Project name
#  EM imputation algorithm for GBS data (Poland et al. 2012)
# Shrinkage estimation can improve the accuracy of genome-wide marker-assisted selection, partic- ularly at low marker density (Endelman and Jannink 2012).
#

# validation rrBLUP
sam = sample(length(Y), round(length(Y)*.2))
Y.train = Y
Y.train[sam] = NA
Y.valid = Y[sam]
CO = pca$x[,1:3]
KI= GAPIT.kinship.VanRaden(snps=as.matrix(G))

ans1 <- mixed.solve(Y.train, K = A.mat(G), return.Hinv = TRUE, SE = TRUE)
ans2 <- mixed.solve(Y.train, K = A.mat(cbind(G, CO)), return.Hinv = TRUE, SE = TRUE)
ans3 <- mixed.solve(Y.train, X = CO, K = A.mat(G), return.Hinv = TRUE, SE = TRUE)
ans4 <- mixed.solve(Y.train, K = KI, return.Hinv = TRUE, SE = TRUE)
cor(ans1$u[sam], Y.valid)
cor(ans2$u[sam], Y.valid)
cor(ans3$u[sam], Y.valid)
cor(ans4$u[sam], Y.valid)

ETA<-list(list(K=KI,model='RKHS')) 
fm<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)

yHat<-fm$yHat
tmp<-range(c(y,yHat)) 
plot(yHat~y,xlab='Observed',ylab='Predicted',col=2,
              xlim=tmp,ylim=tmp); abline(a=0,b=1,col=4,lwd=2)

cor(fm$yHat[sam], Y.valid)


 pred <- GAPIT(
      Y = Y.train,
      GD = G,    
      CV = CO,
      group.from=10000,
      group.to=10000,
      group.by=10,
      SNP.test=FALSE,
      file.output = F
    )







out1 = data.frame(Stat = c("Vu", "Ve", "beta", "beta.SE", "LL"),
                  Value = c(ans$Vu, ans$Ve, ans$beta, ans$beta.SE, ans$LL))
out2 = data.frame(u = ans$u, u.SE = ans$u.SE)
out3 = ans$Hinv

write.table(out1, sprintf("rrBLUP_stat_%s.txt", trait_names),
            row.names = F, quote = F, sep = '\t')
write.table(out2, sprintf("rrBLUP_EBV_%s.txt", trait_names),
            row.names = F, quote = F, sep = '\t')
write.table(out3, sprintf("rrBLUP_InverseH_%s.txt", trait_names),
            row.names = F, quote = F, sep = '\t')


  # rrBLUP
  if(ncol(Y) == 1){
    ans = mixed.solve(Y, K = A.mat(G))
    write.table(data.frame(Taxa = taxa, u = ans$u), 
                sprintf("rrBLUP_out_%s.txt", trait_names),
                row.names = F,
                quote = F, 
                sep = '\t')
  }else{
    for(i in 1:ncol(Y)){
      ans = mixed.solve(Y[,i], K = A.mat(G))
      write.table(data.frame(Taxa = taxa, u = ans$u), 
                  sprintf("rrBLUP_out_%s.txt", trait_names[i]),
                  row.names = F,
                  quote = F, 
                  sep = '\t')
    }
  }
  print(warnings())
}, error = function(e){
  stop(e)
})
