from datetime import date, timedelta, datetime
from decimal import *

def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)

f = open("valutakurs.csv")
exr = {}
start_date = date(2015, 1, 1)
end_date = date.today()

for line in f:
    entry = line.strip().split(";")
    date = datetime.strptime(entry[0], "%d/%m/%Y").date()
    exr[date] = entry[1]

f.close()

for date in daterange(start_date, end_date):
    d = 0
    while date - timedelta(d) not in exr.keys():
        d += 1
    exr[date] = exr[date - timedelta(d)]

eur = open("NO1_EUR.tsv")
nok = open("NO1.tsv", "w")

for line in eur:
    entry = line.strip().split('\t')
    print(entry)
    time = datetime.strptime(entry[0].replace("+00:00", ""), "%Y-%m-%d %H:00:00")
    nok.write(f"{time}\t{'{:.4f}'.format(float(entry[1])*float(exr[time.date()].replace(',', '.')))}\n")

eur.close()
nok.close()