args = commandArgs(trailingOnly=TRUE)
wd = args[1]
project = args[2]
trait = as.numeric(args[3])
Y.path = args[4]
GD = args[5]
lib = args[6]
# wd = "/Users/Poissonfish/Desktop/test/plink"
# project = "Project 1"
# trait = 3
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/PLINK_Binary/simb.txt"
# GD = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/PLINK_Binary/simb.bed"
# lib = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/libs"

setwd(lib)
list.of.packages <- c("magrittr", "bigmemory", "biganalytics", "data.table","MASS", "gplots", "compiler", "scatterplot3d", "R.utils", "snpMatrix")
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
      data = fread(paste0(project, ".P",t,".qassoc"),header=T)
      data = na.omit(data) %>% (function(x){x[order(x$CHR,x$BP),c("CHR", "SNP", "BP", "P")]}) %>% as.data.frame
      GAPIT.Manhattan(GI.MP= data[,c(1,3,4)], name.of.trait = sprintf("Trait_%d", t))
      GAPIT.QQ(data$P, name.of.trait = sprintf("Trait_%d", t))
    }

    #BED < 5M
    #PED < 100M
    if(!file.info(GD)$size > 5e+6){
      bed = substr(GD, 1, nchar(GD)-4)
      #plink --bfile filename --recode --tab --out myfavpedfile
      sprintf("%s/plink --bfile %s --recode --tab --out %s", lib, bed, bed)%>% system()
    }
    # Format conversion PED -> numeric
    ped = fread(sprintf("%s.ped", bed))
    ped = as.matrix(ped[,-(1:6)]) 
    map = read.table(sprintf("%s.map", bed))
    GM = map[,c(2,1,4)]
    gd = matrix(ncol = ncol(ped), nrow = nrow(ped))
    for(j in 1 : ncol(ped)){
      message(round(j*100/ncol(ped), 1), "%")
      first_code = strsplit(x = ped[1,j], split = " ")[[1]][1] #coded as 0
      for(i in 1 : nrow(ped)){
        code = strsplit(x = ped[i,j], split = " ")
        code1 = code[[1]][1]
        code2 = code[[1]][2]
        if(code1==code2){
            gd[i, j] = ifelse(code1 == first_code, 0, 2)
        }else{
            gd[i, j] = 1
        }
      }
    }
    GD = data.frame(gd)
    names(GD) = data$SNP
    #Sorted by P-value
    SNP_p = subset(data, select = c("SNP", "P"))
    names(SNP_p) = c("name", "p")
    SNP_p = SNP_p[order(SNP_p$p),]
    #Select associated SNP
    sig = SNP_p$p < (.05)#/nrow(SNP_p) #length(SNPname)
    GD_sig = as.matrix(GD[,SNP_p$name[sig]])
    # if(ncol(GD_sig)==0){  # No QTN detected
    #   CV = ifelse(is.null(ncol(C)), NULL, cbind(Y[,1], C))
    # }else(is.null(ncol(GD_sig))){ # only 1 QTN detected
    #   CV = ifelse(is.null(ncol(C)), cbind(Y[,1],GD_sig), cbind(Y[,1], GD_sig, C))
    #   C.inher = ifelse(length(C.inher)==0, NULL, C.inher + 1)
    # }else{
    #   #LD Remove
    #   LD_remain = Blink.LDRemove(GD_sig, .7, 1:sum(sig), orientation = "col")
    #   GD_sig = GD_sig[,LD_remain] 
    #   #Check number
    #   if(is.null(ncol(C)) && nrow(Y) < ncol(GD_sig)){ # no C provided  
    #     redundant = ncol(GD_sig) - nrow(Y)
    #     GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
    #   }else if(!is.null(ncol(C)) && nrow(Y) < (ncol(GD_sig) + ncol(C))){ # C provided
    #     redundant = ncol(GD_sig) + ncol(C) - nrow(Y)
    #     GD_sig = GD_sig[,1:(ncol(GD_sig) - redundant)]
    #     C.inher = ifelse(length(C.inher)==0, NULL, C.inher + ncol(GD_sig))
    #   }
    #   CV = ifelse(is.null(ncol(C)), cbind(Y[,1],GD_sig), cbind(Y[,1], GD_sig, C))
    # }
    
    #GAPIT do prediction
    Y = read.table(Y.path, head = T)
    GD = data.frame(taxa = Y[,1],GD)

    GD_sig = as.data.frame(GD_sig)
    for(t in 1:trait){
      pred <- GAPIT(  
      Y = Y[,c(1,2+t)],
      GM = GM,
      GD = GD,
      PCA.total=3,
      #CV = GD_sig,
      group.from=10000,
      group.to=10000,
      group.by=10,
      SNP.test=FALSE,
      memo= sprintf("Trait_%d", t)
      )
    } 

# ./blink --file mydata --compress --out mydata --plink
# ./blink --file mydata --recode --out num --numeric

# ./plink -bfile mydata -assoc -allow-no-sex -pheno bb.txt -all-pheno -adjust -ci 0.95 -out output

# ./plink -bfile simb -assoc -allow-no-sex -pheno simb.txt -all-pheno -adjust -ci 0.95 -out output
# ./blink --file simb --compress --out simb --plink
  },error = function(e){
    print(e)
  }
)