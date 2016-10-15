ptm= proc.time()
setwd("~/test")
library(MASS)
library(multtest)
library(gplots)
library(compiler)
library(scatterplot3d)
source('http://www.zzlab.net/GAPIT/emma.txt')
source('http://www.zzlab.net/GAPIT/gapit_functions.txt')

myY  <- read.table('mdp_traits.txt', head = TRUE)
myG <- read.delim('mdp_genotype_test.hmp.txt', head = FALSE)
myGAPIT <- GAPIT(Y=myY,	G=myG, PCA.total=3)
x= proc.time() - ptm 
print(x)



#616.306


java= c(503.624, 521.172, 519.080, 513.529, 471.783)
r= c(507.037, 458.652, 469.784, 530.401, 537.966)