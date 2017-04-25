<h1 style="text-align:center">User Manual for</h1>

<p align="center"><img src = "./res/icon.png" width = 200></p>

<h1 style="text-align:center">Intelligent Prediction and Association Tool</h1>

<h3 style="text-align:center">(Version 1.1)</h3>
<h3 style="text-align:center">Last updated on Apr 24, 2017</h3>

<br><br><br>

<div style="page-break-after: always;"></div>

# Table of Contents
#### 1. [Getting start](#get_start)
#### 2. [Interface](#interface)
> 2-1 [Import files](#import_files)

> 2-2 [Create a project](#create_projects)

> 2-3 [File formats](#file_format)

> 2-4 [Covariates and kinship](#C_K)

> 2-5 [Define input arguments](#input)

> 2-6 [Run an analysis](#run)

> 2-7 [Check the results](#check)

> 2-8 [Files remove](#delete)

#### 3. [GWAS and GS](#gwas)
> 3-1 [GAPIT](#gapit)

> 3-2 [FarmCPU](#farm)

> 3.3 [PLINK](#plink)

> 3.4 [rrBLUP](#rrblup)

#### 4. [Support](#support)
#### 5. [Citation](#cite)


<br><br><br>

<a name="get_start"></a>
## 1. Getting start 
* Before launching iPat, remember to place folder 'libs' to the path where 'iPat.jar' exists. iPat can function normally only when it is in the same folder as ‘libs’.

	* Operation System: Mac OS X.
	* [Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html): Version 8 or later.
	* [R](https://www.r-project.org): Version 3.4.0 or later. 


<p align="center"><p align="center"><img src = "./res/libs.png" width = 700></p>

<a name="interface"></a>
## 2. Interface

<a name="import_files"></a>
### 2.1 *Import files*


<p align="center"><img src = "./res/dnd.png" width = 700></p>

<a name="create_projects"></a>
### 2.2 *Create a project*
* After importing the files, double clicking on anywhere in iPat to create a new project.

<p align="center"><img src = "./res/linkages.png" width = 250></p>

<a name="file_format"></a>
### 2.3 *File formats*
* iPat can recognize and work fine with different formats, which include hapmap, numeric, vcf and plink.

* ***Imported file set need to have identical names and correct extension name if they need to be converted to a proper format.*** For example, if you want to perform GWAS using VCF format in FarmCPU, files set should be named as: data.vcf and data.txt. The table below shows examples of files and its extension name for the corresponded format:

<center>

|Format |Genotype|Phenotype|Other information|
|:-----:|:------:|:-------:|:---------------:|
|Hapmap |.hmp    |.txt     |None  	        |  
|Numeric|.dat    |.txt     |.map 			     |
|VCF    |.vcf    |.txt     |None             |
|PLINK  |.bed    |.txt     |.fam .bim        |

</center>



<a name="input"></a>
### 2.5 *Define input arguments*



<p align="center"><img src = "./res/config.png" width = 400></p>

<a name="run"></a>
* After defining the analysis, user can start to run the procedure by clicking ‘GO’ at the top of the panel.

<a name="check"></a>
### 2.7 *Check the result*
* When iPat complete a project, the gear icon will show a green dot if the task run successfully without any error occurred. Otherwise it will show a red dot at its top-left to notify users that there’re existing at least one error message during the analysis.

<p align="center"><img src = "./res/indicator.png" width = 700></p>
* Users can check the result by double clicking on the gear icon, which will directe users to the folder where the output files generated.

<p align="center"><img src = "./res/output.png" width = 500></p>

### 2.8 *Files remove*
* Users can remove objects and linkage by typing “Del” after selecting 

* For linkages, the line will become solid when it’s selected. 

* For objects, there will be a dashed line surrounded to indicate that the object is selected.

* Users can also drag the linkages or objects to the bottom-right corner, a hidden trashcan will show up for deletion.

<p align="center"><img src = "./res/delete.png" width = 500></p>

<a name="gwas"></a>
Tools implemented in iPat allow users to do genome-wide associate study (GWAS) and genomic selection (GS). Curretly GWAS can be performed by GAPIT, FarmCPU and PLINK, and GS can be done by GAPIT and rrBLUP in iPat. Tables below are the input arguments available in iPat:

<a name="gapit"></a>
### 3.1 GAPIT
|Category|Parameters|	Definitions| Default|
|:--|:---|:---|:--:|
|Subset|Subset of traits data|Users can select all or partial of traits to be analyzed|All traits|
|Subset|Subset of chromosomes|Users can select all or partial of chromosomes to be analyzed|All
|Covariates|PCA.count|How many of PCs should be treated as covariates|3
|Quality control|By MAF|Users can do a quality control on the marker set by minor allele frequency (MAF).|NULL

<a name="farm"></a>
### 3.2 FarmCPU
|Category|Parameters|	Definitions| Default|
|:--|:---|:---|:--:|
|Subset|Subset of traits data|Users can select all or partial of traits to be analyzed|All traits|
|Subset|Subset of chromosomes|Users can select all or partial of chromosomes to be analyzed|All
|Covariates|PCA.count|How many of PCs should be treated as covariates|3
|Quality control|By MAF|Users can do a quality control on the marker set by minor allele frequency (MAF).|NULL
|GWAS|method.bin|It uses fixed or optimized of possible QTN window size and number of possible QTNs selected into FarmCPU model.|static|

<a name="plink"></a>
### 3.3 PLINK
|Category|Parameters|	Definitions| Default|
|:--|:---|:---|:--:|
|Subset|Subset of chromosomes|Users can select all or partial of chromosomes to be analyzed|All
|Quality control|By MAF|Users can do a quality control on the marker set by minor allele frequency (MAF).|NULL
|GWAS|C.I.|The desired coverage for a confidence interval|0.95|

<a name="rrblup"></a>
### 3.4 rrBLUP
|:--|:---|:---|:--:|
|Subset|Subset of traits data|Users can select all or partial of traits to be analyzed|All traits|


<a name="support"></a>
* User can download demo files from [here](http://zzlab.net/iPat/demo.zip).
* If there is any difficulty on iPat, please leave your question in the page of [issue report](https://github.com/Poissonfish/iPat/issues).
* Or you can directly send an email to the auther [James Chen](mailto:chun-peng.chen@wsu.edu)

<a name="cite"></a>
## 5 Citation
* Bradbury,P.J. et al. (2007) TASSEL: software for association mapping of complex traits in diverse samples. Bioinformatics, 23, 2633–2635.