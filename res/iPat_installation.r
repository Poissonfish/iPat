required_pkg = c("MASS", "data.table", "magrittr", "gplots", "compiler", "scatterplot3d", "R.utils", "snpMatrix", "rrBLUP", "BGLR")
missing_pkg = required_pkg[!(required_pkg %in% installed.packages()[,"Package"])]
if(length(missing_pkg)) install.packages(missing_pkg, repos="http://cran.rstudio.com/")
if(!'multtest'%in% installed.packages()[,"Package"]){
	source("http://www.bioconductor.org/biocLite.R") 
	biocLite("multtest")
}