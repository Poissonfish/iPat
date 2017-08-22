  list.of.packages <- c("MASS", "data.table", "magrittr", "gplots", "compiler", "scatterplot3d", "R.utils")
  new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
  if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
  if(!'multtest'%in% installed.packages()[,"Package"]){
    source("http://www.bioconductor.org/biocLite.R") 
    biocLite("multtest")
    print("install done!!")
  }
print("done runing installation")