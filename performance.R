library(ggplot2)
library(data.table)

gwasipat = fread("/Users/Poissonfish/Desktop/test/FarmCPU_iPat.txt")
gwasori = fread("/Users/Poissonfish/Desktop/test/FarmCPU_Ori.txt")
gsipat = fread("/Users/Poissonfish/Desktop/test/rrBLUP_iPat.txt")
gsori = fread("/Users/Poissonfish/Desktop/test/rrBLUP_Ori.txt")
gwas = data.frame(iPat = gwasipat$P.value, 
    			  FarmCPU = gwasori$P.value)
gs = data.frame(iPat = gsipat$u, 
				rrBLUP = gsori$u)
platformc = factor(c("iPat", "FarmCPU"), levels = c("iPat", "FarmCPU"))
platformd = factor(c("iPat", "rrBLUP"), levels = c("iPat", "rrBLUP"))
data.c = data.frame(time = c(35.866, 28.826), 
					Platform = platformc)
data.d = data.frame(time = c(5.016, 3.513), 
					Platform = platformd)
ga = ggplot(data = gwas, aes(x = iPat, y = FarmCPU)) + geom_point(alpha = .05) + ggtitle("a") +
	scale_x_continuous(name = "-LogP by iPat") + scale_y_continuous(name = "-LogP by FarmCPU")
gb = ggplot(data = gs, aes(x = iPat, y = rrBLUP)) + geom_point(alpha = .5) + ggtitle("b") +
	scale_x_continuous(name = "GEBV by iPat") + scale_y_continuous(name = "GEBV by rrBLUP")
gc = ggplot(data = data.c, aes(x = Platform, y = time, fill = Platform)) + 
	geom_bar(stat="identity", position = "dodge") + ggtitle("c") +
	scale_y_continuous(name = "Computing time (sec)", limits = c(0, 40)) 	
gd = ggplot(data = data.d, aes(x = Platform, y = time, fill = Platform)) + 
	geom_bar(stat="identity", position = "dodge") + ggtitle("d") +
	scale_y_continuous(name = "Computing time (sec)", limits = c(0, 40)) 	
ggsave(plot = ga, file = "ga.png")
ggsave(plot = gb, file = "gb.png")
ggsave(plot = gc, file = "gc.png") 
ggsave(plot = gd, file = "gd.png") 





