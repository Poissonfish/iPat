`iPat.QQ` <-
function(P.values, plot.type = "log_P_values", filename = "_", name.of.trait = "Trait", DPP = 50000, plot.style = "rainbow"){
    #Object: Make a QQ-Plot of the P-values
    #Options for plot.type = "log_P_values" and "P_values"
    #Output: A pdf of the QQ-plot
    #Authors: Alex Lipka and Zhiwu Zhang
    # Last update: May 9, 2011
    ##############################################################################################
    # Sort the data by the raw P-values
    #print("Sorting p values")
    #print(paste("Number of P values: ",length(P.values)))
    #remove NAs and keep the ones between between 0 and 1
    P.values=P.values[!is.na(P.values)]
    P.values=P.values[P.values>0]
    P.values=P.values[P.values<=1]
    
    if(length(P.values[P.values>0])<1) return(NULL)
    N=length(P.values)
    DPP=round(DPP/4) #Reduce to 1/4 for QQ plot
    P.values <- P.values[order(P.values)]
    
    #Set up the p-value quantiles
    #print("Setting p_value_quantiles...")
    p_value_quantiles <- (1:length(P.values))/(length(P.values)+1)
    
    
    if(plot.type == "log_P_values")
    {
        log.P.values <- -log10(P.values)
        log.Quantiles <- -log10(p_value_quantiles)
        
        index=GAPIT.Pruning(log.P.values,DPP=DPP)
        log.P.values=log.P.values[index ]
        log.Quantiles=log.Quantiles[index]
        
        if(plot.style=="FarmCPU"){
        pdf(paste0(filename, "_QQ-Plot.pdf"),width = 5,height=5)
        par(mar = c(5,6,5,3))
        }
        if(plot.style=="rainbow"){
            pdf(paste0(filename, "_QQ-Plot.pdf"), width = 5,height=5)
            par(mar = c(5,6,5,3))
        }
        #Add conficence interval
        N1=length(log.Quantiles)
        ## create the confidence intervals
        c95 <- rep(NA,N1)
        c05 <- rep(NA,N1)
        for(j in 1:N1){
            i=ceiling((10^-log.Quantiles[j])*N)
            if(i==0)i=1
            c95[j] <- qbeta(0.95,i,N-i+1)
            c05[j] <- qbeta(0.05,i,N-i+1)
            #print(c(j,i,c95[j],c05[j]))
        }
        
        #CI Lines
        #plot(log.Quantiles, -log10(c05), xlim = c(0,max(log.Quantiles)), ylim = c(0,max(log.P.values)), type="l",lty=5, axes=FALSE, xlab="", ylab="",col="black")
        #par(new=T)
        #plot(log.Quantiles, -log10(c95), xlim = c(0,max(log.Quantiles)), ylim = c(0,max(log.P.values)), type="l",lty=5, axes=FALSE, xlab="", ylab="",col="black")
        
        #CI shade
        plot(NULL, xlim = c(0,max(log.Quantiles)), ylim = c(0,max(log.P.values)), type="l",lty=5, lwd = 2, axes=FALSE, xlab="", ylab="",col="gray")
        index=length(c95):1
        polygon(c(log.Quantiles[index],log.Quantiles),c(-log10(c05)[index],-log10(c95)),col='gray',border=NA)
        
        #Diagonal line
        abline(a = 0, b = 1, col = "red",lwd=2)
        
        #data
        par(new=T)
        if(plot.style=="FarmCPU"){
            plot(log.Quantiles, log.P.values, cex.axis=1.1, cex.lab=1.3, lty = 1,  lwd = 2, col = "Black" ,bty='l', xlab =expression(Expected~~-log[10](italic(p))), ylab = expression(Observed~~-log[10](italic(p))), main = paste(name.of.trait,sep=""),pch=20)
        }
        if(plot.style=="rainbow"){
            plot(log.Quantiles, log.P.values, xlim = c(0,max(log.Quantiles)), ylim = c(0,max(log.P.values)), cex.axis=1.1, cex.lab=1.3, lty = 1,  lwd = 2, col = "Blue" ,xlab =expression(Expected~~-log[10](italic(p))),ylab = expression(Observed~~-log[10](italic(p))), main = paste(name.of.trait,sep=""))
        }
        
        dev.off()
    }
    
    
    if(plot.type == "P_values")
    {
        pdf(paste("QQ-Plot_", name.of.trait,".pdf" ,sep = ""))
        par(mar = c(5,5,5,5))
        qqplot(p_value_quantiles, P.values, xlim = c(0,1),
        ylim = c(0,1), type = "l" , xlab = "Uniform[0,1] Theoretical Quantiles", 
        lty = 1, lwd = 1, ylab = "Quantiles of P-values from GWAS", col = "Blue",
        main = paste(name.of.trait,sep=" "))
        abline(a = 0, b = 1, col = "red")
        dev.off()   
    }
    #print("GAPIT.QQ  accomplished successfully!")
}

