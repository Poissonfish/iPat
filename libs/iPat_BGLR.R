# Input arguments
args = commandArgs(trailingOnly=TRUE)
GD.path = args[1]
Y.path = args[2]
Y.index = args[3]
C.path = args[4]
C.model = args[5]
K.path = args[6]
model = args[7]
response = args[8]
iter = as.numeric(args[9])
burn = as.numeric(args[10])
thin = as.numeric(args[11])
project = args[12]
wd = args[13]
lib = args[14]
format = args[15]

# Simulation
GM.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.map"
GD.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.dat"
Y.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/Numeric/data.txt"
Y.index = "3sep2sep"
C.path = "/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/covariate.txt"
iter = 1500
burn = 200
wd = "/Users/Poissonfish/Desktop/test/bglr"
lib = "/Users/Poissonfish/git/iPat/libs/"
format = "Numeric"

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
	library(MASS)
	library(multtest)
	library(gplots)
	library(compiler)
	library(scatterplot3d)
	library(R.utils)
	source("./Function_EMMA.R")
	source("./Function_GAPIT.R")
	source("./Function_FarmCPU.R")

	setwd(wd)
	# Subset Phenotype
	Y.data = fread(Y.path) %>% as.data.frame
	subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .) %>% as.numeric
	Y = Y.data[, subset+1]

	# Assign Variables
	taxa = Y.data[,1]
	trait.names = names(Y)

	# Format-free
	OS.Windows = FALSE
  	switch(Sys.info()[['sysname']],
      Windows= {OS.Windows = TRUE}, # Windows
      Linux  = { }, # Linux
      Darwin = { }) # MacOS
	switch(format, 
	  Hapmap = {if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
	            hmp = substring(GD.path, 1, nchar(GD.path)-4)
	            sprintf("%s/blink --file %s --compress --hapmap", lib, hmp) %>% system()
	            sprintf("%s/blink --file %s --recode --out %s --numeric", lib, hmp, hmp) %>% system()
	            GD = read.table(sprintf("%s.dat", hmp)) %>% t() %>% data.frame(Y[,1], .)
	            GM = read.table(sprintf("%s.map", hmp), head = TRUE)}, 
	  VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
	          vcf = substring(GD.path, 1, nchar(GD.path)-4)
	          sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
	          sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
	          GD = read.table(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)
	          GM = read.table(sprintf("%s.map", vcf), head = TRUE)},
	  PLink_Binary = {

	  }, {
	  	# Numeric (Default)
		if(GM.path == "NULL"){GM = NULL}else{GM = fread(GM.path) %>% as.data.frame}
		if(GD.path == "NULL"){GD = NULL}else{GD = fread(GD.path) %>% as.data.frame}
	  }
	)

	# Trim data 
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


# saveAt (character)
# thin


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
