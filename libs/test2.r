args = commandArgs(trailingOnly=TRUE)
a=args[1]
b=args[2]
c=args[3]

library(ggplot2)

if(!require(Rcpp)){
  install.packages('Rcpp')
  library(Rcpp)
}

if(b){
  print('yes')
}else{
  print('no')
}