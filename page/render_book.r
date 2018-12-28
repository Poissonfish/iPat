# system("rm _main.Rmd")
library(data.table)
library(magrittr)
bookdown::render_book("index.Rmd", "bookdown::gitbook")