`iPat.Manhattan` <-
function(GI.MP = NULL,GD=NULL, filename = "_", name.of.trait = "Trait",plot.type = "Genomewise",
DPP=50000,cutOff=0.01,band=5,seqQTN=NULL,plot.style="Oceanic",CG=NULL,plot.bin=10^9){
    #Object: Make a Manhattan Plot
    #Options for plot.type = "Separate_Graph_for_Each_Chromosome" and "Same_Graph_for_Each_Chromosome"
    #Output: A pdf of the Manhattan Plot
    #Authors: Alex Lipka, Zhiwu Zhang, Meng Li and Jiabo Wang
    # Last update: Oct 10, 2016
  #Add r2 between candidata SNP and other markers in on choromosome
    ##############################################################################################
    #print("Manhattan ploting...")
    
    #print(seqQTN)
    #do nothing if null input
    if(is.null(GI.MP)) return
  #if(is.null(GD)) return
    #print("Dimension of GI.MP")
    #print(dim(GI.MP))
    #print(head(GI.MP))
    #print(tail(GI.MP))
    #print(CG)
    
    #seqQTN=c(300,1000,2500)
  #Handler of lable paosition only indicated by negatie position
  position.only=F
    if(!is.null(seqQTN)){
      if(seqQTN[1]<0){
        seqQTN=-seqQTN
        position.only=T
      }
      
    }
    
    #if(is.null(GD)) print ("GD is not same dim as GM")
    
    borrowSlot=4
    GI.MP[,borrowSlot]=0 #Inicial as 0
  GI.MP[,5]=1:(nrow(GI.MP))
    if(!is.null(seqQTN))GI.MP[seqQTN,borrowSlot]=1
    
    
    GI.MP=matrix(as.numeric(as.matrix(GI.MP) ) ,nrow(GI.MP),ncol(GI.MP))
    GI.MP=GI.MP[order(GI.MP[,2]),]
    GI.MP=GI.MP[order(GI.MP[,1]),]
    #print("@@@@@")
    #print(dim(GD))
    #print(dim(GI.MP))
    if(!is.null(GD))
    {  if(ncol(GD)!=nrow(GI.MP))
    {print("GD does not mach GM in Manhattan !!!")
    return
    }}
    #print("!!")
    #GI.MP[,5]=1:(nrow(GI.MP))
  #print(head(GI.MP,20))
    #Remove all SNPs that do not have a choromosome, bp position and p value(NA)
    GI.MP <- GI.MP[!is.na(GI.MP[,1]),]
    GI.MP <- GI.MP[!is.na(GI.MP[,2]),]
    
    GI.MP <- GI.MP[!is.na(GI.MP[,3]),]
    
    #Retain SNPs that have P values between 0 and 1 (not na etc)
    GI.MP <- GI.MP[GI.MP[,3]>0,]
    GI.MP <- GI.MP[GI.MP[,3]<=1,]
    if(!is.null(GD)) GD=GD[,GI.MP[,3]<=1]
    #Remove chr 0 and 99
    GI.MP <- GI.MP[GI.MP[,1]!=0,]
    #GI.MP <- GI.MP[GI.MP[,1]!=99,]
    #print(dim(GI.MP))
    #print("Dimension of GI.MP after QC")
    #print(dim(GI.MP))
    
    numMarker=nrow(GI.MP)
    bonferroniCutOff=-log10(cutOff/numMarker)
    
    #Replace P the -log10 of the P-values
    if(!is.null(GD))
    {  if(ncol(GD)!=nrow(GI.MP))
    {print("GD does not mach GM in Manhattan !!!")
    return
    }}
    GI.MP[,3] <-  -log10(GI.MP[,3])
    index_GI=GI.MP[,3]>0
    GI.MP <- GI.MP[index_GI,]
    if(!is.null(GD)) GD=GD[,index_GI]
    
    GI.MP[,5]=1:(nrow(GI.MP))
    y.lim <- ceiling(max(GI.MP[,3]))
    chm.to.analyze <- unique(GI.MP[,1])
    #print(dim(GI.MP))
    #print(dim(GD))
    #print("name of chromosomes:")
    #print(chm.to.analyze)
    
    chm.to.analyze=chm.to.analyze[order(chm.to.analyze)]
    numCHR= length(chm.to.analyze)
    #GI.MP[,5]=1:(nrow(GI.MP))
     bin.mp=GI.MP[,1:3]
     bin.mp[,3]=0 # for r2
     bin.mp[,1]=as.numeric(as.vector(GI.MP[,2]))+as.numeric(as.vector(GI.MP[,1]))*(10^(max(GI.MP[,1])+1))
     
     
     #as.numeric(as.vector(GP[,3]))+as.numeric(as.vector(GP[,2]))*MaxBP

     bin.mp[,2]=floor(bin.mp[,1]/plot.bin)
     if(!is.null(GD)) X=GD

     #print(head(bin.mp))
        #Chromosomewise plot
    if(plot.type == "Chromosomewise"&!is.null(GD))
    {
        #print("Manhattan ploting Chromosomewise")
        GI.MP=cbind(GI.MP,bin.mp)
        pdf(paste0(filename,"_Manhattan.Plot.Chromosomewise.pdf"), width = 10)
            #par(mar = c(5,5,4,3), lab = c(8,5,7))
        layout(matrix(c(1,1,2,1,1,1,1,1,1),3,3,byrow=TRUE), c(2,1), c(1,1), TRUE)
        for(i in 1:numCHR)
        {
            #Extract SBP on this chromosome
            subset=GI.MP[GI.MP[,1]==chm.to.analyze[i],]
            # print(head(subset))
            subset[,1]=1:(nrow(subset))
            #sub.bin.mp=bin.mp[GI.MP[,1]==chm.to.analyze[i],]
            #subset=cbind(subset,sub.bin.mp)
            sig.mp=subset[subset[,3]>bonferroniCutOff,]
            sig.index=subset[,3]>bonferroniCutOff ### index of significont SNP
            
            
            num.row=nrow(sig.mp)
            if(!is.null(dim(sig.mp)))sig.mp=sig.mp[!duplicated(sig.mp[,7]),]
            num.row=nrow(sig.mp)
            if(is.null(dim(sig.mp))) num.row=1
            bin.set=NULL
            r2_color=matrix(0,nrow(subset),2)
            #r2_color
            print(paste("select ",num.row," candidate gene in ",i," chromosome ",sep="") )
            #print(sig.mp)
            if(length(unique(sig.index))==2)
            {
                for(j in 1:num.row)
                {   sig.mp=matrix(sig.mp,num.row,8)
                    
                    #print(sig.mp[j,7])
                    #print(unique(subset[,7]))
                    bin.store=subset[which(subset[,7]==sig.mp[j,7]),]
                    if(is.null(dim(bin.store)))
                      {subset[which(subset[,7]==sig.mp[j,7]),8]=1
                          next
                      }
                    bin.index=unique(bin.store[,5])
                    subGD=X[,bin.store[,5]]
                    #print(dim(bin.store))
                    if(is.null(CG))candidata=bin.store[bin.store[,3]==max(bin.store[,3]),5]
                    if(length(candidata)!=1)candidata=candidata[1]
                    
                    for (k in 1:ncol(subGD))
                    {
                        r2=cor(X[,candidata],subGD[,k])^2
                        #print(r2)
                        bin.store[k,8]=r2
                        
                    }
                    #print(bin.store)
                    #r2_storage[is.na(r2_storage)]=0
                    #print(bin.store)
                    subset[bin.store[,1],8]=bin.store[,8]
                    #print()
                }###end for each sig.mp
                #sub.bin.mp=bin.mp[subset[,3]>bonferroniCutOff,]
                #print(head(bin.set))
            
            }###end if empty of sig.mp
            #print("@@@@@@@@@@@@@@@@")
            rm(sig.mp,num.row)
            #print(head(subset))
      #print(head(subset))
      #print(dim(X))
            y.lim <- ceiling(max(subset[,3]))+1  #set upper for each chr
            if(length(subset)>3){
                x <- as.numeric(subset[,2])/10^(6)
                y <- as.numeric(subset[,3])
            }else{
                x <- as.numeric(subset[2])/10^(6)
                y <- as.numeric(subset[3])
            }
            
            ##print(paste("befor prune: chr: ",i, "length: ",length(x),"max p",max(y), "min p",min(y), "max x",max(x), "Min x",min(x)))
            n_col=10
            r2_color[,2]=subset[,8]
            do_color=colorRampPalette(c("orangeRed", "blue"))(n_col)
            #Prune most non important SNPs off the plots
            order=order(y,decreasing = TRUE)
            y=y[order]
            x=x[order]
            r2_color=r2_color[order,]
            index=GAPIT.Pruning(y,DPP=round(DPP/numCHR))
            x=x[index]
            y=y[index]
      r2_color=r2_color[index,]
            r2_color[which(r2_color[,2]<=0.2),2]=do_color[n_col]
            r2_color[which(r2_color[,2]<=0.4&r2_color[,2]>0.2),2]=do_color[n_col*0.8]
            r2_color[which(r2_color[,2]<=0.6&r2_color[,2]>0.4),2]=do_color[n_col*0.6]
            r2_color[which(r2_color[,2]<=0.8&r2_color[,2]>0.6),2]=do_color[n_col*0.4]
            r2_color[which(r2_color[,2]<=1&r2_color[,2]>0.8),2]=do_color[n_col/n_col]
            
            #print(unique(r2_color[,2]))
            
            ##print(paste("after prune: chr: ",i, "length: ",length(x),"max p",max(y), "min p",min(y), "max x",max(x), "Min x",min(x)))
      
            par(mar=c(0,0,0,0))
            par(mar=c(5,5,2,1),cex=0.8)

            plot(y~x,type="p", ylim=c(0,y.lim), xlim = c(min(x), max(x)),
      col = r2_color[,2], xlab = expression(Base~Pairs~(x10^-6)),
      ylab = "-Log Base 10 p-value", main =       paste("Chromosome",chm.to.analyze[i],sep=" "),
      cex.lab=1.6,pch=21,bg=r2_color[,2])
            
            abline(h=bonferroniCutOff,col="forestgreen")
            ##print("manhattan plot (chr) finished")
            #layout.show(nf)  
            #provcol <-c("darkblue","cyan","green3","brown1","brown1")
            #provcol <-heat.colors(50)
            #par(mar=c(0,0,0,0))
            par(mar=c(15,5,6,5),cex=0.5)
            
            barplot(matrix(rep(1,times=n_col),n_col,1),beside=T,col=do_color,border=do_color,axes=FALSE,)
        #legend(x=10,y=2,legend=expression(R^"2"),,lty=0,cex=1.3,bty="n",bg=par("bg"))
            axis(3,seq(11,1,by=-2),seq(0,1,by=0.2))

        }# end plot.type == "Chromosomewise"&!is.null(GD)
        dev.off()
    
        print("manhattan plot on chromosome finished")
    } #Chromosomewise plot
    
    
    #Genomewise plot
    if(plot.type == "Genomewise")
    {
        #print("Manhattan ploting Genomewise")
        #Set corlos for chromosomes
        #nchr=max(chm.to.analyze)
        nchr=length(chm.to.analyze)

    #Set color schem            
        ncycle=ceiling(nchr/band)
        ncolor=band*ncycle
        #palette(rainbow(ncolor+1))
        cycle1=seq(1,nchr,by= ncycle)
        thecolor=cycle1
        for(i in 2:ncycle){thecolor=c(thecolor,cycle1+(i-1))}
        col.Rainbow=rainbow(ncolor+1)[thecolor]       
        col.FarmCPU=rep(c("#CC6600","deepskyblue","orange","forestgreen","indianred3"),ceiling(numCHR/5))
        col.Rushville=rep(c("orangered","navyblue"),ceiling(numCHR/2))    
        col.Congress=rep(c("deepskyblue3","firebrick"),ceiling(numCHR/2))
        col.Ocean=rep(c("steelblue4","cyan3"),ceiling(numCHR/2))    
        col.PLINK=rep(c("gray10","gray70"),ceiling(numCHR/2))     
        col.Beach=rep(c("turquoise4","indianred3","darkolivegreen3","red","aquamarine3","darkgoldenrod"),ceiling(numCHR/5))
        #col.Oceanic=rep(c( '#EC5f67',  '#F99157',  '#FAC863',  '#99C794',  '#5FB3B3',  '#6699CC',  '#C594C5',  '#AB7967'),ceiling(numCHR/8))
        #col.Oceanic=rep(c( '#EC5f67',    '#FAC863',  '#99C794',    '#6699CC',  '#C594C5',  '#AB7967'),ceiling(numCHR/6))
        col.Oceanic=rep(c(  '#EC5f67',    '#FAC863',  '#99C794',    '#6699CC',  '#C594C5'),ceiling(numCHR/5))
        col.cougars=rep(c(  '#990000',    'dimgray'),ceiling(numCHR/2))
    
        if(plot.style=="Rainbow")plot.color= col.Rainbow
        if(plot.style =="FarmCPU")plot.color= col.Rainbow
        if(plot.style =="Rushville")plot.color= col.Rushville
        if(plot.style =="Congress")plot.color= col.Congress
        if(plot.style =="Ocean")plot.color= col.Ocean
        if(plot.style =="PLINK")plot.color= col.PLINK
        if(plot.style =="Beach")plot.color= col.Beach
        if(plot.style =="Oceanic")plot.color= col.Oceanic
        if(plot.style =="cougars")plot.color= col.cougars
    
    #FarmCPU uses filled dots
      mypch=1
      if(plot.style =="FarmCPU")mypch=20
              
        GI.MP <- GI.MP[order(GI.MP[,2]),]
        GI.MP <- GI.MP[order(GI.MP[,1]),]

        ticks=NULL
        lastbase=0
        
        #print("Manhattan data sorted")
        #print(chm.to.analyze)
        
        #change base position to accumulatives (ticks)
        for (i in chm.to.analyze)
        {
            index=(GI.MP[,1]==i)
            ticks <- c(ticks, lastbase+mean(GI.MP[index,2]))
            GI.MP[index,2]=GI.MP[index,2]+lastbase
            lastbase=max(GI.MP[index,2])
        }
        
        #print("Manhattan chr processed")
        #print(length(index))
        #print(length(ticks))
        #print((ticks))
        #print((lastbase))
        
        x0 <- as.numeric(GI.MP[,2])
        y0 <- as.numeric(GI.MP[,3])
        z0 <- as.numeric(GI.MP[,1])
        position=order(y0,decreasing = TRUE)
        index0=GAPIT.Pruning(y0[position],DPP=DPP)
        index=position[index0]
        
        x=x0[index]
        y=y0[index]
        z=z0[index]

        #Extract QTN
        QTN=GI.MP[which(GI.MP[,borrowSlot]==1),]
        
        #Draw circles with same size and different thikness
        size=1 #1
        ratio=10 #5
        base=1 #1
        themax=ceiling(max(y))
        themin=floor(min(y))
        wd=((y-themin+base)/(themax-themin+base))*size*ratio
        s=size-wd/ratio/2
        
        #print("Manhattan XY created")
       ####xiaolei update on 2016/01/09 
        if(plot.style =="FarmCPU"){
          pdf(paste0(filename,"_Manhattan.Plot.Genomewise.pdf"), width = 13, height=5.75)
        }else{
          pdf(paste0(filename,"_Manhattan.Plot.Genomewise.pdf"), width = 13, height=5.75)
        }
            par(mar = c(3,6,5,1))
          plot(y~x,xlab="",ylab=expression(-log[10](italic(p))) ,
          cex.axis=1.5, cex.lab=2, ,col=plot.color[z],axes=FALSE,type = "p",pch=mypch,lwd=wd,cex=s+.3,main = paste(name.of.trait,sep="      "),cex.main=2.5)
        
        #Label QTN positions
        if(is.vector(QTN)){
          if(position.only){abline(v=QTN[2], lty = 2, lwd=1.5, col = "grey")}else{
          points(QTN[2], QTN[3], type="p",pch=21, cex=2,lwd=1.5,col="dimgrey")
          points(QTN[2], QTN[3], type="p",pch=20, cex=1,lwd=1.5,col="dimgrey")
          }
        }else{
          if(position.only){abline(v=QTN[,2], lty = 2, lwd=1.5, col = "grey")}else{
          points(QTN[,2], QTN[,3], type="p",pch=21, cex=2,lwd=1.5,col="dimgrey")
          points(QTN[,2], QTN[,3], type="p",pch=20, cex=1,lwd=1.5,col="dimgrey")
          }
        }
        
        #Add a horizontal line for bonferroniCutOff
        abline(h=bonferroniCutOff,col="forestgreen")
       
        #Set axises
        axis(1, at=ticks,cex.axis=1.5,labels=chm.to.analyze,tick=F)
        axis(2, at=1:themax,cex.axis=1.5,labels=1:themax,tick=F)

        box()
        palette("default")
        dev.off()
        #print("Manhattan done Genomewise")
        
    } #Genomewise plot
    
    print("GAPIT.Manhattan accomplished successfully!zw")
} #end of GAPIT.Manhattan

