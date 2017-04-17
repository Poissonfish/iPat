args = commandArgs(trailingOnly=TRUE)

# G.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Numeric/mdp_numeric.txt"
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Numeric/mdp_traits.txt"
# wd = "/Users/Poissonfish/Desktop/test/rr"
G.path = args[1]
Y.path = args[2]
wd = args[3]

list.of.packages = c("data.table","rrBLUP")
new.packages <- list.of.packages[!(list.of.packages%in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
library(rrBLUP)
library(data.table)

setwd(wd)
G = fread(G.path); G = as.data.frame(G)
Y = fread(Y.path); Y = as.data.frame(Y)
taxa = c()

#Remove first column if it is taxa name
if(is.character(G[,1])){
  G = G[,-1]
}
if(is.character(Y[,1])){
  taxa = Y[,1]
  name = names(Y)[-1]
  Y = Y[,-1]
}else{
  name = names(Y)
  taxa = 1:ncol(Y)
}
#Select Phenotype
trait = c()
for (i in 4:length(args)){
  trait = c(trait, as.numeric(args[i])) 
}
Y = Y[,trait]

#Genome prediction
print('rrBLUP start')
tryCatch(
  {if(is.null(ncol(Y))){
    ans <- mixed.solve(Y,K=A.mat(G))
    write.table(data.frame(Taxa = taxa, u = ans$u), 
                sprintf("rrBLUP_out_%s.txt", name[trait]),
                row.names = F,
                quote = F, 
                sep = '\t')
  }else{
    for(i in 1:ncol(Y)){
      ans <- mixed.solve(Y[,i],K=A.mat(G))
      write.table(data.frame(Taxa = taxa, u = ans$u), 
                  sprintf("rrBLUP_out_%s.txt", name[trait[i]]),
                  row.names = F,
                  quote = F, 
                  sep = '\t')
    }
  }},error = function(e){
    print(e)
  }
)
print("done")
print(warnings())
stop("")
