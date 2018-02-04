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
  W = as.numeric(args[16])
  pow = as.numeric(args[17])
  arg_length = 17


# project="Project_1"
# wd="/Users/Poissonfish/Dropbox/iPat/demo_data/BSA"
# lib="/Users/Poissonfish/git/iPat/res/"
# format="BSA"
# ms=as.numeric("No_threshold")
# maf=as.numeric("0.05")
# Y.path="NA"
# Y.index="Selectedsep"
# GD.path="/Users/Poissonfish/git/iPat/demo_data/BSA/data.bsa"
# GM.path="/Users/Poissonfish/git/iPat/demo_data/BSA/data.map"
# C.path="NA"
# C.index="NA"
# K.path="NA"
# FAM.path="NA"
# BIM.path="NA"
# W = 50000
# pow = 4

tryCatch({
  # Load libraries
  print("Initializing iPat")
  setwd(lib)
  list.of.packages <- c("data.table", "magrittr", "bigmemory", "biganalytics", "MASS", "gplots", "compiler", "scatterplot3d", "R.utils", "snpMatrix")
  new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(data.table)
  library(magrittr)
  library(bigmemory)
  library(biganalytics)
  library(compiler) #this library is already installed in R 
  library(MASS) # required for ginv
  library(multtest)
  library(gplots)
  library(scatterplot3d)
  library(R.utils)
  source("./Function_GAPIT.R")
  source("./Function_BSA.R")
  setwd(wd)
  # Load Data
  print("Loading data")
  GM = fread(GM.path, head=TRUE); names(GM)[1] = "SNP"; setkey(GM, SNP)
  GD = fread(GD.path, head=TRUE); names(GD)[1] = "SNP"; setkey(GD, SNP)
  data = merge(GM, GD)
  data = data[order(Chromosome, Position)]
  m = nrow(data)
  
  # SNP index
  print("Computing SNP index")
  data[, LA.frq := (LowA) / (LowA + LowB)]
  data[, HA.frq := (HighA) / (HighA + HighB)]
  data[, snp.index := LA.frq - HA.frq]
  print("Plotting for SNP index")
  bsa.manhattan(GI.MP = data.frame(Chromosom = data$Chromosome, 
                                    Position = data$Position, 
                                    P = data$snp.index), 
                  name.of.trait = paste0(project, ".SNPindex"), seqQTN=)
  GAPIT.QQ(data$snp.index,
                  name.of.trait = paste0(project, ".SNPindex"))

  # Compute G 
  print("Computing G Statistic")
  adjust = 1e-10
  data[, sum := LowA + LowB + HighA + HighB]
  data[, LA.hat := ((LowA  + LowB)  * (LowA  + HighA) / sum) + adjust]
  data[, LB.hat := ((LowB  + LowA)  * (LowB  + HighB) / sum) + adjust]
  data[, HA.hat := ((HighA + HighB) * (HighA + LowA)  / sum) + adjust]
  data[, HB.hat := ((HighB + HighA) * (HighB + LowB)  / sum) + adjust]
  data[, G_Stat := 2 * (LowA  * log((LowA  / LA.hat) + adjust) +
                        LowB  * log((LowB  / LB.hat) + adjust) +
                        HighA * log((HighA / HA.hat) + adjust) +
                        HighB * log((HighB / HB.hat) + adjust))]
  # Compute G'
  print("Computing G-Prime Statistic")
  gp = vector(mode = "numeric", length = m) 
  for (i in 1 : m) {
    focal = data[i, Position]
    chr = data[i, Chromosome]
    subset = data[Chromosome == chr & Position < focal + (W / 2) & Position > focal - (W / 2)]
    subset[, D := (1 - (abs((focal - Position) / (W / 2))) ^ 3) ^ 3]
    Sw = sum(subset$D)
    subset[,k := D / Sw]
    gp[i] = t(matrix(subset$k)) %*% matrix(subset$G_Stat)
  }  
  data[, G_Prime:= gp]
  data[, G_Prime.P:= pchisq(G_Prime, df = 3, lower.tail = F)]
  print("Plotting for G-Prime")
  bsa.manhattan(GI.MP = data.frame(Chromosom = data$Chromosome, 
                                    Position = data$Position, 
                                    P = data$G_Prime.P),
                  name.of.trait = paste0(project, ".G"))
  GAPIT.QQ(data$G_Prime.P, 
                  name.of.trait = paste0(project, ".G"))

  # Eucledean Distance
  print("Computing Eucleadean Distance (ED)")
  data[, ED := sqrt((HA.frq - LA.frq) ^ 2 + ((1 - HA.frq) - (1 - LA.frq)) ^ 2)]
  data[, ED.pow := ED ^ pow]
  print("Plotting for ED")
  bsa.manhattan(GI.MP = data.frame(Chromosom = data$Chromosome, 
                                    Position = data$Position, 
                                    P = data$ED.pow), 
                  name.of.trait = paste0(project, ".ED_Pow"))
  GAPIT.QQ(data$ED.pow, 
                  name.of.trait = paste0(project, ".ED_Pow"))

  write.table(x    = data[,c("SNP", "Chromosome", "Position", "snp.index", "G_Stat", "G_Prime", "G_Prime.P", "ED", "ED.pow")], 
              file = sprintf("BSA_%s.txt", project), sep = "\t", quote = F, row.names = F)
  print("Done")
  print(warnings())
}, error = function(e) {
    stop(e)
})