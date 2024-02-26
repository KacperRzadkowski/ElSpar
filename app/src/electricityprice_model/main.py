import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import tensorflow as tf

from windowgenerator import WindowGenerator

MAX_EPOCHS = 20


def compile_and_fit(model, window, patience=2):
    early_stopping = tf.keras.callbacks.EarlyStopping(monitor='val_loss',
                                                      patience=patience,
                                                      mode='min')

    model.compile(loss=tf.keras.losses.MeanSquaredError(),
                  optimizer=tf.keras.optimizers.Adam(),
                  metrics=[tf.keras.metrics.MeanAbsoluteError()])

    model.fit(window.train, epochs=MAX_EPOCHS,
              validation_data=window.val,
              callbacks=[early_stopping])


mpl.rcParams['figure.figsize'] = (8, 6)
mpl.rcParams['axes.grid'] = False

df = pd.read_table(open('NO1_data.tsv'))
date_time = pd.to_datetime(df.pop('time'), format='ISO8601')

timestamp_s = date_time.map(pd.Timestamp.timestamp)

day = 24 * 60 * 60
year = 365.2425 * day

df['Day sin'] = np.sin(timestamp_s * (2 * np.pi / day))
df['Day cos'] = np.cos(timestamp_s * (2 * np.pi / day))
df['Year sin'] = np.sin(timestamp_s * (2 * np.pi / year))
df['Year cos'] = np.cos(timestamp_s * (2 * np.pi / year))

plt.plot(df)
plt.show()

n = len(df)
train_df = df[0:int(n * 0.9)]
val_df = df[int(n * 0.9):int(n * 0.95)]
test_df = df[int(n * 0.95):]

print("DF", str(train_df))

train_mean = train_df.mean()
train_std = train_df.std()

print("MEAN:" + str(train_mean))
print("STANDARD DEVIATION:" + str(train_std))

train_df = (train_df - train_mean) / train_std
val_df = (val_df - train_mean) / train_std
test_df = (test_df - train_mean) / train_std

window = WindowGenerator(
    input_width=48,
    label_width=1,
    shift=1,
    train_df=train_df,
    val_df=val_df,
    test_df=test_df,
    label_columns=['price'])

lstm_model = tf.keras.models.Sequential([
    tf.keras.layers.LSTM(32),
    tf.keras.layers.Dense(units=1),
    tf.keras.layers.Reshape([1, -1]),
])

print('Input shape:', window.example[0].shape)
print('Output shape:', lstm_model(window.example[0]).shape)

compile_and_fit(lstm_model, window)
lstm_model.trainable = False
lstm_model.save("savedmodel")

window.plot(lstm_model)
plt.show()