`iPat.Genotype.View` <-function(myGD = NULL, filename = "_"){
# Object: Analysis for Genotype data:Distribution of SNP density,Accumulation,Moving Average of density,result:a pdf of the scree plot
# myG:Genotype data
# chr: chromosome value
# w1_start:Moving Average windows Start Position
# w1_end:Moving Average windows End Position
# mav1:Moving Average set value length
# Authors: You Tang and Zhiwu Zhang
# Last update: March 11, 2016 
##############################################################################################

#heterozygosity of individuals and SNPs (By Zhiwu Zhang)
  #print("Heterozygosity of individuals and SNPs (By Zhiwu Zhang)")
  X=myGD[,-1]
  H=1-abs(X-1)
  het.ind=apply(H,1,mean)
  het.snp=apply(H,2,mean)
  ylab.ind=paste("Frequency (out of ",length(het.ind)," individuals)",sep="")
  ylab.snp=paste("Frequency (out of ",length(het.snp)," markers)",sep="")
  pdf(paste0(filename, "_heterozygosity.pdf"), width = 10, height = 6)
  par(mfrow=c(1,2),mar=c(5,5,1,1)+0.1)
  hist(het.ind,col="gray", main="",ylab=ylab.ind, xlab="Heterozygosity of individuals")
  hist(het.snp,col="gray", main="",ylab=ylab.snp, xlab="Heterozygosity of markers")
  dev.off()
}


`iPat.Phenotype.View` <-function(myY = NULL, filename = "_"){
# Object: Analysis for Phenotype data:Distribution of density,Accumulation,result:a pdf of the scree plot
# myY:Phenotype data

# Authors: You Tang
# Last update: Sep 7, 2015 
############################################################################################## 
print("GAPIT.Phenotype.View in press...")
if(is.null(myY)){stop("Validation Invalid. Please select read valid Phenotype flies  !")}

y<-myY[!is.na(myY[,2]),2]
obs<-as.matrix(y)

traitname=colnames(myY)[2]

pdf(paste0(filename, "_phnotype_view.pdf"), width = 10, height = 6)
par(mar = c(5,5,5,5))

par(mfrow=c(2,2))
plot(obs,pch=1)
#hist(obs)
hist(obs,xlab="Density",main="",breaks=12, cex.axis=1,col = "gray")
boxplot(obs)
plot(ecdf(obs),col="red",bg="lightgray",xlab="Density",ylab="Accumulation",main="")

dev.off()
}

