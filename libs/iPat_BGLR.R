# Input arguments
args = commandArgs(trailingOnly=TRUE)
GM.path = args[1]
GD.path = args[2]
Y.path = args[3]
C.path = args[4]
iter = as.numeric(args[5])
burn = as.numeric(args[6])
wd = args[7]
lib = args[8]
format = args[9]
arg_length = 9

# Simulation
# GM.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.map"
# GD.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.dat"
# Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.txt"
# C.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/covariate.txt"
# iter = 1500
# burn = 200
# wd = "/Users/Poissonfish/Desktop/test/bglr"
# lib = "/Users/Poissonfish/git/iPat/libs/"
# format = "Numeric"
# arg_length = 9
# args = c(1,2,3,4,5,6,7,8,9,3,1) 

tryCatch({
	# Load libraries
	setwd(lib)
	list.of.packages <- c("BGLR", "MASS", "data.table", "magrittr", "gplots", "compiler", "scatterplot3d", "R.utils")
	new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
	if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
	if(!'multtest'%in% installed.packages()[,"Package"]){
	  source("http://www.bioconductor.org/biocLite.R") 
	  biocLite("multtest")
	}
	library(BGLR)
	library(data.table)
	library(magrittr)
	library(MASS) # required for ginv
	library(multtest)
	library(gplots)
	library(compiler) #required for cmpfun
	library(scatterplot3d)
	library(R.utils)
	source("./Function_EMMA.R")
	source("./Function_GAPIT.R")
	source("./Function_FarmCPU.R")

	setwd(wd)
	# Subset Phenotype
	Y = read.table(Y.path, head=TRUE)
	trait = c()
	if(length(args) > arg_length){
	  for (i in (arg_length+1):length(args)){
	    trait = c(trait, as.numeric(args[i])) 
	  }
	  Y = Y[,c(1,trait+1)]
	}
	trait_names = names(Y)[-1]

	# Format-free
	switch(format, 
	  Hapmap = {sprintf("chmod 777 %s/blink", lib) %>% system()
	            hmp = substring(GD.path, 1, nchar(GD.path)-4)
	            sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
	            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
	            GD = read.table(sprintf("%s.dat", hmp)) %>% t() %>% data.frame(Y[,1], .)
	            GM = read.table(sprintf("%s.map", hmp), head = TRUE)}, 
	  VCF = { sprintf("chmod 777 %s/blink", lib) %>% system()
	          vcf = substring(GD.path, 1, nchar(GD.path)-4)
	          sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
	          sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
	          GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)
	          GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
	  PLink_ASCII = {

	  },
	  PLink_Binary = {

	  }, {
	  	# Numeric (Default)
		if(GM.path == "NULL"){GM = NULL}else{GM = fread(GM.path, head = TRUE)}
		if(GD.path == "NULL"){GD = NULL}else{GD = fread(GD.path, head = TRUE) %>% as.data.frame}
	  }
	)

	# Remove first column if it is taxa name
	if(is.character(GD[,1])){
	  GD = GD[,-1]
	}

	# Covariates and define ETA used in BGLR
	if(C.path == "NULL"){
		C = NULL
		ETA = list(list(X = GD, model="BL"))
	}else{
		C = fread(C.path, head = TRUE) %>% as.data.frame
		ETA = list(list(X = GD, model="BL"), list(X = C, model="FIXED"))
	}

	# BGLR
	for (i in 1:length(trait_names)){
		blr = BGLR(y = Y[,1+i], 
				   ETA = ETA, 
				   nIter = iter, burnIn = burn) 
		P = blr$ETA[[1]]$tau2
		myGI.MP=cbind(GM[,-1], 1/(exp(10000*P))) %>% as.data.frame
		GAPIT.Manhattan(GI.MP = myGI.MP, name.of.trait = sprintf("%s", trait_names[i]))
		GAPIT.QQ(P, name.of.trait = sprintf("%s", trait_names[i]))
	}
	print(warnings())
}, error = function(e){
	stop(e)	
})
