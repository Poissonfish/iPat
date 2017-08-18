# Input arguments
args = commandArgs(trailingOnly=TRUE)
GM.path = args[1]
GD.path = args[2]
W = as.numeric(args[3])
project = args[4]
wd = args[5]
lib = args[6]
arg_length = 6

# #GM.path= "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/BSA/data.map"
# GD.path= "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/BSA/data.bsa"
# wd="/Users/Poissonfish/Desktop/test/seg"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# W = 50000
# project = "project 1"

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

  setwd(wd)
  # Load Data
  print("Loading data")
  GM = fread(GM.path, head=TRUE); names(GM)[1] = "SNP"; setkey(GM, SNP)
  GD = fread(GD.path, head=TRUE); names(GD)[1] = "SNP"; setkey(GD, SNP)
  data = merge(GM, GD)
  data = data[order(Chromosome, Position)]
  m = nrow(data)
  adjust = 1e-10
  # Compute G 
  print("Computing G Statistics")
  data[, sum := LowA + LowB + HighA + HighB]
  data[, LA.hat := ((LowA+LowB)*(LowA+HighA)/sum)+adjust]
  data[, LB.hat := ((LowB+LowA)*(LowB+HighB)/sum)+adjust]
  data[, HA.hat := ((HighA+HighB)*(HighA+LowA)/sum)+adjust]
  data[, HB.hat := ((HighB+HighA)*(HighB+LowB)/sum)+adjust]
  data[, G_Stat := 2*(LowA*log((LowA/LA.hat)+adjust) +
                      LowB*log((LowB/LB.hat)+adjust) +
                      HighA*log((HighA/HA.hat)+adjust) +
                      HighB*log((HighB/HB.hat)+adjust))]
  # Compute G'
  print("Computing G-Prime Statistics")
  gp = vector(mode = "numeric", length = m) 
  for (i in 1:m){
    focal = data[i, Position]
    chr = data[i, Chromosome]
    subset = data[Chromosome==chr & Position < focal+(W/2) & Position > focal-(W/2)]
    subset[, D := (1-(abs((focal-Position)/(W/2)))^3)^3]
    Sw = sum(subset$D)
    subset[,k := D/Sw]
    gp[i] = t(matrix(subset$k)) %*% matrix(subset$G_Stat)
  }  
  data[, G_Prime:= gp]
  data[, P:= pchisq(G_Prime, df = 3, lower.tail = F)]
  print("Plotting")
  GAPIT.Manhattan(GI.MP= data.frame(Chromosom = data$Chromosome, 
                                    Position = data$Position, 
                                    P = data$P), name.of.trait = project)
  GAPIT.QQ(data$P, name.of.trait = project)
  write.table(x = data[,c("SNP", "Chromosome", "Position", "G_Stat", "G_Prime", "P")], file = sprintf("BSA_%s.txt", project), sep = "\t", quote = F, row.names = F)
  print("Done")
  print(warnings())
},error = function(e){
    stop(e)
})