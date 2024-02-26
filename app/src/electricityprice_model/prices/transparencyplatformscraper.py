import requests
import urllib.parse
import time
from datetime import date, timedelta, timezone
from dateutil import parser
from bs4 import BeautifulSoup

def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)

start_date = date(2021, 11, 30)
end_date = date(2021, 12, 1)
areas = ["NO1"]#, "NO2", "NO3", "NO4", "NO5"]

url = "https://transparency.entsoe.eu/transmission-domain/r2/dayAheadPrices/show"

params = {}
params["name"] = ""
params["defaultValue"] = "false"
params["viewType"] = "TABLE"
params["areaType"] = "BZN"
params["atch"] = "false"
params["resolution.values"] = "PT60M"
params["dateTime.timezone"] = "UTC"
params["dateTime.timezone_input"] = "UTC"

for area in areas:
    f = open(f"{area}_duplicates.tsv", "a")
    for date in daterange(start_date, end_date):
        while True:
            try:
                if area == "NO1":
                    params["biddingZone.values"] = "CTY|10YNO-0--------C!BZN|10YNO-1--------2"
                elif area == "NO2":
                    params["biddingZone.values"] = "CTY|10YNO-0--------C!BZN|10YNO-2--------T"
                elif area == "NO3":
                    params["biddingZone.values"] = "CTY|10YNO-0--------C!BZN|10YNO-3--------J"
                elif area == "NO4":
                    params["biddingZone.values"] = "CTY|10YNO-0--------C!BZN|10YNO-4--------9"
                else:
                    params["biddingZone.values"] = "CTY|10YNO-0--------C!BZN|10Y1001A1001A48H"
                params["dateTime.dateTime"] = f"{date.day:02}.{date.month:02}.{date.year}+00:00|UTC|DAY"
                response = requests.get(url, params=urllib.parse.urlencode(params, safe='+!'))
                print(response.url)
                soup = BeautifulSoup(response.text, 'html.parser')
                table = soup.find('table')
                for row in table.tbody.find_all('tr'):
                    columns = row.find_all('td')
                    if columns != []:
                        f.write("" + date.strftime('%Y-%m-%d') + " " + columns[0].text.strip().split()[0] + "\t" + "{:.4f}".format(float(columns[1].text.strip())/1000) + "\n")
            except AttributeError:
                time.sleep(600)
                continue
            break
    f.close()

