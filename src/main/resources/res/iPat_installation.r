required_pkg = c("ape", "BGLR", "bigmemory", "biganalytics",
                 "data.table", "EMMREML", "genetics", "ggplot2", "gplots",
                 "LDheatmap", "magrittr", "MASS", "R.utils", "rrBLUP",
                 "scatterplot3d"
                )
missing_pkg = required_pkg[!(required_pkg %in% installed.packages()[,"Package"])]
if(length(missing_pkg)) 
    install.packages(missing_pkg, repos="http://cran.rstudio.com/")

# Bioc
tryCatch({
    # for R 3.5 and above
    if (!requireNamespace("BiocManager", quietly = TRUE))
        install.packages("BiocManager")
    BiocManager::install("multtest")
    BiocManager::install("snpStats")
}, error = function(e) {
    # for earlier version of R (< 3.5)
    source("http://www.bioconductor.org/biocLite.R")
	biocLite("multtest")
	biocLite("snpStats")  
})

# RiPat
devtools::install_url("http://zzlab.net/iPat/RiPat-master.zip")
