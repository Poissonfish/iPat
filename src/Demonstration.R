library(ggplot2)
library(magrittr)
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  library(grid)
  
  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)
  
  numPlots = length(plots)
  
  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                     ncol = cols, nrow = ceiling(numPlots/cols))
  }
  
  if (numPlots==1) {
    print(plots[[1]])
    
  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))
    
    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))
      
      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}
n=10

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
    geom_point(data=dotset, aes(x=x.d, y=y.d, color=factor(l.d), fill=factor(l.d), alpha=.05))+
    scale_x_continuous(limits=c(0,100))+scale_y_continuous(limits=c(0,100))

g2=ggplot(data=data, aes(x=x, y=y, color=line))+
  geom_point()+
  geom_line(aes(group=line))+
  scale_x_continuous(limits=c(0,100))+scale_y_continuous(limits=c(0,100))

g1
g2
multiplot(g1,g2, cols=2)



data2=data.frame(x=(1:10000)/1000, y=(1:10000)/1000)
data2=rbind(data2,data2%>%round()) %>%
  data.frame(., group=gl(2,10000), line=gl(10000,1)%>%rep(2))

ggplot(data=data2, aes(x=x, y=y, color=group))+geom_point()+geom_line(aes(group=line, colour='Black'))
scale_x_continuous(limits=c(0,10))+scale_y_continuous(limits=c(0,10))
  
