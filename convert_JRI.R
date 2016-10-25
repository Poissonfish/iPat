wd <- "/Users/Poissonfish/git/iPat"
file <- "FarmCPU.R"

library(magrittr)
setwd(wd)
data <- readLines(paste0(wd, '/',file))

for (i in 1:length(data))
{
  write(data[i] %>% gsub("\"","\'", .) %>% paste("r.eval(",.,");", sep='"'), 
        paste0(wd,'/J_',file), 
        sep= "\n", append = TRUE)
}