list.of.packages <- c("MASS", "gplots", "compiler", "scatterplot3d", "R.utils", "data.table", "magrittr", "rrBLUP")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
if(!'multtest'%in% installed.packages()[,"Package"]){
  source("http://www.bioconductor.org/biocLite.R") 
  biocLite("multtest")
}
library(MASS) # required for ginv
library(multtest)
library(gplots)
library(compiler) #required for cmpfun
library(scatterplot3d)
library(R.utils)
library(data.table)
library(magrittr)
library(rrBLUP)
source("./EMMA.R")
source("./GAPIT_Function.R")

myGD=read.table(file="http://zzlab.net/GAPIT/data/mdp_numeric.txt",head=T)
myGM=read.table(file="http://zzlab.net/GAPIT/data/mdp_SNP_information.txt",head=T)
myCV=read.table(file="http://zzlab.net/GAPIT/data/mdp_env.txt",head=T)

#Simultate 10 QTN on the first half chromosomes
X=myGD[,-1]
index1to5=myGM[,2]<6
X1to5 = X[,index1to5]
taxa=myGD[,1]

set.seed(99164)
GD.candidate=cbind(taxa,X1to5)
mySim=GAPIT.Phenotype.Simulation(GD=GD.candidate,
                                 GM=myGM[index1to5,],
                                 h2=.5, NQTN=2, effectunit =.95,
                                 QTNDist="normal", CV=myCV, cveff=c(.01,.01))
setwd('./test/')
myGAPIT <- GAPIT(Y=mySim$Y, 
                 GD=myGD, 
                 GM=myGM, 
                 PCA.total=3, CV=myCV,
                 group.from=1, group.to=1, group.by=10,
                 QTN.position=mySim$QTN.position,memo="GLM")

mySim$Y %>%head

myGAPIT$Pred

pred <- GAPIT(
  Y=mySim$Y[1:100,],
  GD=myGD,
  GM=myGM,
  PCA.total=3,
  CV=myCV,
  group.from=10000,
  group.to=10000,
  group.by=10,
  SNP.test=FALSE,
  memo="gBLUP",
)


pred2 <- GAPIT(
  Y=mySim$Y,
  GD=myGD,
  GM=myGM,
  PCA.total=3,
  CV=myCV,
  group.from=10000,
  group.to=10000,
  group.by=10,
  SNP.test=FALSE,
  memo="gBLUP",
)


###


