`GAPIT.Numericalization` <-
function(x,bit=2,effect="Add",impute="None", Create.indicator = FALSE, Major.allele.zero = FALSE, byRow=TRUE){
#Object: To convert character SNP genotpe to numerical
#Output: Coresponding numerical value
#Authors: Feng Tian and Zhiwu Zhang
# Last update: May 30, 2011 
##############################################################################################
if(bit==1)  {
x[x=="X"]="N"
x[x=="-"]="N"
x[x=="+"]="N"
x[x=="/"]="N"
x[x=="K"]="Z" #K (for GT genotype)is replaced by Z to ensure heterozygose has the largest value
}

if(bit==2)  {
x[x=="XX"]="N"
x[x=="--"]="N"
x[x=="++"]="N"
x[x=="//"]="N"
x[x=="NN"]="N"
}

n=length(x)
lev=levels(as.factor(x))
lev=setdiff(lev,"N")
#print(lev)
len=length(lev)
#print(lev)



#Genotype counts
count=1:len
for(i in 1:len){
	count[i]=length(x[(x==lev[i])])
}



if(Major.allele.zero){
  if(len>1 & len<=3){
    #One bit: Make sure that the SNP with the major allele is on the top, and the SNP with the minor allele is on the second position
    if(bit==1){ 
      count.temp = cbind(count, seq(1:len))
      if(len==3) count.temp = count.temp[-3,]
      count.temp <- count.temp[order(count.temp[,1], decreasing = TRUE),]
      if(len==3)order =  c(count.temp[,2],3)else order = count.temp[,2]
    }

    #Two bit: Make sure that the SNP with the major allele is on the top, and the SNP with the minor allele is on the third position
    if(bit==2){ 
      count.temp = cbind(count, seq(1:len))
      if(len==3) count.temp = count.temp[-2,]
      count.temp <- count.temp[order(count.temp[,1], decreasing = TRUE),]
      if(len==3) order =  c(count.temp[1,2],2,count.temp[2,2])else order = count.temp[,2]
    }

    count = count[order]
    lev = lev[order]

  }   #End  if(len<=1 | len> 3)
} #End  if(Major.allele.zero)



#make two  bit order genotype as AA,AT and TT, one bit as A(AA),T(TT) and X(AT)
if(bit==1 & len==3){
	temp=count[2]
	count[2]=count[3]
	count[3]=temp
}
position=order(count)


#1status other than 2 or 3
if(len<=1 | len> 3)x=0

#2 status
if(len==2)x=ifelse(x=="N",NA,ifelse(x==lev[1],0,2))

#3 status
if(bit==1){
	if(len==3)x=ifelse(x=="N",NA,ifelse(x==lev[1],0,ifelse(x==lev[3],1,2)))
}else{
	if(len==3)x=ifelse(x=="N",NA,ifelse(x==lev[1],0,ifelse(x==lev[3],2,1)))
}

#print(paste(lev,len,sep=" "))
#print(position)

#missing data imputation
if(impute=="Middle") {x[is.na(x)]=1 }

if(len==3){
	if(impute=="Minor")  {x[is.na(x)]=position[1]  -1}
	if(impute=="Major")  {x[is.na(x)]=position[len]-1}

}else{
	if(impute=="Minor")  {x[is.na(x)]=2*(position[1]  -1)}
	if(impute=="Major")  {x[is.na(x)]=2*(position[len]-1)}
}

#alternative genetic models
if(effect=="Dom") x=ifelse(x==1,1,0)
if(effect=="Left") x[x==1]=0
if(effect=="Right") x[x==1]=2

if(byRow) {
  result=matrix(x,n,1)
}else{
  result=matrix(x,1,n)  
}

return(result)
}#end of GAPIT.Numericalization function
#=============================================================================================

