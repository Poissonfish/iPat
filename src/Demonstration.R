library(ggplot2)
library(magrittr)
n=15
x=sample(1:100, 2*n, replace=TRUE)
y=sample(1:100, 2*n, replace=TRUE)
data=data.frame(x=x, y=y, line=gl(n,2))
x.d=c()
y.d=c()
l.d=c()
for(i in 1:n){
  for(j in 1:100){
    for(k in 1:100){
      if(
        ((((j-data[data$line==i,][1,1])^2)+ ((k-data[data$line==i,][1,2])^2) )%>%sqrt())+
        ((((j-data[data$line==i,][2,1])^2)+ ((k-data[data$line==i,][2,2])^2) )%>%sqrt())<
        ((((data[data$line==i,][2,1]-data[data$line==i,][1,1])^2)+ ((data[data$line==i,][2,2]-data[data$line==i,][1,2])^2) )%>%sqrt())+.3
      ){
        x.d= c(x.d, j)
        y.d= c(y.d, k)
        l.d= c(l.d, i)
      }
    }
  }
}
dotset=data.frame(x.d, y.d, l.d)
g1=ggplot(data=data, aes(x=x, y=y, color=line))+
    geom_point()+
    geom_line(aes(group=line))+
    geom_point(data=dotset, aes(x=x.d, y=y.d, color=factor(l.d), alpha=.3))+
    scale_x_continuous(limits=c(0,100))+scale_y_continuous(limits=c(0,100))
