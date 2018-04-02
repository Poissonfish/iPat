map = "/Users/jameschen/IdeaProjects/iPat/target/classes/test.txt"
map2 = "/Users/jameschen/IdeaProjects/iPat/target/classes/test2.txt"
d1 = fread(map)
d2 = fread(map2)

index = c(2,5,3,1,4)
dt2 = data.table(d2)
dt2[ ,p.value := d1]
dt2$cov2[is.na(dt2$cov2)] = 1

order = order(dt2$p.value)
dt2[order]
indexSig = which(dt2$p.value > 99)
length(indexSig)
nrow(dt2)

c = NULL
data.frame(c[, 1:30])

GAPIT
arg = c("/usr/local/bin/Rscript", "/Users/jameschen/IdeaProjects/iPat/target/classes/iPatGAPIT.r",
"-wd", "/Users/jameschen/Desktop/Test",
"-project", "Module 1",
"-pSelect", "ExcludedsepExcludedsepSelectedsep",
"-maf", "0.05",
"-ms", "0.2", "-format", "Numeric",
"-cSelect", "NA",
"-arg", "GLM", "average", "Mean", "1", "FALSE",
"-phenotype", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.txt",
"-genotype", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.dat",
"-map", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.map",
"-cov", "/Users/jameschen/IdeaProjects/iPat/NA",
"-kin", "/Users/jameschen/IdeaProjects/iPat/NA")

FarmCPU
arg = c("/usr/local/bin/Rscript", "/Users/jameschen/IdeaProjects/iPat/target/classes/iPatFarmCPU.r",
"-wd", "/Users/jameschen/Desktop/Test",
"-project", "Module 1",
"cSelect", "NA",
"-pSelect", "ExcludedsepExcludedsepSelectedsep",
"-maf", "0.05",
"-ms", "0.2", "-format", "Hapmap",
"-arg", "static", "10",
"-phenotype", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.txt",
"-genotype", "/Users/jameschen/Dropbox/iPat/demo_data/Hapmap/data_recode.dat", 
"-map", "/Users/jameschen/Dropbox/iPat/demo_data/Hapmap/data_recode.nmap",
"-cov", "/Users/jameschen/IdeaProjects/iPat/NA",
"-kin", "/Users/jameschen/IdeaProjects/iPat/NA")

  Command : , -maf, 0.05, -ms, 0.2, -format, Hapmap, -arg, static, 10, -phenotype,
  /Users/jameschen/Dropbox/iPat/demo_data/Hapmap/data.txt,
  -genotype, /Users/jameschen/Dropbox/iPat/demo_data/Hapmap/data_recode.dat,
  -map, /Users/jameschen/Dropbox/iPat/demo_data/Hapmap/data_recode.nmap, -cov, /Users/jameschen/IdeaProjects/iPat/NA, -kin, /Users/jameschen/IdeaProjects/iPat/NA]

)

gblup
arg = c("/usr/local/bin/Rscript", "/Users/jameschen/IdeaProjects/iPat/target/classes/iPatgBLUP.r",
"-wd", "/Users/jameschen/Desktop/Test",
"-project", "Module 1",
"-pSelect", "ExcludedsepExcludedsepSelectedsep",
"-maf", "0.05",
"-ms", "0.2", "-format", "Numeric",
"-cSelect", "NA",
"-gwas", "TRUE", "0.0001",
"-arg", "1", "FALSE",
"-phenotype", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.txt",
"-genotype", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.dat",
"-map", "/Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.map",
"-cov", "/Users/jameschen/IdeaProjects/iPat/NA",
"-kin", "/Users/jameschen/IdeaProjects/iPat/NA")


gblup
   Command : [/usr/local/bin/Rscript,
   /Users/jameschen/IdeaProjects/iPat/target/classes/iPatgBLUP.r,
    -wd, /Users/jameschen/Desktop/Test, -project, Module 1,
    -pSelect, ExcludedsepExcludedsepSelectedsep,
    -maf, 0.05, -ms, 0.2, -format, Numeric,
    -cSelect, NA, -gwas, TRUE, 5, -arg, 1, False, -phenotype, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.txt, -genotype, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.dat, -map, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.map, -cov, /Users/jameschen/IdeaProjects/iPat/NA, -kin, /Users/jameschen/IdeaProjects/iPat/NA]
