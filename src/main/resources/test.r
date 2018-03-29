/usr/local/bin/Rscript,
/Users/jameschen/IdeaProjects/iPat/target/classes/iPatFarmCPU.r, -wd, /Users/jameschen/Desktop/Test, -project, abc, -pSelect, SelectedsepSelectedsepSelectedsep, -maf, 0.05, -ms, 0.2, -format, Hapmap, -cSelect, NA, -arg, static, 10, -phenotype, /Users/jameschen/Downloads/demo_data/Hapmap/data.txt, -genotype, /Users/jameschen/Downloads/demo_data/Hapmap/data.hmp, -map, /Users/jameschen/IdeaProjects/iPat/NA, -cov, /Users/jameschen/IdeaProjects/iPat/NA, -kin, /Users/jameschen/IdeaProjects/iPat/NA]

/usr/local/bin/Rscript, /Users/jameschen/IdeaProjects/iPat/target/classes/iPatFarmCPU.r,
-wd, /Users/jameschen/Desktop/Test,
-project, abc,
-pSelect, SelectedsepSelectedsepSelectedsep,
-maf, 0.05,
-ms, 0.2,
-format, Hapmap,
-cSelect, NA,
-arg, static, 10,
-phenotype, /Users/jameschen/Downloads/demo_data/Hapmap/data.txt, -genotype, /Users/jameschen/Downloads/demo_data/Hapmap/data.hmp, -map, /Users/jameschen/IdeaProjects/iPat/NA, -cov, /Users/jameschen/IdeaProjects/iPat/NA, -kin, /Users/jameschen/IdeaProjects/iPat/NA]
Mouse exit



AA = c('-wd', 'jd', '-j', 'abc')

switch(AA[],
  -wd ={
# case 'foo' here...
  print('-wd')
},
-j={
# case 'bar' here...
  print('bar')
},
{
 print('default')
}
)
