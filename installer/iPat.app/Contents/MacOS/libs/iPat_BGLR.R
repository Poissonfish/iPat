# Input arguments
	args = commandArgs(trailingOnly=TRUE)
# Common args
	project = args[1]
	wd = args[2]
	lib = args[3]
	#format = args[4]
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
	gwas.assist = as.logical(args[21])
  	cutoff = as.numeric(args[22])
  	#gwas.method = args[23] 

# Load libraries
  	cat("=== BGLR === \n")
	cat("   Loading libraries ...")
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
  	source("./Function_iPat.R")
	source("./Function_GAPIT.R")
	source("./Function_FarmCPU.R")
    cat("Done\n")

tryCatch({
	setwd(wd)
	# Subset Phenotype
  	cat("   Loading phenotype ...")
	  Y.data = fread(Y.path) %>% as.data.frame
	  if(toupper(names(Y.data)[1]) == "FID") {Y.data = Y.data[,-1]}
	  subset = Y.index %>% strsplit(split = "sep") %>% do.call(c, .)
	  index.trait = which(subset == "Selected") 
	  if(length(index.trait) == 1){
	    Y = data.frame(y = Y.data[, index.trait + 1])
	    names(Y) = names(Y.data)[1 + index.trait] 
	  }else{
	    Y = Y.data[, index.trait + 1]
	  }
	# Assign Variables
	taxa = Y.data[,1]
	trait.names = names(Y)
    cat("Done\n")
    # Genotype
    cat("   Loading genotype ...")
    GD = fread(GD.path) %>% as.data.frame()
    GM = fread(GM.path) %>% as.data.frame()
    if(is.character(GD[,1])) GD = GD[,-1]
    cat("Done\n")
	# QC
    cat("   Quality control ...")
	  # Missing rate
		if(!is.na(ms)){
	      	MS = is.na(GD) %>% apply(2, function(x) sum(x)/length(x))
	      	GD = GD[, MS <= ms]
	      	GM = GM[MS <= ms, ]
	    }
      # MAF
	    if(!is.na(maf)){
	    	GD_temp = GD
      		GD_temp[is.na(GD)] = 1
      		MAF = apply(GD_temp, 2, mean) %>% 
            	  as.matrix() %>% 
            	  apply(1, function(x) min(1 - x/2, x/2))
      		GD = GD[, MAF >= maf]
      		GM = GM[MAF >= maf, ]
	    }
	  # No NA allowed in BGLR
      	GD[is.na(GD)] = 1
    cat("Done\n")
	# BGLR
	for (i in 1:length(trait.names)){
		# Define ETA in BGLR
		ETA = list(list(X = GD, model = model))
			## Covariates
			if(C.path != "NA"){
			    cat("   Loading covariates ...")
				C = fread(C.path) %>% as.data.frame()
				C.model.name = C.index %>% strsplit(split = "sep") %>% do.call(c, .)
				for (j in 1:ncol(C)){
					if(C.model.name[j] != "OMIT IT"){
						length(ETA) = length(ETA) + 1
						ETA[[length(ETA)]] = list(X = C[,j], model = C.model.name[j])
					}
				}
			    cat("Done\n")
			}
			## Kinship
			if(K.path != "NA"){
		      	cat("   Loading Kinship ...")
				K = fread(K.path) %>% as.data.frame()
				length(ETA) = length(ETA) + 1
				ETA[[length(ETA)]] = list(K = K, model = "RKHS")
			    cat("Done\n")
			}
			## GWAS_assist
			if(gwas.assist){
      			cat("   Loading QTNs information ...")
				## Read GWAS result
			    gwas = fread(sprintf("%s_%s_GWAS.txt", project, trait.names[i]))
			    ## Merge GM and p-value
			    names(GM)[1] = "SNP"
			    map_gwas = data.frame(GM, P.value = gwas$P.value[match(GM$SNP, gwas$SNP)])
			    map_gwas$P.value[is.na(map_gwas$P.value)] = 1
			    ## Order p-value
			    snp_order = order(map_gwas$P.value) 
			    map_gwas = map_gwas[snp_order,]
			    GD = GD[,snp_order]
			    ## Find QTNs
			    index.sig = which(map_gwas$P.value < (cutoff/nrow(gwas)))
			    ## Generate dataframe by number of QTNs
				if(length(index.sig) == 1){
			        C.gwas = data.frame(m = GD[,index.sig])
			        length(ETA) = length(ETA) + 1
					ETA[[length(ETA)]] = list(X = C.gwas, model = "FIXED")
			    ### 1+ QTNs
			    }else if(length(index.sig) > 1){
			    	## LD Remove
			    	C.gwas = data.frame(GD[,index.sig])
       				LD_remain = Blink.LDRemove(C.gwas, .7, index.sig, orientation = "col")
        			C.gwas = C.gwas[,LD_remain]
        			length(ETA) = length(ETA) + 1
					ETA[[length(ETA)]] = list(X = C.gwas, model = "FIXED")
			    }
    			cat("Done\n")
			}
	   	# run BGLR
		cat(sprintf("   BGLR is computing for trait %s ...", trait.names[i]))
		blr = BGLR(y = Y[,i], ETA = ETA, response_type = response, 
				   nIter = nIter, burnIn = burnIn, thin = thin, verbose = FALSE,
				   saveAt = sprintf("BGLR_%s_%s_", project, trait.names[i])) 
	    cat("Done\n")
	}
	print(warnings())
}, error = function(e){
	stop(e)	
})

project="Project_1"
wd="/Users/Poissonfish/Desktop/test/farm"
lib="/Users/Poissonfish/git/iPat/libs/"
format="PLINK"
ms=as.numeric("No_threshold")
maf=as.numeric("0.05")
Y.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/PLINK/simb.txt"
Y.index="ExcludedsepExcludedsepSelectedsep"
GD.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/PLINK/sim_recode.dat"
GM.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/PLINK/sim_recode.nmap"
C.path="/Users/Poissonfish/Dropbox/MeetingSlides/iPat/Demo_data/covariates.txt"
C.index="NA"
K.path="NA"
FAM.path="NA"
BIM.path="NA"
model = "BRR"
response = "gaussian"
nIter = 1200 
burnIn = 200
thin = 5
gwas.assist = as.logical("TRUE") 
cutoff = .05 


# ETA<-list(list(K=KI,model='RKHS')) 
# fm<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm$yHat[sam], Y.valid)
# ETA = list(list(K = KI, model = 'RKHS'), list(X=G, model='BL'))
# fm2<-BGLR(y= Y.train, ETA=ETA,nIter=12000, burnIn=2000,saveAt='RKHS_h=0.5_', verbose = F)
# cor(fm2$yHat[sam], Y.valid)
