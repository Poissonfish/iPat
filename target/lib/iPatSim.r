library(multtest)
library(gplots)
library(LDheatmap)
library(genetics)
library(ape)
library(EMMREML)
library(compiler) #this library is already installed in R
library(scatterplot3d)
library(magrittr)
library(data.table)
source("http://zzlab.net/GAPIT/gapit_functions.txt")
source("http://zzlab.net/GAPIT/emma.txt")

setwd("/Users/jameschen/Desktop/Test/iPatDEMO")

####### ================ Simulation ================ #######
n = 500
m = 1e+4
alleles = c(0, 2)

seq_gd = sample(alleles, n*m, replace = T)
seq_gd[sample(length(seq_gd))[1:(n*m*0.001)]] = 1
GD = matrix(seq_gd, ncol = m, nrow = n) %>% data.frame(taxa = paste0("ID_", 1:n), .)
GM = data.table(
  SNP = paste0("SNP_", 1:m),
  Chromosome = rep(1:10, each = m/10),
  Position = rep(seq(1, 1e+6, m/10), 10))

Y_sim75 = GAPIT.Phenotype.Simulation(GD, h2 = 0.75, NQTN = 30)
Y_sim50 = GAPIT.Phenotype.Simulation(GD, h2 = 0.50, NQTN = 30)
Y_sim25 = GAPIT.Phenotype.Simulation(GD, h2 = 0.25, NQTN = 30)
Y = data.table(y75 = Y_sim75$Y$V1,
               y50 = Y_sim50$Y$V1,
               y25 = Y_sim25$Y$V1)

Cov = data.table(C1 = rnorm(n, sd = 2), C2 = rnorm(n, sd = 2))
beta = c(.6, .8)
Xb = as.matrix(Cov) %*% as.matrix(beta)
for (i in 1:3) {
  Y[,i] = Y[,..i] + Xb + rnorm(n, sd = .5)
  Y[sample(n, 3),i] = NA
}

fwrite(x = data.table(GD), file = "demo.dat", quote = F, sep = "\t",
      row.names = F, col.names = F)
fwrite(x = GM, file = "demo.map", quote = F, sep = "\t",
      row.names = F, col.names = T)
fwrite(x = Y, file = "demo.txt", quote = F, sep = "\t",
      row.names = F, col.names = T)
fwrite(x = Cov, file = "demo.cov", quote = F, sep = "\t",
      row.names = F, col.names = T)

########
n = 281
m = 3093
GD = fread("demo.dat") %>% as.data.frame()
GD[,1] = paste0("ID_", 1:n)

# pca = prcomp(GD[,-1])
# pca$x[,1:3] %>% data.table() %>% fwrite(file = "demo.cov", quote = F, sep = "\t",
                                        # row.names = F, col.names = T)

Y_sim75 = GAPIT.Phenotype.Simulation(GD, h2 = 0.75, NQTN = 30)
Y_sim50 = GAPIT.Phenotype.Simulation(GD, h2 = 0.50, NQTN = 30)
Y_sim25 = GAPIT.Phenotype.Simulation(GD, h2 = 0.25, NQTN = 30)
Y = data.table(y75 = Y_sim75$Y$V1,
               y50 = Y_sim50$Y$V1,
               y25 = Y_sim25$Y$V1)

Cov = data.table(C1 = rnorm(n, sd = 2), C2 = rnorm(n, sd = 2))
beta = c(1.2, 1.5)
Xb = as.matrix(Cov) %*% as.matrix(beta)
for (i in 1:3) {
  Y[,i] = Y[,..i] + Xb + rnorm(n, sd = .5)
  Y[sample(n, 3),i] = NA
}

fwrite(x = data.table(GD), file = "demo.dat", quote = F, sep = "\t",
      row.names = F, col.names = F)
fwrite(x = Y, file = "demo.txt", quote = F, sep = "\t",
      row.names = F, col.names = T)
fwrite(x = Cov, file = "demo.cov", quote = F, sep = "\t",
      row.names = F, col.names = T)
