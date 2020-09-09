# imports
library(ape)
library(BGLR)
library(biganalytics)
library(bigmemory)
library(compiler) #this library is already installed in R
library(data.table)
library(devtools)
library(EMMREML)
library(genetics)
library(ggplot2)
library(gplots)
library(LDheatmap)
library(magrittr)
library(MASS) # required for ginv
library(multtest)
library(R.utils)
library(rrBLUP)
library(scatterplot3d)
source("http://zzlab.net/FarmCPU/FarmCPU_functions.txt")
source("http://zzlab.net/GAPIT/emma.txt")
source("http://zzlab.net/GAPIT/gapit_functions.txt")
source("http://zzlab.net/iPat/Function_iPat.R")
devtools::install_github("Poissonfish/RiPat")

source("/Users/jameschen/Dropbox/RiPat/R/model.R")
source("/Users/jameschen/Dropbox/RiPat/R/main.r")
source("/Users/jameschen/Dropbox/RiPat/R/plotting.r")


args = c(
"PLINK",
"-arg", 
"0.95", 
"GLM", 
"/Users/jameschen/IdeaProjects/iPat/target/res/plink",
"-cSelect", 
"NA", 
"-wd", 
"/Users/jameschen", 
"-project", 
"PLINKppp", 
'-phenotype', 
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt", 
"-pSelect", 
"y25sepy50sepy75sep", 
"-cov",  
"NA", 
"-kin", 
"NA", 
"-maf",
"0.05",
"-ms",
"0.05",
"-genotype", 
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.ped", 
"-map",
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.map")

# library(RiPat)
runArgs(args)


args = c(
"FarmCPU",
"-arg", 
"optimum", 
"10", 
"-cSelect", 
"NA", 
"-wd", 
"/Users/jameschen", 
"-project", 
"Project", 
'-phenotype', 
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt", 
"-pSelect", 
"y25sepy50sepy75sep", 
"-cov", 
"NA", 
"-kin", 
"NA", 
"-genotype", 
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat", 
"-map",
"/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap")

# args = c(
# "BGLR",
# "-arg", 
# "BRR", 
# "5000",
# "3000",
# "-gs",
# "FALSE",
# "3", "10",
# "-gwas", 
# "FALSE", 
# "-cSelect", 
# "C1sepC2sep", 
# "-wd", 
# "/Users/jameschen", 
# "-project", 
# "BGRRRR", 
# '-phenotype', 
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt", 
# "-pSelect", 
# "y25sepy50sepy75sep", 
# "-cov", 
# "/Users/jameschen/Dropbox/iPat/demo/demo.cov", 
# "-kin", 
# "NA", 
# "-genotype", 
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat", 
# "-map",
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap")


# args = c(
# "rrBLUP",
# "-gs",
# "TRUE",
# "3", "10",
# "-gwas", 
# "FALSE", 
# "-cSelect", 
# "C1sepC2sep", 
# "-wd", 
# "/Users/jameschen", 
# "-project", 
# "woCV", 
# '-phenotype', 
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt", 
# "-pSelect", 
# "y25sepy50sepy75sep", 
# "-cov", 
# "/Users/jameschen/Dropbox/iPat/demo/demo.cov", 
# "-kin", 
# "NA", 
# "-genotype", 
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat", 
# "-map",
# "/Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap")
