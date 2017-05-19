# Input arguments
args = commandArgs(trailingOnly=TRUE)
G.path = args[1]
Y.path = args[2]
wd = args[3]
lib = args[4]
format = args[5]
arg_length = 5
# G.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.hmp"
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/demo_data/Hapmap/data.txt"
# wd = "/Users/Poissonfish/Desktop/test/rr"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# format = "Hapmap"
# args = c(1,2,3,4,5,2,1)

tryCatch({
  # Load libraries
  list.of.packages = c("data.table", "magrittr", "rrBLUP")
  new.packages <- list.of.packages[!(list.of.packages%in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  library(rrBLUP)
  library(data.table)
  library(magrittr)

  setwd(wd)
  # Read traits
  Y = fread(Y.path) %>% as.data.frame
  # First col can't contain inidvidual name, extract taxa names
  if(is.character(Y[,1])){
        taxa = Y[,1]
        Y = Y[,-1]
  }else{
        taxa = 1:ncol(Y)
  } 
  # Select Phenotype
  trait = c()
  if(length(args)>arg_length){
    for (i in (arg_length+1):length(args)){
      trait = c(trait, as.numeric(args[i])) 
    }
    if(length(trait) == 1){
      trait_names = names(Y)[trait]
      Y = Y[,trait] %>% as.matrix(ncol = 1)
    }else{
      Y = Y[,trait] 
      trait_names = names(Y)
    }
  }

  # Format-free
  switch(format, 
    Hapmap = {sprintf("chmod 777 %s/blink", lib) %>% system()
              hmp = substring(G.path, 1, nchar(G.path)-4)
              sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
              sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
              G = read.table(sprintf("%s.dat", hmp)) %>% t()}, 
    VCF = { sprintf("chmod 777 %s/blink", lib) %>% system()
            vcf = substring(G.path, 1, nchar(G.path)-4)
            sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
            G = read.table(sprintf("%s.dat", vcf)) %>% t()},
    PLink_ASCII = {
    },
    PLink_Binary = {
    },{
      # Numeric (Default)
      G = fread(G.path) %>% as.data.frame
    }
  )

  # Remove first column if it is taxa name
  if(is.character(G[,1])){
    G = G[,-1]
  }

  # rrBLUP
  if(ncol(Y) == 1){
    ans = mixed.solve(Y, K = A.mat(G))
    write.table(data.frame(Taxa = taxa, u = ans$u), 
                sprintf("rrBLUP_out_%s.txt", trait_names),
                row.names = F,
                quote = F, 
                sep = '\t')
  }else{
    for(i in 1:ncol(Y)){
      ans = mixed.solve(Y[,i], K = A.mat(G))
      write.table(data.frame(Taxa = taxa, u = ans$u), 
                  sprintf("rrBLUP_out_%s.txt", trait_names[i]),
                  row.names = F,
                  quote = F, 
                  sep = '\t')
    }
  }
  print(warnings())
}, error = function(e){
    stop(e)
})
