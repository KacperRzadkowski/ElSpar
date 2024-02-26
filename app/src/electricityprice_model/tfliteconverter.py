import tensorflow as tf

directory = "savedmodel"
converter = tf.lite.TFLiteConverter.from_saved_model(directory)
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS, tf.lite.OpsSet.SELECT_TF_OPS]
converter._experimental_lower_tensor_list_ops = False
tflite_model = converter.convert()

with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
