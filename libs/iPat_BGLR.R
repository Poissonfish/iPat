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
	model = args[16]
	response = args[17]
	nIter = as.numeric(args[18])
	burnIn = as.numeric(args[19])
	thin = as.numeric(args[20])

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
	            GD = fread(sprintf("%s.dat", hmp)) %>% t() %>% data.frame(Y[,1], .)},
	  VCF = { if(!OS.Windows){sprintf("chmod 777 %s/blink", lib) %>% system()}
	          vcf = substring(GD.path, 1, nchar(GD.path)-4)
	          sprintf("%s/blink --file %s --compress --vcf", lib, vcf) %>% system()
	          sprintf("%s/blink --file %s --recode --out %s --numeric", lib, vcf, vcf) %>% system()
	          GD = fread(sprintf("%s.dat", vcf)) %>% t() %>% data.frame(Y[,1], .)},
	  PLink_Binary = {
	  }, {
	  	# Numeric (Default)
		GD = fread(GD.path) %>% as.data.frame()
	  }
	)

	# QC

	# Trim data 
	if(is.character(GD[,1])){
	  GD = GD[,-1]
	}

	# Define ETA in BGLR
	ETA = list(list(X = GD, model = model))
		## Covariates
		if(C.path != "NA"){
			C = fread(C.path) %>% as.data.frame()
			C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
			for (i in 1:ncol(C)){
				if(C.model.name[i] != "OMIT IT"){
					length(ETA) = length(ETA) + 1
					ETA[[length(ETA)]] = list(X = C[,i], model = C.model.name[i])
				}
			}
		}
		## Kinship
		if(K.path != "NA"){
			K = fread(K.path) %>% as.data.frame()
			length(ETA) = length(ETA) + 1
			ETA[[length(ETA)]] = list(K = K, model = "RKHS")
		}

	# BGLR
	for (i in 1:length(trait.names)){
		blr = BGLR(y = Y[,i], ETA = ETA, response_type = response, 
				   nIter = nIter, burnIn = burnIn, thin = thin, saveAt = sprintf("BGLR_%s_%s_", project, trait.names[i])) 
	}
	print(warnings())
}, error = function(e){
	stop(e)	
})

# ETA<-list(list(K=KI,model='RKHS')) 
# fm<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm$yHat[sam], Y.valid)

# ETA = list(list(K = KI, model = 'RKHS'), list(X=G, model='BL'))
# fm2<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm2$yHat[sam], Y.valid)