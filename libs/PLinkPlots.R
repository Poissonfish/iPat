args = commandArgs(trailingOnly=TRUE)
wd = args[1]
project = args[2]
trait = as.numeric(args[3])
lib = args[4]
# wd = "/Users/Poissonfish/Documents/PLINK/test"
# project = "Fianl"
# trait = 3
#Load required library

setwd(lib)
list.of.packages <- c("magrittr", "bigmemory", "biganalytics", "data.table","MASS", "gplots", "compiler", "scatterplot3d", "R.utils")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
library(magrittr)
library(bigmemory)
library(biganalytics)
library(compiler) #this library is already installed in R 
library(data.table)
library(MASS) # required for ginv
library(multtest)
library(gplots)
library(scatterplot3d)
library(R.utils)
source("./*Function_EMMA.R")
source("./*Function_GAPIT.R")
source("./*Function_FarmCPU.R")
source("./*Function_LDRemove.R")

setwd(wd)
tryCatch(
  {
    for (t in 1:trait){
      #Loading data
      print(paste("Plotting trait ", t))
      cat("Loading data...")
      data = fread(paste0(project, ".P",t,".qassoc"),header=T)
      cat("Finish loading", sep = '\n')
      data = na.omit(data) %>% (function(x){x[order(x$CHR,x$BP),c("CHR", "BP", "P")]}) %>% as.data.frame
      GAPIT.Manhattan(GI.MP= data, name.of.trait = sprintf("Trait_%d", t))
      GAPIT.QQ(data$P, name.of.trait = sprintf("Trait_%d", t))
    }
  },error = function(e){
    print(e)
  }
)