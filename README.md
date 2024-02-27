# © 2023 Eirik Ravnkleven Tøndel, Erik Orten, Sebastian Koranda, Elise Evjen, Iver Korvald, Kacper Sradkowski 
# Institutt for informatikk, Universitetet i Oslo

The is an Android app developed by the students mentioned above. The app code is written in Kotlin, while the machine learning is done in Python. UI components are designed with Jetpack Compose.

The app Elektra's purpose is to let users plan their power consumption based on visual illustrations. The graph shows both published electricity prices and a forecast for the next 1-2 days in the future that are currently unknown. The forecast uses machine learning to predict the upcoming day's electricity price based on data from some of the factors we believe have a big impact on electricity prices. Another feature is the 'Strømkalkulator', which calculates how much everyday tasks at home will cost at the moment. The user is able to adjust the time parameter for the task. This gives the user a more understandable relationship with the costs of daily tasks in Norwegian kroner.

@@ RECOMMENDED API LEVEL ON ANDROID EMULATOR: minimum API level 26, Android version 8 (Oreo) or newer @@
Internal libraries used:
- Jetpack Compose
- Serializiation
- Coroutines
- Navigation
- LifeCycle
- Java LocalDateTime
- ProtoDatastore
External libraries used:
- TensorFlow Lite: integration of machine learning model
- Ktor: for handling API calls
- Vico: Display data as graphs
APIs used:
- Strømpris API https://www.hvakosterstrommen.no/strompris-api
- Location Forecast https://api.met.no/weatherapi/locationforecast/2.0/documentation
- Frost https://frost.met.no/index.html
- SSB KPI-index https://data.ssb.no/api/v0/dataset/1118?lang=no
