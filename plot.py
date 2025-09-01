from matplotlib import pyplot as plt
import pandas as pd

df = pd.read_csv("output_times.csv", index_col=0)
df["speed up"] = df.iloc[0, 0] / df["ms"]
df["speed up"].plot.bar(title="Speed Up")