`Blink.LDRemoveDivided`<-function(GDneo=NULL,LD=NULL,Porder=NULL,bound=FALSE,model="A",orientation=NULL,l=NULL){
  #Objects: LD remove, especially length(Porder)>10000
  #Authors: Yao Zhou
  #Last update: 08/15/2016
  seqQTN=NULL
  lp=length(Porder)
  k=ceiling(lp/l)
  GDneo=as.matrix(GDneo)
  if(min(ncol(GDneo),nrow(GDneo))<201) bound=FALSE
  if(orientation=="col"){
    n=nrow(GDneo)
    if(bound){
      GDneo=GDneo[sample(n,200,replace=F),]
    }
  }else{
    n=ncol(GDneo)
    if(bound){
      GDneo=GDneo[,sample(n,200,replace=F)]
    }
    GDneo=t(GDneo)	
  }
  for(i in 1:k){
    bottom=(i-1)*l+1
    up=l*i
    if(up>lp) up = lp
    Porderb=Porder[bottom:up]
    GDneob = GDneo[,bottom:up]
    seqQTNs=Blink.LDRemoveBlock(GDneo=GDneob,LD=LD,Porder=Porderb,orientation="col",model=model)
    seqQTN=append(seqQTN,seqQTNs)
  }
  rm(GDneob,seqQTNs,Porderb)
  return(seqQTN)
}
`Blink.LDRemoveBlock`<-function(GDneo=NULL,LD=NULL,Porder=NULL,bound=FALSE,model="A",orientation=NULL){
  #`Blink.LDRemove`<-function(GDneo=NULL,LD=NULL,Porder=NULL,bound=FALSE,model="A",orientation=NULL){
  #Objects: Calculate LD and remove the correlated SNPs
  #Authors: Yao Zhou
  #Last Update:  03/03/16
  if (model=="D"){
    GDneo=1-abs(GDneo-1)
  }
  
  GDneo=as.matrix(GDneo)
  if(min(ncol(GDneo),nrow(GDneo))<201) bound=FALSE
  if(orientation=="col"){
    n=nrow(GDneo)
    if(bound){
      GDneo=GDneo[sample(n,200,replace=F),]
    }
  }else{
    n=ncol(GDneo)
    if(bound){
      GDneo=GDneo[,sample(n,200,replace=F)]
    }
    GDneo=t(GDneo)	
  }
  
  corr=cor(GDneo)	
  corr[is.na(corr)]=1
  corr[abs(corr)<=LD]=0
  corr[abs(corr)>LD]=1
  Psort=as.numeric(matrix(1,1,ncol(corr)))
  for(i in 2:ncol(corr)){
    p.a=Psort[1:(i-1)]
    p.b=as.numeric(corr[1:(i-1),i])
    index=(p.a==p.b)
    index[(p.a==0)&(p.b==0)]=FALSE
    if(sum(index)!=0) Psort[i]=0
  }
  seqQTN=Porder[Psort==1]
  return(seqQTN)	
}
`Blink.LDRemove`<-function(GDneo=NULL,LD=NULL,Porder=NULL,bound=FALSE,model="A",orientation=NULL){
  #`Blink.LDRemovebackup`<-function(GDneo=NULL,LD=NULL,Porder=NULL,bound=FALSE,model="A",orientation=NULL){
  #Objects: LD remove, especially length(Porder)>10000
  #Authors: Yao Zhou
  #Last update: 08/15/2016
  seqQTN=NULL
  is.done=FALSE
  l=1000
  lp=length(Porder)
  tt=1
  while(!is.done){
    tt = tt+1
    Pordern=Blink.LDRemoveDivided(GDneo=GDneo,LD=LD,Porder=Porder,orientation=orientation,model=model,l=lp)
    index=Porder %in% Pordern
    if(orientation=="col"){
      GDneo = GDneo[,index]
    }else{
      GDneo = GDneo[index,]
    }
    ls=length(Pordern)
    if(ls==lp) lp=l*tt
    if(ls<=lp){
      is.done=TRUE	
    }
    Porder = Pordern
  }	
  if(length(Porder) > 1){
    seqQTN=Blink.LDRemoveBlock(GDneo=GDneo,LD=LD,Porder=Porder,orientation=orientation,model=model)	
  }else{
    seqQTN = Porder
  }
  return(seqQTN)	
}
emma.kinship <- function(snps, method="additive", use="all") {
  n0 <- sum(snps==0,na.rm=TRUE)
  nh <- sum(snps==0.5,na.rm=TRUE)                                                                                         
  n1 <- sum(snps==1,na.rm=TRUE)
  nNA <- sum(is.na(snps))
  
  stopifnot(n0+nh+n1+nNA == length(snps))
  
  if ( method == "dominant" ) {
    flags <- matrix(as.double(rowMeans(snps,na.rm=TRUE) > 0.5),nrow(snps),ncol(snps))
    snps[!is.na(snps) && (snps == 0.5)] <- flags[!is.na(snps) && (snps == 0.5)]
  }
  else if ( method == "recessive" ) {
    flags <- matrix(as.double(rowMeans(snps,na.rm=TRUE) < 0.5),nrow(snps),ncol(snps))
    snps[!is.na(snps) && (snps == 0.5)] <- flags[!is.na(snps) && (snps == 0.5)]
  }
  else if ( ( method == "additive" ) && ( nh > 0 ) ) {
    dsnps <- snps
    rsnps <- snps
    flags <- matrix(as.double(rowMeans(snps,na.rm=TRUE) > 0.5),nrow(snps),ncol(snps))
    dsnps[!is.na(snps) && (snps==0.5)] <- flags[is.na(snps) && (snps==0.5)]
    flags <- matrix(as.double(rowMeans(snps,na.rm=TRUE) < 0.5),nrow(snps),ncol(snps))
    rsnps[!is.na(snps) && (snps==0.5)] <- flags[is.na(snps) && (snps==0.5)]
    snps <- rbind(dsnps,rsnps)
  }
  
  if ( use == "all" ) {
    mafs <- matrix(rowMeans(snps,na.rm=TRUE),nrow(snps),ncol(snps))
    snps[is.na(snps)] <- mafs[is.na(snps)]
  }
  else if ( use == "complete.obs" ) {
    snps <- snps[rowSums(is.na(snps))==0,]
  }
  
  n <- ncol(snps)
  K <- matrix(nrow=n,ncol=n)
  diag(K) <- 1
  
  for(i in 1:(n-1)) {
    for(j in (i+1):n) {
      x <- snps[,i]*snps[,j] + (1-snps[,i])*(1-snps[,j])
      K[i,j] <- sum(x,na.rm=TRUE)/sum(!is.na(x))
      K[j,i] <- K[i,j]
    }
  }
  return(K)
}

emma.eigen.L <- function(Z,K,complete=TRUE) {
  if ( is.null(Z) ) {
    return(emma.eigen.L.wo.Z(K))
  }
  else {
    return(emma.eigen.L.w.Z(Z,K,complete))
  }
}

emma.eigen.L.wo.Z <- function(K) {
  eig <- eigen(K,symmetric=TRUE)
  return(list(values=eig$values,vectors=eig$vectors))
}

emma.eigen.L.w.Z <- function(Z,K,complete=TRUE) {
  if ( complete == FALSE ) {
    vids <- colSums(Z)>0
    Z <- Z[,vids]
    K <- K[vids,vids]
  }
  eig <- eigen(K%*%crossprod(Z,Z),symmetric=FALSE,EISPACK=TRUE)
  return(list(values=eig$values,vectors=qr.Q(qr(Z%*%eig$vectors),complete=TRUE)))
}

emma.eigen.R <- function(Z,K,X,complete=TRUE) {
  if (is.null(Z) ) {
    return(emma.eigen.R.wo.Z(K,X))
  }
  else {
    return(emma.eigen.R.w.Z(Z,K,X,complete))
  }
}

emma.eigen.R.wo.Z <- function(K, X) {
  n <- nrow(X)
  q <- ncol(X)
  S <- diag(n)-X%*%solve(crossprod(X,X))%*%t(X)
  eig <- eigen(S%*%(K+diag(1,n))%*%S,symmetric=TRUE)
  stopifnot(!is.complex(eig$values))
  return(list(values=eig$values[1:(n-q)]-1,vectors=eig$vectors[,1:(n-q)]))
}

emma.eigen.R.w.Z <- function(Z, K, X, complete = TRUE) {
  if ( complete == FALSE ) {
    vids <-  colSums(Z) > 0
    Z <- Z[,vids]
    K <- K[vids,vids]
  }
  n <- nrow(Z)
  t <- ncol(Z)
  q <- ncol(X)
  
  
  
  SZ <- Z - X%*%solve(crossprod(X,X))%*%crossprod(X,Z)
  eig <- eigen(K%*%crossprod(Z,SZ),symmetric=FALSE,EISPACK=TRUE)
  if ( is.complex(eig$values) ) {
    eig$values <- Re(eig$values)
    eig$vectors <- Re(eig$vectors)    
  }
  qr.X <- qr.Q(qr(X))
  return(list(values=eig$values[1:(t-q)],
              vectors=qr.Q(qr(cbind(SZ%*%eig$vectors[,1:(t-q)],qr.X)),
                           complete=TRUE)[,c(1:(t-q),(t+1):n)]))   
}

emma.delta.ML.LL.wo.Z <- function(logdelta, lambda, etas, xi) {
  n <- length(xi)
  delta <- exp(logdelta)
  return( 0.5*(n*(log(n/(2*pi))-1-log(sum((etas*etas)/(lambda+delta))))-sum(log(xi+delta))) )  
}

emma.delta.ML.LL.w.Z <- function(logdelta, lambda, etas.1, xi.1, n, etas.2.sq ) {
  t <- length(xi.1)
  delta <- exp(logdelta)
  #  stopifnot(length(lambda) == length(etas.1))
  return( 0.5*(n*(log(n/(2*pi))-1-log(sum(etas.1*etas.1/(lambda+delta))+etas.2.sq/delta))-(sum(log(xi.1+delta))+(n-t)*logdelta)) )
}

emma.delta.ML.dLL.wo.Z <- function(logdelta, lambda, etas, xi) {
  n <- length(xi)
  delta <- exp(logdelta)
  etasq <- etas*etas
  ldelta <- lambda+delta
  return( 0.5*(n*sum(etasq/(ldelta*ldelta))/sum(etasq/ldelta)-sum(1/(xi+delta))) )
}

emma.delta.ML.dLL.w.Z <- function(logdelta, lambda, etas.1, xi.1, n, etas.2.sq ) {
  t <- length(xi.1)
  q <- t-length(lambda)
  delta <- exp(logdelta)
  etasq <- etas.1*etas.1
  ldelta <- lambda+delta
  return( 0.5*(n*(sum(etasq/(ldelta*ldelta))+etas.2.sq/(delta*delta))/(sum(etasq/ldelta)+etas.2.sq/delta)-(sum(1/(xi.1+delta))+(n-t)/delta) ) )
}

emma.delta.REML.LL.wo.Z <- function(logdelta, lambda, etas) {
  nq <- length(etas)
  delta <-  exp(logdelta)
  return( 0.5*(nq*(log(nq/(2*pi))-1-log(sum(etas*etas/(lambda+delta))))-sum(log(lambda+delta))) )
}

emma.delta.REML.LL.w.Z <- function(logdelta, lambda, etas.1, n, t, etas.2.sq ) {
  tq <- length(etas.1)
  nq <- n - t + tq
  delta <-  exp(logdelta)
  return( 0.5*(nq*(log(nq/(2*pi))-1-log(sum(etas.1*etas.1/(lambda+delta))+etas.2.sq/delta))-(sum(log(lambda+delta))+(n-t)*logdelta)) ) 
}

