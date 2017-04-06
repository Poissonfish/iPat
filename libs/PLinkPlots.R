args = commandArgs(trailingOnly=TRUE)
#Load required library
list.of.packages <- c("ggplot2", "RColorBrewer", "data.table", "dplyr", "magrittr")
new.packages <- list.of.packages[!(list.of.packages %in% installed.packages()[,"Package"])]
if(length(new.packages)) install.packages(new.packages, repos="http://cran.rstudio.com/")
library(ggplot2)
library(RColorBrewer)
library(data.table)
library(dplyr)
library(magrittr)
#Function definition
manhatton = function(data, cut, subtitle, ymax, filename){
  chr = unique(data$CHR)
  ticks = summarise(group_by(data, CHR), tick = median(Pos)%>%round)[[2]]
  G =
    ggplot(data, aes(x = Pos, y = P_log, colour = factor(CHR)))+
    geom_point(size = 1.5,alpha = .5)+
    geom_hline(yintercept = cut, colour = "red", size = .3, alpha=.8)+
    ggtitle("Manhattan Plot", subtitle=subtitle)+
    scale_colour_manual(values=rep(c("black", "#646D71"),times=10))+
    scale_x_continuous(name="Chromosome",
                       breaks=ticks,
                       labels=chr)+
    scale_y_continuous(name = expression(-log[10](italic(p))), 
                       limits=c(0,ymax),
                       breaks=1:ymax)+ 
    theme(legend.position="none",
          panel.background = element_blank(),
          panel.grid.minor = element_blank(),
          panel.border = element_rect(colour = "black", fill=NA, size=.5)
    )
  ggsave(G, file = filename, width = 8, height = 4, dpi = 150)
}
qq = function(data, filename){
  obs = data[["P_log"]] %>% (function(x){x[order(x,decreasing = T)]})
  m = length(obs)
  dt = data.table(obs = obs,
                  exp = -log10(1:m/m),
                  c95 = -log10(qbeta(.95, 1:m, m-(1:m)+1)),
                  c05 = -log10(qbeta(.05, 1:m, m-(1:m)+1)))
  G =
    ggplot(dt, aes(x = exp, y = obs)) +
    geom_point(size=2, alpha=.5) +
    geom_abline(intercept=0, slope=1, alpha=0.5, colour = 'red') +
    geom_line(aes(exp, c95), linetype=2, colour = 'red') +
    geom_line(aes(exp, c05), linetype=2, colour = 'red') +
    xlab(expression(Expected~~-log[10](italic(p)))) +
    ylab(expression(Observed~~-log[10](italic(p))))
  ggsave(G, file=  filename, width = 4, height = 4, dpi = 150)
}
#Load arguments
wd = args[1]
project = args[2]
trait = as.numeric(args[3])
# wd = "/Users/Poissonfish/Documents/PLINK/test"
# project = "Fianl"
# trait = 3
setwd(wd)

tryCatch(
  {
    for (t in 1:trait){
      #Loading data
      print(paste("Plotting trait ", t))
      cat("Loading data...")
      data = fread(paste0(project, ".P",t,".qassoc"),header=T)
      cat("Finish loading", sep = '\n')
      data = na.omit(data) %>% (function(x){x[order(x$CHR,x$BP),c("CHR", "BP", "P")]})
      data[,P_log := -log10(data$P)]
      data[,Pos := 1:nrow(data)]
      m = nrow(data)
      #Set boundary
      data[data$P_log > 15,'P_log'] = 15
      if(max(data$P_log)<8){
        max = 8
      }else{
        max = ceiling(max(data$P_log)+1)
      }
      # Manhattan
      cut = -log10(0.05/m) #bonferroni cutoff
      print(paste("Generate plots for trait",t))
      if(m>5e+5){
        for(i in unique(data$CHR)){
          print(sprintf("Plotting for chromosome %d", i))
          manhatton(data[CHR==i], cut, sprintf("Trait%d Chr.%d", t, i), max , sprintf("ManhattanPlot_Trait%d_Chr%d.png", t, i))
          qq(data[CHR==i], sprintf("QQPlot_Trait%d_Chr%d.png", t, i))
        }
      }else{
        manhatton(data, cut, sprintf("Trait%d", t), max , sprintf("ManhattanPlot_Trait%d.png", t))
        qq(data[CHR==i], sprintf("QQPlot_Trait_Trait%d.png", t))
      }
    }
  },error = function(e){
    print(e)
  }
)

# for(c in unique(data$CHR)){
#   print(paste("Compress data in Chromosom ",c))
#   sub = subset(data, CHR==c, select = c("CHR", "BP", "P"))
#   next_index = 1
#   start = min(sub$BP)
#   while(!is.na(start)){
#     #print(paste("Progress: ", round(start/max(sub$BP),3)*100, "% at Chr",c))
#     end_index = max(which((sub$BP<(start+ws))))
#     sub[next_index:end_index]$BP = start+ws/2
#     sub[next_index:end_index]$P = min(sub[next_index:end_index]$P)
#     next_index = min(which(sub$BP>(start+ws)))
#     start = sub$BP[next_index]
#   }
#   com = rbind(com, unique(sub[,c("CHR", "P")]))
# }  

#brewer.pal(8, "Dark2")