required_pkg = c("MASS", "LDheatmap", "genetics", "EMMREML", "biganalytics",
						"ape", "bigmemory", "gplots", "compiler", "scatterplot3d",
						"R.utils", "data.table", "magrittr", "ggplot2", "rrBLUP", "BGLR")
missing_pkg = required_pkg[!(required_pkg %in% installed.packages()[,"Package"])]
if(length(missing_pkg)) install.packages(missing_pkg, repos="http://cran.rstudio.com/")
if(!'multtest'%in% installed.packages()[,"Package"]){
	source("http://www.bioconductor.org/biocLite.R")
	biocLite("multtest")
	biocLite("snpStats")
}