emma.delta.REML.dLL.wo.Z <- function(logdelta, lambda, etas) {
  nq <- length(etas)
  delta <- exp(logdelta)
  etasq <- etas*etas
  ldelta <- lambda+delta
  return( 0.5*(nq*sum(etasq/(ldelta*ldelta))/sum(etasq/ldelta)-sum(1/ldelta)) )
}

emma.delta.REML.dLL.w.Z <- function(logdelta, lambda, etas.1, n, t1, etas.2.sq ) {
  t <- t1
  tq <- length(etas.1)
  nq <- n - t + tq
  delta <- exp(logdelta)
  etasq <- etas.1*etas.1
  ldelta <- lambda+delta
  return( 0.5*(nq*(sum(etasq/(ldelta*ldelta))+etas.2.sq/(delta*delta))/(sum(etasq/ldelta)+etas.2.sq/delta)-(sum(1/ldelta)+(n-t)/delta)) )
}

emma.MLE <- function(y, X, K, Z=NULL, ngrids=100, llim=-10, ulim=10,
                     esp=1e-10, eig.L = NULL, eig.R = NULL)
{
  n <- length(y)
  t <- nrow(K)
  q <- ncol(X)
  
  #  stopifnot(nrow(K) == t)
  stopifnot(ncol(K) == t)
  stopifnot(nrow(X) == n)
  
  if ( det(crossprod(X,X)) == 0 ) {
    warning("X is singular")
    return (list(ML=0,delta=0,ve=0,vg=0))
  }
  
  if ( is.null(Z) ) {
    if ( is.null(eig.L) ) {
      eig.L <- emma.eigen.L.wo.Z(K)
    }
    if ( is.null(eig.R) ) {
      eig.R <- emma.eigen.R.wo.Z(K,X)
    }
    etas <- crossprod(eig.R$vectors,y)
    
    
    logdelta <- (0:ngrids)/ngrids*(ulim-llim)+llim
    m <- length(logdelta)
    delta <- exp(logdelta)
    Lambdas <- matrix(eig.R$values,n-q,m) + matrix(delta,n-q,m,byrow=TRUE)
    Xis <- matrix(eig.L$values,n,m) + matrix(delta,n,m,byrow=TRUE)
    Etasq <- matrix(etas*etas,n-q,m)
    LL <- 0.5*(n*(log(n/(2*pi))-1-log(colSums(Etasq/Lambdas)))-colSums(log(Xis)))
    dLL <- 0.5*delta*(n*colSums(Etasq/(Lambdas*Lambdas))/colSums(Etasq/Lambdas)-colSums(1/Xis))
    
    optlogdelta <- vector(length=0)
    optLL <- vector(length=0)
    if ( dLL[1] < esp ) {
      optlogdelta <- append(optlogdelta, llim)
      optLL <- append(optLL, emma.delta.ML.LL.wo.Z(llim,eig.R$values,etas,eig.L$values))
    }
    if ( dLL[m-1] > 0-esp ) {
      optlogdelta <- append(optlogdelta, ulim)
      optLL <- append(optLL, emma.delta.ML.LL.wo.Z(ulim,eig.R$values,etas,eig.L$values))
    }
    
    for( i in 1:(m-1) )
    {
      if ( ( dLL[i]*dLL[i+1] < 0 ) && ( dLL[i] > 0 ) && ( dLL[i+1] < 0 ) ) 
      {
        r <- uniroot(emma.delta.ML.dLL.wo.Z, lower=logdelta[i], upper=logdelta[i+1], lambda=eig.R$values, etas=etas, xi=eig.L$values)
        optlogdelta <- append(optlogdelta, r$root)
        optLL <- append(optLL, emma.delta.ML.LL.wo.Z(r$root,eig.R$values, etas, eig.L$values))
      }
    }
    #    optdelta <- exp(optlogdelta)
  }
  else {
    if ( is.null(eig.L) ) {
      eig.L <- emma.eigen.L.w.Z(Z,K)
    }
    if ( is.null(eig.R) ) {
      eig.R <- emma.eigen.R.w.Z(Z,K,X)
    }
    etas <- crossprod(eig.R$vectors,y)
    etas.1 <- etas[1:(t-q)]
    etas.2 <- etas[(t-q+1):(n-q)]
    etas.2.sq <- sum(etas.2*etas.2)
    
    logdelta <- (0:ngrids)/ngrids*(ulim-llim)+llim
    
    m <- length(logdelta)
    delta <- exp(logdelta)
    Lambdas <- matrix(eig.R$values,t-q,m) + matrix(delta,t-q,m,byrow=TRUE)
    Xis <- matrix(eig.L$values,t,m) + matrix(delta,t,m,byrow=TRUE)
    Etasq <- matrix(etas.1*etas.1,t-q,m)
    #LL <- 0.5*(n*(log(n/(2*pi))-1-log(colSums(Etasq/Lambdas)+etas.2.sq/delta))-colSums(log(Xis))+(n-t)*log(deltas))
    dLL <- 0.5*delta*(n*(colSums(Etasq/(Lambdas*Lambdas))+etas.2.sq/(delta*delta))/(colSums(Etasq/Lambdas)+etas.2.sq/delta)-(colSums(1/Xis)+(n-t)/delta))
    
    optlogdelta <- vector(length=0)
    optLL <- vector(length=0)
    if ( dLL[1] < esp ) {
      optlogdelta <- append(optlogdelta, llim)
      optLL <- append(optLL, emma.delta.ML.LL.w.Z(llim,eig.R$values,etas.1,eig.L$values,n,etas.2.sq))
    }
    if ( dLL[m-1] > 0-esp ) {
      optlogdelta <- append(optlogdelta, ulim)
      optLL <- append(optLL, emma.delta.ML.LL.w.Z(ulim,eig.R$values,etas.1,eig.L$values,n,etas.2.sq))
    }
    
    for( i in 1:(m-1) )
    {
      if ( ( dLL[i]*dLL[i+1] < 0 ) && ( dLL[i] > 0 ) && ( dLL[i+1] < 0 ) ) 
      {
        r <- uniroot(emma.delta.ML.dLL.w.Z, lower=logdelta[i], upper=logdelta[i+1], lambda=eig.R$values, etas.1=etas.1, xi.1=eig.L$values, n=n, etas.2.sq = etas.2.sq )
        optlogdelta <- append(optlogdelta, r$root)
        optLL <- append(optLL, emma.delta.ML.LL.w.Z(r$root,eig.R$values, etas.1, eig.L$values, n, etas.2.sq ))
      }
    }
    #    optdelta <- exp(optlogdelta)
  }
  
  maxdelta <- exp(optlogdelta[which.max(optLL)])
  maxLL <- max(optLL)
  if ( is.null(Z) ) {
    maxva <- sum(etas*etas/(eig.R$values+maxdelta))/n    
  }
  else {
    maxva <- (sum(etas.1*etas.1/(eig.R$values+maxdelta))+etas.2.sq/maxdelta)/n
  }
  maxve <- maxva*maxdelta
  
  return (list(ML=maxLL,delta=maxdelta,ve=maxve,vg=maxva))
}

