import requests
from datetime import date, timedelta, timezone
from dateutil import parser

def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)

start_date = date(2021, 12, 1)
end_date = date.today()
areas = ["NO1", "NO2", "NO3", "NO4", "NO5"]

for area in areas:
    f = open(f"{area}_output.tsv", "a")
    for date in daterange(start_date, end_date):
        url = f"https://www.hvakosterstrommen.no/api/v1/prices/"\
            + f"{date.year}/"                                   \
            + f"{date.month:02}-"                               \
            + f"{date.day:02}_"                                 \
            + f"{area}.json"
        print(url)
        response = requests.get(url).json()
        for entry in response:
            time = parser.parse(entry['time_start'], fuzzy=True)
            time_utc = time.astimezone(timezone.utc)
            f.write(f"{time_utc}\t{entry['EUR_per_kWh']}\n")
    f.close()
