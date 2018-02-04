`bsa.manhattan` <-
function(GI.MP = NULL, name.of.trait = "Trait",plot.type = "Genomewise",
           DPP=50000,cutOff=0.01,band=5,seqQTN=NULL,plot.style="Oceanic"){
    #Object: Make a Manhattan Plot
    #Options for plot.type = "Separate_Graph_for_Each_Chromosome" and "Same_Graph_for_Each_Chromosome"
    #Output: A pdf of the Manhattan Plot

    # Haixiao, Nov 7, 2011
    ##############################################################################################
    #print("Manhattan ploting...")
    
    #print(seqQTN)
    #do nothing if null input
    if(is.null(GI.MP)) return
    #print("Dimension of GI.MP")
    #print(dim(GI.MP))
    #print(head(GI.MP))
    #print(tail(GI.MP))
    
    
    #seqQTN=c(300,1000,2500)
    #Handler of lable paosition only indicated by negatie position
    position.only=F
    if(!is.null(seqQTN)){
      if(seqQTN[1]<0){
        seqQTN=-seqQTN
        position.only=T
      }
      
    }
    borrowSlot=4
    GI.MP[,borrowSlot]=0 #Inicial as 0
    if(!is.null(seqQTN))GI.MP[seqQTN,borrowSlot]=1
    
    
    GI.MP=matrix(as.numeric(as.matrix(GI.MP) ) ,nrow(GI.MP),ncol(GI.MP))
    
    #Remove all SNPs that do not have a choromosome, bp position and p value(NA)
    GI.MP <- GI.MP[!is.na(GI.MP[,1]),]
    GI.MP <- GI.MP[!is.na(GI.MP[,2]),]
    GI.MP <- GI.MP[!is.na(GI.MP[,3]),]
    
    #Retain SNPs that have P values between 0 and 1 (not na etc)
    GI.MP <- GI.MP[GI.MP[,3]>0,]
    GI.MP <- GI.MP[GI.MP[,3]<=1,]
    
    #Remove chr 0 and 99
    GI.MP <- GI.MP[GI.MP[,1]!=0,]
    #GI.MP <- GI.MP[GI.MP[,1]!=99,]
    
    #print("Dimension of GI.MP after QC")
    #print(dim(GI.MP))
    
    numMarker=nrow(GI.MP)
    #bonferroniCutOff=-log10(cutOff/numMarker)
    
    #Replace P the -log10 of the P-values
    #GI.MP[,3] <-  -log10(GI.MP[,3])
    
    chm.to.analyze <- unique(GI.MP[,1])
    
    #print("name of chromosomes:")
    #print(chm.to.analyze)
    
    chm.to.analyze=chm.to.analyze[order(chm.to.analyze)]
    numCHR= length(chm.to.analyze)
    
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
      #col.Oceanic=rep(c(	'#EC5f67',	'#F99157',	'#FAC863',	'#99C794',	'#5FB3B3',	'#6699CC',	'#C594C5',	'#AB7967'),ceiling(numCHR/8))
      #col.Oceanic=rep(c(	'#EC5f67',		'#FAC863',	'#99C794',		'#6699CC',	'#C594C5',	'#AB7967'),ceiling(numCHR/6))
      col.Oceanic=rep(c(	'#EC5f67',		'#FAC863',	'#99C794',		'#6699CC',	'#C594C5'),ceiling(numCHR/5))
      col.cougars=rep(c(	'#990000',		'dimgray'),ceiling(numCHR/2))
      
      if(plot.style=="Rainbow")plot.color= col.Rainbow
      if(plot.style =="FarmCPU")plot.color= col.FarmCPU
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
        pdf(paste("FarmCPU.", name.of.trait,".Manhattan.Plot.Genomewise.pdf" ,sep = ""), width = 13,height=5.75)
      }else{
        pdf(paste("GAPIT.", name.of.trait,".Manhattan.Plot.Genomewise.pdf" ,sep = ""), width = 13,height=5.75)
      }
      par(mar = c(3,6,5,1))
      plot(y~x,xlab="",ylab="", 
           cex.axis=1.5, cex.lab=2, ,col=plot.color[z],axes=FALSE,type = "p",pch=mypch,lwd=wd,cex=s+.3,main = paste(name.of.trait,sep=" 			"),cex.main=2.5)
      
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
      #abline(h=bonferroniCutOff,col="forestgreen")
      
      #Set axises
      axis(1, at=ticks,cex.axis=1.5,labels=chm.to.analyze,tick=F)
      axis(2, at=c(0, 0.2, 0.4, 0.6, 0.8, 1), cex.axis=1.5, tick=F)
      
      box()
      palette("default")
      dev.off()
      #print("Manhattan done Genomewise")
      
    } #Genomewise plot
    
    #print("GAPIT.Manhattan accomplished successfully!zw")
  }
