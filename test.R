setwd("~/test")
library('MASS') # required for ginv
library(multtest)
library(gplots)
library(compiler) #required for cmpfun
library("scatterplot3d")

source("http://www.zzlab.net/GAPIT/emma.txt")
source("http://www.zzlab.net/GAPIT/gapit_functions.txt")
x=proc.time()

myGAPIT = GAPIT(
Y=read.table('mdp_traits.txt', head = TRUE),
G=read.delim('mdp_genotype_test.hmp.txt', head = FALSE),
esp	=	1.00E-10	,
llim	=	-10	,
ngrid	=	100	,
ulim	=	10	,
acceleration	=	0	,
converge	=	1	,
maxLoop	=	3	,
file.Ext.G	=	NULL	,
file.Ext.GD	=	NULL	,
file.Ext.GM	=	NULL	,
file.fragment	=	99999	,
file.from	=	1	,
file.G	=	NULL	,
file.GD	=	NULL	,
file.GM	=	NULL	,
file.output	=	TRUE	,
file.path	=	NULL	,
file.to	=	1	,
file.total	=	NULL	,
group.by	=	10	,
group.from	=	30	,
group.to	=	1000000	,
kinship.algorithm	=	'VanRaden'	,
kinship.cluster	=	"average"	,
kinship.group	=	"Mean"	,
LD.chromosome	=	NULL	,
LD.location	=	NULL	,
LD.range	=	NULL	,
iteration.method	=	"accum"	,
method.bin	=	"static"	,
method.GLM	=	"fast.lm"	,
method.sub	=	"reward"	,
method.sub.final	=	"reward"	,
Model.selection	=	FALSE	,
cutOff	=	0.01	,
CV.Inheritance	=	NULL	,
DPP	=	100000	,
Geno.View.output	=	TRUE	,
iteration.output	=	FALSE	,
maxOut	=	100	,
output.hapmap	=	FALSE	,
output.numerical	=	FALSE	,
plot.style	=	"Oceanic"	,
threshold.output	=	0.01	,
PCA.total	=	0	,
PCA.View.output	=	TRUE	,
Prior	=	NULL	,
QTN	=	NULL	,
QTN.limit	=	0	,
QTN.method	=	"Penalty"	,
QTN.position	=	NULL	,
QTN.round	=	1	,
QTN.update	=	TRUE	,
Create.indicator	=	FALSE	,
Major.allele.zero	=	FALSE	,
SNP.CV	=	NULL	,
SNP.effect	=	"Add"	,
SNP.FDR	=	1	,
SNP.fraction	=	1	,
SNP.impute	=	"Middle"	,
SNP.MAF	=	0	,
SNP.P3D	=	TRUE	,
SNP.permutation	=	FALSE	,
SNP.robust	=	"GLM"	,
SNP.test	=	TRUE	,
bin.by	=	10000	,
bin.from	=	10000	,
bin.selection	=	c(10,20,50,100,200,500,1000)	,
bin.size	=	c(1000000)	,
bin.to	=	10000	,
BINS	=	20	,
FDR.Rate	=	1	,
GTindex	=	NULL	,
inclosure.by	=	10	,
inclosure.from	=	10	,
inclosure.to	=	10	,
LD	=	0.1	,
sangwich.bottom	=	NULL	,
sangwich.top	=	NULL	,
SUPER_GD	=	NULL	,
SUPER_GS	=	FALSE	)


(proc.time()-x)[3]


x=3
catch = 
  tryCatch({print(y); print(x)}, error= function(e){e})
catch
tryCatch({
  stop("demo error")
}, error = function(e) {
  conditionMessage(e) # 這就會是"demo error"
})



  
  

