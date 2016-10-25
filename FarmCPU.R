rm(list=ls())

library(bigmemory)
library(biganalytics)
require(compiler) #for cmpfun

source("http://www.zzlab.net/GAPIT/gapit_functions.txt")#web source code
source("http://www.zzlab.net/FarmCPU/FarmCPU_functions.txt")#web source code
myY <- read.table("mdp_traits_validation.txt", head = TRUE)
myGD <- read.big.matrix("mdp_numeric.txt",type = "char",sep = "\t",header = T)
myGM <-  read.table("mdp_SNP_information.txt" , head = TRUE)
myFarmCPU=FarmCPU( Y=myY[,c(1,8)], GD=myGD, GM=myGM)