emma.REMLE <- function(y, X, K, Z=NULL, ngrids=100, llim=-10, ulim=10,
                       esp=1e-10, eig.L = NULL, eig.R = NULL) {
  n <- length(y)
  t <- nrow(K)
  q <- ncol(X)
  
  #  stopifnot(nrow(K) == t)
  stopifnot(ncol(K) == t)
  stopifnot(nrow(X) == n)
  
  if ( det(crossprod(X,X)) == 0 ) {
    warning("X is singular")
    return (list(REML=0,delta=0,ve=0,vg=0))
  }
  
  if ( is.null(Z) ) {
    if ( is.null(eig.R) ) {
      eig.R <- emma.eigen.R.wo.Z(K,X)
    }
    etas <- crossprod(eig.R$vectors,y)
    
    logdelta <- (0:ngrids)/ngrids*(ulim-llim)+llim
    m <- length(logdelta)
    delta <- exp(logdelta)
    Lambdas <- matrix(eig.R$values,n-q,m) + matrix(delta,n-q,m,byrow=TRUE)
    Etasq <- matrix(etas*etas,n-q,m)
    LL <- 0.5*((n-q)*(log((n-q)/(2*pi))-1-log(colSums(Etasq/Lambdas)))-colSums(log(Lambdas)))
    dLL <- 0.5*delta*((n-q)*colSums(Etasq/(Lambdas*Lambdas))/colSums(Etasq/Lambdas)-colSums(1/Lambdas))
    
    optlogdelta <- vector(length=0)
    optLL <- vector(length=0)
    if ( dLL[1] < esp ) {
      optlogdelta <- append(optlogdelta, llim)
      optLL <- append(optLL, emma.delta.REML.LL.wo.Z(llim,eig.R$values,etas))
    }
    if ( dLL[m-1] > 0-esp ) {
      optlogdelta <- append(optlogdelta, ulim)
      optLL <- append(optLL, emma.delta.REML.LL.wo.Z(ulim,eig.R$values,etas))
    }
    
    for( i in 1:(m-1) )
    {
      if ( ( dLL[i]*dLL[i+1] < 0 ) && ( dLL[i] > 0 ) && ( dLL[i+1] < 0 ) ) 
      {
        r <- uniroot(emma.delta.REML.dLL.wo.Z, lower=logdelta[i], upper=logdelta[i+1], lambda=eig.R$values, etas=etas)
        optlogdelta <- append(optlogdelta, r$root)
        optLL <- append(optLL, emma.delta.REML.LL.wo.Z(r$root,eig.R$values, etas))
      }
    }
    #    optdelta <- exp(optlogdelta)
  }
  else {
    if ( is.null(eig.R) ) {
      eig.R <- emma.eigen.R.w.Z(Z,K,X)
    }
    etas <- crossprod(eig.R$vectors,y)
    etas.1 <- etas[1:(t-q)]
    etas.2 <- etas[(t-q+1):(n-q)]
    etas.2.sq <- sum(etas.2*etas.2)
    
    logdelta <- (0:ngrids)/ngrids*(ulim-llim)+llim
    m <- length(logdelta)
    delta <- exp(logdelta)
    Lambdas <- matrix(eig.R$values,t-q,m) + matrix(delta,t-q,m,byrow=TRUE)
    Etasq <- matrix(etas.1*etas.1,t-q,m)
    dLL <- 0.5*delta*((n-q)*(colSums(Etasq/(Lambdas*Lambdas))+etas.2.sq/(delta*delta))/(colSums(Etasq/Lambdas)+etas.2.sq/delta)-(colSums(1/Lambdas)+(n-t)/delta))
    
    optlogdelta <- vector(length=0)
    optLL <- vector(length=0)
    if ( dLL[1] < esp ) {
      optlogdelta <- append(optlogdelta, llim)
      optLL <- append(optLL, emma.delta.REML.LL.w.Z(llim,eig.R$values,etas.1,n,t,etas.2.sq))
    }
    if ( dLL[m-1] > 0-esp ) {
      optlogdelta <- append(optlogdelta, ulim)
      optLL <- append(optLL, emma.delta.REML.LL.w.Z(ulim,eig.R$values,etas.1,n,t,etas.2.sq))
    }
    
    for( i in 1:(m-1) )
    {
      if ( ( dLL[i]*dLL[i+1] < 0 ) && ( dLL[i] > 0 ) && ( dLL[i+1] < 0 ) ) 
      {
        r <- uniroot(emma.delta.REML.dLL.w.Z, lower=logdelta[i], upper=logdelta[i+1], lambda=eig.R$values, etas.1=etas.1, n=n, t1=t, etas.2.sq = etas.2.sq )
        optlogdelta <- append(optlogdelta, r$root)
        optLL <- append(optLL, emma.delta.REML.LL.w.Z(r$root,eig.R$values, etas.1, n, t, etas.2.sq ))
      }
    }
    #    optdelta <- exp(optlogdelta)
  }  
  
  maxdelta <- exp(optlogdelta[which.max(optLL)])
  maxLL <- max(optLL)
  if ( is.null(Z) ) {
    maxva <- sum(etas*etas/(eig.R$values+maxdelta))/(n-q)    
  }
  else {
    maxva <- (sum(etas.1*etas.1/(eig.R$values+maxdelta))+etas.2.sq/maxdelta)/(n-q)
  }
  maxve <- maxva*maxdelta
  
  return (list(REML=maxLL,delta=maxdelta,ve=maxve,vg=maxva))
}

