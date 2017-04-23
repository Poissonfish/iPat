setwd("~/all demofile")
library(MASS)
library(multtest)
library(gplots)
library(compiler)
library(scatterplot3d)
source('http://www.zzlab.net/GAPIT/emma.txt')
source('http://www.zzlab.net/GAPIT/gapit_functions.txt')
myY  <- read.table('mdp_traits.txt', head = TRUE)
myG <- read.delim('mdp_genotype_test.hmp.txt', head = FALSE)

count = 0
time = 0
while(count<51){
  ptm= proc.time()
  myGAPIT <- GAPIT(Y=myY,	G=myG, PCA.total=3)
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

time_plot = data.frame(x= 1:51, y= data) 
write.csv(x = time_plot, file = "r_run.csv")


## plot
library(ggplot2)
data <- read.csv("plot_data.csv")
new_data <- data[!data$time%in%boxplot.stats(data$time)$out,] # clean outliers
levels(new_data$env)= c('iPat', 'Original tools')
names(new_data) = c('N','N2', 'Platform', 'Runtime' )
  
ggplot(data = new_data, aes(x=Platform, fill=Platform, y=Runtime))+
  geom_boxplot(outlier.colour = 'NA')+
  scale_y_continuous(limits=c(550, 570))+ theme(legend.justification = c(1, 1), legend.position = c(1, 1))





