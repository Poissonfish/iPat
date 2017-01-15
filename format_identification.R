setwd('/Users/Poissonfish/all demofile')
G = read.csv("G.txt", sep = '\t', header=FALSE)
GD = read.csv("GD.txt", sep = '\t', header=FALSE)
GM = read.csv("GM.txt", sep = '\t', header=FALSE)
P = read.csv("P.txt", sep = '\t', header=TRUE)
KI = read.csv("KI.txt", sep = '\t', header=FALSE)
CO = read.csv("CO.txt", sep = '\t', header=FALSE)

## 2
file1 = read.csv("G.txt", sep = '\t', header= FALSE, stringsAsFactors=FALSE)
file2 = read.csv("P.txt", sep = '\t', header= FALSE, stringsAsFactors=FALSE)

if(all(file1[2:6, 5]%in%c('+', '-')) && any(file1[2:6, 5]%in%c('+', '-'))){
  one = 1
  two = 0
}else{
  one = 0
  two = 1
}

if(all(file1[2:6, 5]%in%c('+', '-')) && any(file1[2:6, 5]%in%c('+', '-'))){one = 1;two = 0}else{one = 0;two = 1}

## 3
file1 = read.csv("P.txt", sep = '\t', head = FALSE, stringsAsFactors=FALSE)
file2 = read.csv("GD.txt", sep = '\t', head = FALSE, stringsAsFactors=FALSE)
file3 = read.csv("GM.txt", sep = '\t', head = FALSE, stringsAsFactors=FALSE)
max = max(dim(file1)[2], dim(file2)[2], dim(file3)[2])
if(dim(file1)[2] == max){
  one = 2
  if(all(file1[1,2:6]==file2[2:6, 1])){
    two = 3 
    three = 0
  }else{
    two = 0
    three = 3
  }
}else if(dim(file2)[2] == max){
  two = 2
  if(all(file2[1,2:6]==file1[2:6, 1])){
    one = 3
    three = 0
  }else{
    one = 0
    three = 3
  }
}else{
  three = 2
  if(all(file3[1,2:6]==file2[2:6, 1])){
    one = 0
    two = 3
  }else{
    one = 3
    two = 0
  }
}
one
two
three

if(dim(file1)[2] == max){one = 2;if(all(file1[1,2:6]==file2[2:6, 1])){two = 3;three = 0}else{two = 0;three = 3}}else if(dim(file2)[2] == max){two = 2;if(all(file2[1,2:6]==file1[2:6, 1])){one = 3;three = 0}else{one = 0;three = 3}}else{three = 2;if(all(file3[1,2:6]==file2[2:6, 1])){one = 0;two = 3}else{one = 3;two = 0}}

#281 samples, 3093 QTNs


#Step 2: Run GAPIT 
myGAPIT <- GAPIT( Y=P ,
                  KI=KI, 
                  PCA.total=3, 
                  SNP.test=FALSE)

