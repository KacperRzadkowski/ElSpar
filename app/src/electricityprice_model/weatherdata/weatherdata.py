import requests
from datetime import date
from collections import defaultdict
from statistics import mean

start_date = date(2015, 1, 1)
end_date = date.today()

elements = ["air_temperature", "precipitation_amount", "wind_speed"]

areas = {}
areas["NO1"] = ["SN18700"]#, "SN12680"] #oslo - blindern, lillehammer - sætherengen
areas["NO2"] = ["SN44640"]#, "SN39040"] #stavanger - våland, kjevik
areas["NO3"] = ["SN68125"]#,"SN68230", "SN62290"] #trondheim - sverresborg, trondheim - risvollan, molde - nøisomhed
areas["NO4"] = ["SN82410"]#,"SN82310", "SN90495", "SN94280"] #bodø - helligvær ii, bodø - skivika, tromsø - stakkevollan, hammerfest lufthavn
areas["NO5"] = ["SN50500"]#, "SN50810", "SN51800"] #bergen - flesland, bergen - åsane, voss - mjølfjell


api_key = "48380fc6-bed9-4253-a039-488eb2431968"
url = "https://gw-uio.intark.uh-it.no/in2000/frostapi/observations/v0.jsonld"

headers = {"X-Gravitee-API-Key": api_key}
params = {"levels": "default", "timeresolutions": "PT1H", "qualities": "0"}

for area in areas:
    for element in elements:
        if element == "precipitation_amount":
            params["elements"] = "sum(precipitation_amount PT1H)"
        else:
            params["elements"] = element

        results = defaultdict(list)
        for station in areas[area]:
            params["sources"] = station
            for year in range(start_date.year, end_date.year):
                reference_time = f"{year}-01-01/"
                if year == start_date.year:
                    reference_time = f"{year}-{start_date.month:02}-{start_date.day:02}/"
                if year == (end_date.year-1):
                    reference_time += f"{end_date.year}-{end_date.month:02}-{end_date.day:02}"
                else:
                    reference_time += f"{year+1}-01-01"
                params["referencetime"] = reference_time
                response = requests.get(url, headers=headers, params=params).json()
                for entry in response["data"]:
                    results[entry['referenceTime']].append(entry['observations'][0]['value'])
        
        f = open(f"{element}/{area}.tsv", "w")
        for date in results:
            f.write(f"{date}\t{round(mean(results[date]), 2)}\n")
        f.close()
