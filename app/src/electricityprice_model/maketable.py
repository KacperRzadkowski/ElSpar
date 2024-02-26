areas = ["NO1"]#, "NO2", "NO3", "NO4", "NO5"]

for area in areas:
    prices = open(f"prices/{area}.tsv")
    kpi = open(f"kpi/kpi.tsv")
    air_temperature = open(f"weatherdata/air_temperature/{area}.tsv")
    precipitation_amount = open(f"weatherdata/precipitation_amount/{area}.tsv")
    wind_speed = open(f"weatherdata/wind_speed/{area}.tsv")
    output = open(f"{area}_data.tsv", "w")
    
    output.write("time\tair_temperature\tprecipitation_amount\twind_speed\tprice\tkpi\n")
    for price, kpi, temp, prec, wind in zip(
            prices, kpi, air_temperature, 
            precipitation_amount, wind_speed):
        output.write(str(price.strip().split('\t')[0].replace("+00:00", ""))+'\t'\
            + str(temp.strip().split('\t')[1])+'\t'\
            + str(prec.strip().split('\t')[1])+'\t'\
            + str(wind.strip().split('\t')[1])+'\t'\
            + str(price.strip().split('\t')[1])+'\t'\
            + str(kpi.strip().split('\t')[1]+'\n'))

    output.close()
