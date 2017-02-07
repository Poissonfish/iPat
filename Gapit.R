setwd("~/all_demofile")
library(MASS)
library(multtest)
library(gplots)
library(compiler)
library(scatterplot3d)
source('http://www.zzlab.net/GAPIT/emma.txt')
source('http://www.zzlab.net/GAPIT/gapit_functions.txt')
myY  <- read.table('G.txt', head = FALSE)
head(myY)
G  <- read.delim('jeff_G.txt', head = FALSE)
head(G)

count = 0
time = 0
while(count<51){
  ptm= proc.time()
  myGAPIT <- GAPIT(Y=myY,	G=myG, PCA.total=3,KI=NULL)
  x= proc.time() - ptm 
  time = c(time, x)
  count = count + 1
}
i = 1
data = 0
while((5*i-1)<length(time)){
  data = c(data,time[5*i-1])
  i = i+1
}
length(data)
library(ggplot2)
data= data[-1]
time_plot = data.frame(x= 1:51, y= data) 
#ggplot(data = time_plot, aes(x=x, y=y))+geom_point()+geom_line(aes(group= 1))
write.csv(x = time_plot, file = "r_run.csv")

GD= read.delim('GD.txt', head=FALSE)
G= read.delim('G.txt', head=FALSE)