emma.ML.LRT <- function(ys, xs, K, Z=NULL, X0 = NULL, ngrids=100, llim=-10, ulim=10, esp=1e-10, ponly = FALSE) {
  if ( is.null(dim(ys)) || ncol(ys) == 1 ) {
    ys <- matrix(ys,1,length(ys))
  }
  if ( is.null(dim(xs)) || ncol(xs) == 1 ) {
    xs <- matrix(xs,1,length(xs))
  }
  if ( is.null(X0) ) {
    X0 <- matrix(1,ncol(ys),1)
  }  
  
  g <- nrow(ys)
  n <- ncol(ys)
  m <- nrow(xs)
  t <- ncol(xs)
  q0 <- ncol(X0)
  q1 <- q0 + 1
  
  if ( !ponly ) {
    ML1s <- matrix(nrow=m,ncol=g)
    ML0s <- matrix(nrow=m,ncol=g)
    vgs <- matrix(nrow=m,ncol=g)
    ves <- matrix(nrow=m,ncol=g)
  }
  stats <- matrix(nrow=m,ncol=g)
  ps <- matrix(nrow=m,ncol=g)
  ML0 <- vector(length=g)
  
  stopifnot(nrow(K) == t)
  stopifnot(ncol(K) == t)
  stopifnot(nrow(X0) == n)
  
  if ( sum(is.na(ys)) == 0 ) {
    eig.L <- emma.eigen.L(Z,K)
    eig.R0 <- emma.eigen.R(Z,K,X0)
    
    for(i in 1:g) {
      ML0[i] <- emma.MLE(ys[i,],X0,K,Z,ngrids,llim,ulim,esp,eig.L,eig.R0)$ML
    }
    
    x.prev <- vector(length=0)
    
    for(i in 1:m) {
      vids <- !is.na(xs[i,])
      nv <- sum(vids)
      xv <- xs[i,vids]
      
      if ( ( mean(xv) <= 0 ) || ( mean(xv) >= 1 ) ) {
        if (!ponly) {
          stats[i,] <- rep(NA,g)
          vgs[i,] <- rep(NA,g)
          ves[i,] <- rep(NA,g)
          ML1s[i,] <- rep(NA,g)
          ML0s[i,] <- rep(NA,g)
        }
        ps[i,] = rep(1,g)
      }
      else if ( identical(x.prev, xv) ) {
        if ( !ponly ) {
          stats[i,] <- stats[i-1,]
          vgs[i,] <- vgs[i-1,]
          ves[i,] <- ves[i-1,]
          ML1s[i,] <- ML1s[i-1,]
          ML0s[i,] <- ML0s[i-1,]
        }
        ps[i,] <- ps[i-1,]
      }
      else {
        if ( is.null(Z) ) {
          X <- cbind(X0[vids,,drop=FALSE],xs[i,vids])
          eig.R1 = emma.eigen.R.wo.Z(K[vids,vids],X)
        }
        else {
          vrows <- as.logical(rowSums(Z[,vids]))
          nr <- sum(vrows)
          X <- cbind(X0[vrows,,drop=FALSE],Z[vrows,vids]%*%t(xs[i,vids,drop=FALSE]))
          eig.R1 = emma.eigen.R.w.Z(Z[vrows,vids],K[vids,vids],X)          
        }
        
        for(j in 1:g) {
          if ( nv == t ) {
            MLE <- emma.MLE(ys[j,],X,K,Z,ngrids,llim,ulim,esp,eig.L,eig.R1)
            #            MLE <- emma.MLE(ys[j,],X,K,Z,ngrids,llim,ulim,esp,eig.L,eig.R1)            
            if (!ponly) { 
              ML1s[i,j] <- MLE$ML
              vgs[i,j] <- MLE$vg
              ves[i,j] <- MLE$ve
            }
            stats[i,j] <- 2*(MLE$ML-ML0[j])
            
          }
          else {
            if ( is.null(Z) ) {
              eig.L0 <- emma.eigen.L.wo.Z(K[vids,vids])
              MLE0 <- emma.MLE(ys[j,vids],X0[vids,,drop=FALSE],K[vids,vids],NULL,ngrids,llim,ulim,esp,eig.L0)
              MLE1 <- emma.MLE(ys[j,vids],X,K[vids,vids],NULL,ngrids,llim,ulim,esp,eig.L0)
            }
            else {
              if ( nr == n ) {
                MLE1 <- emma.MLE(ys[j,],X,K,Z,ngrids,llim,ulim,esp,eig.L)
              }
              else {
                eig.L0 <- emma.eigen.L.w.Z(Z[vrows,vids],K[vids,vids])              
                MLE0 <- emma.MLE(ys[j,vrows],X0[vrows,,drop=FALSE],K[vids,vids],Z[vrows,vids],ngrids,llim,ulim,esp,eig.L0)
                MLE1 <- emma.MLE(ys[j,vrows],X,K[vids,vids],Z[vrows,vids],ngrids,llim,ulim,esp,eig.L0)
              }
            }
            if (!ponly) { 
              ML1s[i,j] <- MLE1$ML
              ML0s[i,j] <- MLE0$ML
              vgs[i,j] <- MLE1$vg
              ves[i,j] <- MLE1$ve
            }
            stats[i,j] <- 2*(MLE1$ML-MLE0$ML)
          }
        }
        if ( ( nv == t ) && ( !ponly ) ) {
          ML0s[i,] <- ML0
        }
        ps[i,] <- pchisq(stats[i,],1,lower.tail=FALSE)
      }
    }
  }
  else {
    eig.L <- emma.eigen.L(Z,K)
    eig.R0 <- emma.eigen.R(Z,K,X0)
    
    for(i in 1:g) {
      vrows <- !is.na(ys[i,])      
      if ( is.null(Z) ) {
        ML0[i] <- emma.MLE(ys[i,vrows],X0[vrows,,drop=FALSE],K[vrows,vrows],NULL,ngrids,llim,ulim,esp)$ML
      }
      else {
        vids <- colSums(Z[vrows,]>0)
        
        ML0[i] <- emma.MLE(ys[i,vrows],X0[vrows,,drop=FALSE],K[vids,vids],Z[vrows,vids],ngrids,llim,ulim,esp)$ML        
      }
    }
    
    x.prev <- vector(length=0)
    
    for(i in 1:m) {
      vids <- !is.na(xs[i,])
      nv <- sum(vids)
      xv <- xs[i,vids]
      
      if ( ( mean(xv) <= 0 ) || ( mean(xv) >= 1 ) ) {
        if (!ponly) {
          stats[i,] <- rep(NA,g)
          vgs[i,] <- rep(NA,g)
          ves[i,] <- rep(NA,g)
          ML1s[i,] <- rep(NA,g)
          ML0s[,i] <- rep(NA,g)
        }
        ps[i,] = rep(1,g)
      }      
      else if ( identical(x.prev, xv) ) {
        if ( !ponly ) {
          stats[i,] <- stats[i-1,]
          vgs[i,] <- vgs[i-1,]
          ves[i,] <- ves[i-1,]
          ML1s[i,] <- ML1s[i-1,]
        }
        ps[i,] = ps[i-1,]
      }
      else {
        if ( is.null(Z) ) {
          X <- cbind(X0,xs[i,])
          if ( nv == t ) {
            eig.R1 = emma.eigen.R.wo.Z(K,X)
          }          
        }
        else {
          vrows <- as.logical(rowSums(Z[,vids]))
          X <- cbind(X0,Z[,vids,drop=FALSE]%*%t(xs[i,vids,drop=FALSE]))
          if ( nv == t ) {
            eig.R1 = emma.eigen.R.w.Z(Z,K,X)
          }
        }
        
        for(j in 1:g) {
          #          print(j)
          vrows <- !is.na(ys[j,])
          if ( nv == t ) {
            nr <- sum(vrows)
            if ( is.null(Z) ) {
              if ( nr == n ) {
                MLE <- emma.MLE(ys[j,],X,K,NULL,ngrids,llim,ulim,esp,eig.L,eig.R1)                
              }
              else {
                MLE <- emma.MLE(ys[j,vrows],X[vrows,],K[vrows,vrows],NULL,ngrids,llim,ulim,esp)
              }
            }
            else {
              if ( nr == n ) {
                MLE <- emma.MLE(ys[j,],X,K,Z,ngrids,llim,ulim,esp,eig.L,eig.R1)                
              }
              else {
                vtids <- as.logical(colSums(Z[vrows,,drop=FALSE]))
                MLE <- emma.MLE(ys[j,vrows],X[vrows,],K[vtids,vtids],Z[vrows,vtids],ngrids,llim,ulim,esp)
              }
            }
            
            if (!ponly) { 
              ML1s[i,j] <- MLE$ML
              vgs[i,j] <- MLE$vg
              ves[i,j] <- MLE$ve
            }
            stats[i,j] <- 2*(MLE$ML-ML0[j])
          }
          else {
            if ( is.null(Z) ) {
              vtids <- vrows & vids
              eig.L0 <- emma.eigen.L(NULL,K[vtids,vtids])
              MLE0 <- emma.MLE(ys[j,vtids],X0[vtids,,drop=FALSE],K[vtids,vtids],NULL,ngrids,llim,ulim,esp,eig.L0)
              MLE1 <- emma.MLE(ys[j,vtids],X[vtids,],K[vtids,vtids],NULL,ngrids,llim,ulim,esp,eig.L0)
            }
            else {
              vtids <- as.logical(colSums(Z[vrows,])) & vids
              vtrows <- vrows & as.logical(rowSums(Z[,vids]))
              eig.L0 <- emma.eigen.L(Z[vtrows,vtids],K[vtids,vtids])
              MLE0 <- emma.MLE(ys[j,vtrows],X0[vtrows,,drop=FALSE],K[vtids,vtids],Z[vtrows,vtids],ngrids,llim,ulim,esp,eig.L0)
              MLE1 <- emma.MLE(ys[j,vtrows],X[vtrows,],K[vtids,vtids],Z[vtrows,vtids],ngrids,llim,ulim,esp,eig.L0)
            }
            if (!ponly) { 
              ML1s[i,j] <- MLE1$ML
              vgs[i,j] <- MLE1$vg
              ves[i,j] <- MLE1$ve
              ML0s[i,j] <- MLE0$ML
            }
            stats[i,j] <- 2*(MLE1$ML-MLE0$ML)
          }
        }
        if ( ( nv == t ) && ( !ponly ) ) {
          ML0s[i,] <- ML0
        }
        ps[i,] <- pchisq(stats[i,],1,lower.tail=FALSE)
      }
    }    
  }
  if ( ponly ) {
    return (ps)
  }
  else {
    return (list(ps=ps,ML1s=ML1s,ML0s=ML0s,stats=stats,vgs=vgs,ves=ves))
  }  
}

