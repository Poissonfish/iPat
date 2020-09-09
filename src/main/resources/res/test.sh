Rscript \
"iPatLauncher.r" \
"FarmCPU" \
-arg optimum 10 \
-cSelect NA \
-wd "~" \
-project Project \
-phenotype "demo.txt" \
-pSelect y25sepy50sepy75sep \
-cov NA \
-kin NA \
-genotype "demo_recode.dat" \
-map "demo_recode.nmap"


/usr/local/bin/Rscript 
/Users/jameschen/IdeaProjects/iPat/target/res/iPatLauncher.r 
rrBLUP
-gs FALSE 3 10 -gwas FALSE 
-cSelect C1sepC2sep 
-wd /Users/jameschen 
-project ProjectGS 
-phenotype /Users/jameschen/Dropbox/iPat/demo/Case 3/demo.txt 
-pSelect y25sepy50sepy75sep 
-cov /Users/jameschen/Dropbox/iPat/demo/demo.cov 
-kin NA 
-genotype /Users/jameschen/Dropbox/iPat/demo/Case 3/demo_recode.dat 
-map /Users/jameschen/Dropbox/iPat/demo/Case 3/demo_recode.nmap]


/usr/local/bin/Rscript 
/Users/jameschen/IdeaProjects/iPat/target/res/iPatLauncher.r 
BGLR
-arg BRR 5000 3000 -gs FALSE 3 10 -gwas FALSE 
-cSelect C1sepC2sep 
-wd /Users/jameschen 
-project Project 
-phenotype /Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt 
-pSelect y25sepy50sepy75sep 
-cov /Users/jameschen/Dropbox/iPat/demo/demo.cov 
-kin NA 
-genotype /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat 
-map /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap]


/usr/local/bin/Rscript 
/Users/jameschen/IdeaProjects/iPat/target/res/iPatLauncher.r 
rrBLUP
-gs TRUE 5 10 -gwas FALSE 
-cSelect NA 
-wd /Users/jameschen 
-project ProjectCV 
-phenotype /Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt 
-pSelect y25sepy50sepy75sep 
-cov NA 
-kin NA 
-genotype /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat 
-map /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap]





# IPatCommand : [/usr/local/bin/Rscript, 
# /Users/jameschen/IdeaProjects/iPat/target/res/iPatrrBLUP.r, 
# -gs, FALSE, 3, 10, -gwas, FALSE, 
# -cSelect, C1sepC2sep, 
# -wd, /Users/jameschen, 
# -project, ProjectGS, 
# -phenotype, /Users/jameschen/Dropbox/iPat/demo/Case 3/demo.txt, 
# -pSelect, y25sepy50sepy75sep, 
# -cov, /Users/jameschen/Dropbox/iPat/demo/demo.cov, 
# -kin, NA, 
# -genotype, /Users/jameschen/Dropbox/iPat/demo/Case 3/demo_recode.dat, 
# -map, /Users/jameschen/Dropbox/iPat/demo/Case 3/demo_recode.nmap]

# IPatCommand : [/usr/local/bin/Rscript, 
# /Users/jameschen/IdeaProjects/iPat/target/res/iPatrrBLUP.r, 
# -gs, TRUE, 5, 10, -gwas, FALSE, 
# -cSelect, NA, 
# -wd, /Users/jameschen, 
# -project, ProjectCV, 
# -phenotype, /Users/jameschen/Dropbox/iPat/demo/Case 1/demo.txt, 
# -pSelect, y25sepy50sepy75sep, 
# -cov, NA, 
# -kin, NA, 
# -genotype, /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.dat, 
# -map, /Users/jameschen/Dropbox/iPat/demo/Case 1/demo_recode.nmap]

