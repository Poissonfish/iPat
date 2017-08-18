Usage: java -jar ipat_converter.jar [input-format] [output-format] [path to data]

Option for input and output format:
	—-num		Numeric format, individuals listed as rows
	—-hmp		Hapmap 
	—-vcf		VCF
	—-plink 	PLINK format (.ped and .map)
Options for path to data:
	-GD Path to input genotype data 
	-GM Path to input map data (Only required for num and plink)

Example 1: To convert files from numeric to hapmap:
	java -jar ipat_converter.jar —-num —-hmp —GD data.dat —GM data.map
Example 2: To convert files from vcf to plink:
	java -jar ipat_converter.jar —-vcf —-plink —GD data.vcf