emma.REML.t <- function(ys, xs, K, Z=NULL, X0 = NULL, ngrids=100, llim=-10, ulim=10, esp=1e-10, ponly = FALSE) {
  if ( is.null(dim(ys)) || ncol(ys) == 1 ) {
    ys <- matrix(ys,1,length(ys))
  }
  if ( is.null(dim(xs)) || ncol(xs) == 1 ) {
    xs <- matrix(xs,1,length(xs))
  }
  if ( is.null(X0) ) {
    X0 <- matrix(1,ncol(ys),1)
  }
  
  g <- nrow(ys)
  n <- ncol(ys)
  m <- nrow(xs)
  t <- ncol(xs)
  q0 <- ncol(X0)
  q1 <- q0 + 1
  
  stopifnot(nrow(K) == t)
  stopifnot(ncol(K) == t)
  stopifnot(nrow(X0) == n)
  
  if ( !ponly ) {
    REMLs <- matrix(nrow=m,ncol=g)
    vgs <- matrix(nrow=m,ncol=g)
    ves <- matrix(nrow=m,ncol=g)
  }
  dfs <- matrix(nrow=m,ncol=g)
  stats <- matrix(nrow=m,ncol=g)
  ps <- matrix(nrow=m,ncol=g)
  
  if ( sum(is.na(ys)) == 0 ) {
    eig.L <- emma.eigen.L(Z,K)
    
    x.prev <- vector(length=0)
    
    for(i in 1:m) {
      vids <- !is.na(xs[i,])
      nv <- sum(vids)
      xv <- xs[i,vids]
      
      if ( ( mean(xv) <= 0 ) || ( mean(xv) >= 1 ) ) {
        if ( !ponly ) {
          vgs[i,] <- rep(NA,g)
          ves[i,] <- rep(NA,g)
          dfs[i,] <- rep(NA,g)
          REMLs[i,] <- rep(NA,g)
          stats[i,] <- rep(NA,g)
        }
        ps[i,] = rep(1,g)
        
      }
      else if ( identical(x.prev, xv) ) {
        if ( !ponly ) {
          vgs[i,] <- vgs[i-1,]
          ves[i,] <- ves[i-1,]
          dfs[i,] <- dfs[i-1,]
          REMLs[i,] <- REMLs[i-1,]
          stats[i,] <- stats[i-1,]
        }
        ps[i,] <- ps[i-1,]
      }
      else {
        if ( is.null(Z) ) {
          X <- cbind(X0[vids,,drop=FALSE],xs[i,vids])
          eig.R1 = emma.eigen.R.wo.Z(K[vids,vids],X)
        }
        else {
          vrows <- as.logical(rowSums(Z[,vids]))              
          X <- cbind(X0[vrows,,drop=FALSE],Z[vrows,vids,drop=FALSE]%*%t(xs[i,vids,drop=FALSE]))
          eig.R1 = emma.eigen.R.w.Z(Z[vrows,vids],K[vids,vids],X)
        }
        
        for(j in 1:g) {
          if ( nv == t ) {
            REMLE <- emma.REMLE(ys[j,],X,K,Z,ngrids,llim,ulim,esp,eig.R1)
            if ( is.null(Z) ) {
              U <- eig.L$vectors * matrix(sqrt(1/(eig.L$values+REMLE$delta)),t,t,byrow=TRUE)
              dfs[i,j] <- nv - q1
            }
            else {
              U <- eig.L$vectors * matrix(c(sqrt(1/(eig.L$values+REMLE$delta)),rep(sqrt(1/REMLE$delta),n-t)),n,n,byrow=TRUE)
              dfs[i,j] <- n - q1
            }
            yt <- crossprod(U,ys[j,])
            Xt <- crossprod(U,X)
            iXX <- solve(crossprod(Xt,Xt))
            beta <- iXX%*%crossprod(Xt,yt)
            
            if ( !ponly ) {
              vgs[i,j] <- REMLE$vg
              ves[i,j] <- REMLE$ve
              REMLs[i,j] <- REMLE$REML
            }
            stats[i,j] <- beta[q1]/sqrt(iXX[q1,q1]*REMLE$vg)
          }
          else {
            if ( is.null(Z) ) {
              eig.L0 <- emma.eigen.L.wo.Z(K[vids,vids])
              nr <- sum(vids)
              yv <- ys[j,vids]
              REMLE <- emma.REMLE(yv,X,K[vids,vids,drop=FALSE],NULL,ngrids,llim,ulim,esp,eig.R1)
              U <- eig.L0$vectors * matrix(sqrt(1/(eig.L0$values+REMLE$delta)),nr,nr,byrow=TRUE)
              dfs[i,j] <- nr - q1
            }
            else {
              eig.L0 <- emma.eigen.L.w.Z(Z[vrows,vids,drop=FALSE],K[vids,vids])              
              yv <- ys[j,vrows]
              nr <- sum(vrows)
              tv <- sum(vids)
              REMLE <- emma.REMLE(yv,X,K[vids,vids,drop=FALSE],Z[vrows,vids,drop=FALSE],ngrids,llim,ulim,esp,eig.R1)
              U <- eig.L0$vectors * matrix(c(sqrt(1/(eig.L0$values+REMLE$delta)),rep(sqrt(1/REMLE$delta),nr-tv)),nr,nr,byrow=TRUE)
              dfs[i,j] <- nr - q1
            }
            yt <- crossprod(U,yv)
            Xt <- crossprod(U,X)
            iXX <- solve(crossprod(Xt,Xt))
            beta <- iXX%*%crossprod(Xt,yt)
            if (!ponly) {
              vgs[i,j] <- REMLE$vg
              ves[i,j] <- REMLE$ve
              REMLs[i,j] <- REMLE$REML
            }
            stats[i,j] <- beta[q1]/sqrt(iXX[q1,q1]*REMLE$vg)
          }
        }
        ps[i,] <- 2*pt(abs(stats[i,]),dfs[i,],lower.tail=FALSE)
      }
    }
  }
  else {
    eig.L <- emma.eigen.L(Z,K)
    eig.R0 <- emma.eigen.R(Z,K,X0)
    
    x.prev <- vector(length=0)
    
    for(i in 1:m) {
      vids <- !is.na(xs[i,])
      nv <- sum(vids)
      xv <- xs[i,vids]
      
      if ( ( mean(xv) <= 0 ) || ( mean(xv) >= 1 ) ) {
        if (!ponly) {
          vgs[i,] <- rep(NA,g)
          ves[i,] <- rep(NA,g)
          REMLs[i,] <- rep(NA,g)
          dfs[i,] <- rep(NA,g)
        }
        ps[i,] = rep(1,g)
      }      
      else if ( identical(x.prev, xv) ) {
        if ( !ponly ) {
          stats[i,] <- stats[i-1,]
          vgs[i,] <- vgs[i-1,]
          ves[i,] <- ves[i-1,]
          REMLs[i,] <- REMLs[i-1,]
          dfs[i,] <- dfs[i-1,]
        }
        ps[i,] = ps[i-1,]
      }
      else {
        if ( is.null(Z) ) {
          X <- cbind(X0,xs[i,])
          if ( nv == t ) {
            eig.R1 = emma.eigen.R.wo.Z(K,X)
          }
        }
        else {
          vrows <- as.logical(rowSums(Z[,vids,drop=FALSE]))
          X <- cbind(X0,Z[,vids,drop=FALSE]%*%t(xs[i,vids,drop=FALSE]))
          if ( nv == t ) {
            eig.R1 = emma.eigen.R.w.Z(Z,K,X)
          }          
        }
        
        for(j in 1:g) {
          vrows <- !is.na(ys[j,])
          if ( nv == t ) {
            yv <- ys[j,vrows]
            nr <- sum(vrows)
            if ( is.null(Z) ) {
              if ( nr == n ) {
                REMLE <- emma.REMLE(yv,X,K,NULL,ngrids,llim,ulim,esp,eig.R1)
                U <- eig.L$vectors * matrix(sqrt(1/(eig.L$values+REMLE$delta)),n,n,byrow=TRUE)                
              }
              else {
                eig.L0 <- emma.eigen.L.wo.Z(K[vrows,vrows,drop=FALSE])
                REMLE <- emma.REMLE(yv,X[vrows,,drop=FALSE],K[vrows,vrows,drop=FALSE],NULL,ngrids,llim,ulim,esp)
                U <- eig.L0$vectors * matrix(sqrt(1/(eig.L0$values+REMLE$delta)),nr,nr,byrow=TRUE)
              }
              dfs[i,j] <- nr-q1
            }
            else {
              if ( nr == n ) {
                REMLE <- emma.REMLE(yv,X,K,Z,ngrids,llim,ulim,esp,eig.R1)
                U <- eig.L$vectors * matrix(c(sqrt(1/(eig.L$values+REMLE$delta)),rep(sqrt(1/REMLE$delta),n-t)),n,n,byrow=TRUE)                
              }
              else {
                vtids <- as.logical(colSums(Z[vrows,,drop=FALSE]))
                eig.L0 <- emma.eigen.L.w.Z(Z[vrows,vtids,drop=FALSE],K[vtids,vtids,drop=FALSE])
                REMLE <- emma.REMLE(yv,X[vrows,,drop=FALSE],K[vtids,vtids,drop=FALSE],Z[vrows,vtids,drop=FALSE],ngrids,llim,ulim,esp)
                U <- eig.L0$vectors * matrix(c(sqrt(1/(eig.L0$values+REMLE$delta)),rep(sqrt(1/REMLE$delta),nr-sum(vtids))),nr,nr,byrow=TRUE)
              }
              dfs[i,j] <- nr-q1
            }
            
            yt <- crossprod(U,yv)
            Xt <- crossprod(U,X[vrows,,drop=FALSE])
            iXX <- solve(crossprod(Xt,Xt))
            beta <- iXX%*%crossprod(Xt,yt)
            if ( !ponly ) {
              vgs[i,j] <- REMLE$vg
              ves[i,j] <- REMLE$ve
              REMLs[i,j] <- REMLE$REML
            }
            stats[i,j] <- beta[q1]/sqrt(iXX[q1,q1]*REMLE$vg)
          }
          else {
            if ( is.null(Z) ) {
              vtids <- vrows & vids
              eig.L0 <- emma.eigen.L.wo.Z(K[vtids,vtids,drop=FALSE])
              yv <- ys[j,vtids]
              nr <- sum(vtids)
              REMLE <- emma.REMLE(yv,X[vtids,,drop=FALSE],K[vtids,vtids,drop=FALSE],NULL,ngrids,llim,ulim,esp)
              U <- eig.L0$vectors * matrix(sqrt(1/(eig.L0$values+REMLE$delta)),nr,nr,byrow=TRUE)
              Xt <- crossprod(U,X[vtids,,drop=FALSE])
              dfs[i,j] <- nr-q1
            }
            else {
              vtids <- as.logical(colSums(Z[vrows,,drop=FALSE])) & vids
              vtrows <- vrows & as.logical(rowSums(Z[,vids,drop=FALSE]))
              eig.L0 <- emma.eigen.L.w.Z(Z[vtrows,vtids,drop=FALSE],K[vtids,vtids,drop=FALSE])
              yv <- ys[j,vtrows]
              nr <- sum(vtrows)
              REMLE <- emma.REMLE(yv,X[vtrows,,drop=FALSE],K[vtids,vtids,drop=FALSE],Z[vtrows,vtids,drop=FALSE],ngrids,llim,ulim,esp)
              U <- eig.L0$vectors * matrix(c(sqrt(1/(eig.L0$values+REMLE$delta)),rep(sqrt(1/REMLE$delta),nr-sum(vtids))),nr,nr,byrow=TRUE)
              Xt <- crossprod(U,X[vtrows,,drop=FALSE])
              dfs[i,j] <- nr-q1
            }
            yt <- crossprod(U,yv)
            iXX <- solve(crossprod(Xt,Xt))
            beta <- iXX%*%crossprod(Xt,yt)
            if ( !ponly ) {
              vgs[i,j] <- REMLE$vg
              ves[i,j] <- REMLE$ve
              REMLs[i,j] <- REMLE$REML
            }
            stats[i,j] <- beta[q1]/sqrt(iXX[q1,q1]*REMLE$vg)
            
          }
        }
        ps[i,] <- 2*pt(abs(stats[i,]),dfs[i,],lower.tail=FALSE)        
      }
    }    
  }
  if ( ponly ) {
    return (ps)
  }
  else {
    return (list(ps=ps,REMLs=REMLs,stats=stats,dfs=dfs,vgs=vgs,ves=ves))
  }
}

