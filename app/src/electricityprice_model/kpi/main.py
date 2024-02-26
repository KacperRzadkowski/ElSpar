from pyjstat import pyjstat
from datetime import date, timedelta
from calendar import monthrange


def daterange(start_date, end_date):
    for n in range(int((end_date - start_date).days)):
        yield start_date + timedelta(n)


start_date = date(2015, 1, 1)
end_date = date(2023, 3, 1)

url = "https://data.ssb.no/api/v0/dataset/130297.json?lang=no"
dataset = pyjstat.Dataset.read(url)
df = dataset.write("dataframe")

energivarer = df.query('Leveringssektor=="KPI-JA Energivarer" & \
    statistikkvariabel=="KPI-JA og KPI-JAE for varer og tjenester, etter leveringssektor (2015=100). Månedlig"')

print(energivarer.filter(items=["måned", "value"]))

f = open("kpi.tsv", "w")

for current_date in daterange(start_date, end_date):
    _ , days_in_month = monthrange(current_date.year, current_date.month)
    start_month_date = date(current_date.year, current_date.month, 1)
    next_month_date = start_month_date + timedelta(days_in_month)
    val = energivarer.query(f'måned=="{start_month_date.year}M{start_month_date.month:02}"').iloc[0]["value"]
    next_val = energivarer.query(f'måned=="{next_month_date.year}M{next_month_date.month:02}"').iloc[0]["value"]
    increment = (next_val-val)*(current_date.day/days_in_month)
    for i in range(24):
        f.write(f"{current_date} {i:02}:00:00\t{'{:.4f}'.format((val+increment)/100)}\n")

f.close()