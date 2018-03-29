map = "/Users/jameschen/IdeaProjects/iPat/target/classes/test.txt"
map2 = "/Users/jameschen/IdeaProjects/iPat/target/classes/test2.txt"
gd = fread(map2)
MAF = apply(gd, 2, mean) %>%
      as.matrix() %>%
      apply(1, function(x) min(1 - x/2, x/2))
gd[is.na(gd)] = 1

gd

Command : [/usr/local/bin/Rscript, /Users/jameschen/IdeaProjects/iPat/target/classes/iPatGAPIT.r,
-wd, /Users/jameschen/Desktop/Test, -project, gate,
 -pSelect, SelectedsepSelectedsepSelectedsep, -maf, 0.05, -ms, 0.2,
 -format, Numeric, -cSelect, NA,
 -arg, GLM, average, Mean, 1, FALSE,

 -phenotype, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.txt,
 -genotype, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.dat,
  -map, /Users/jameschen/Dropbox/iPat/demo_data/Numeric/data.map,
   -cov, /Users/jameschen/IdeaProjects/iPat/NA, -kin, /Users/jameschen/IdeaProjects/iPat/NA]
