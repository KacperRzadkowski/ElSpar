import datetime
from dateutil import parser

input_data = open("NO1_output.tsv")
output_data = open("NO1_formatted.tsv", "w")

for line in input_data:
    data = line.strip().split("\t")
    try:
        date = datetime.datetime.strptime(data[0], "%d/%m/%Y %H:%M")
    except ValueError:
        date = parser.parse(data[0], fuzzy=True)
    output_data.write(f"{date}\t{data[1]}\n")

input_data.close()
output_data.close()
