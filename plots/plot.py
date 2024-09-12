import pandas as pd
import matplotlib.pyplot as plt

# Load the benchmark data from the text file
data = pd.read_csv('../stats/benchmark_results.txt')

# Pivot the data to have buffer size as the index and prefix type as the columns
execution_time_data = data.pivot(index='BufferSize', columns='PrefixType', values='AverageTime(ms)')
speedup_data = data.pivot(index='BufferSize', columns='PrefixType', values='Speedup')

# Plot execution time as a bar graph
execution_time_data.plot(kind='bar', figsize=(10, 6))
plt.title('Average Execution Time vs Buffer Size for Different Prefix Types')
plt.xlabel('Buffer Size')
plt.ylabel('Average Execution Time (ms)')
plt.legend(title='Prefix Type')
plt.xticks(rotation=0)
plt.tight_layout()
plt.savefig('execution_time_plot.png')  # Save the plot
plt.show()

# Plot speedup as a bar graph
speedup_data.plot(kind='bar', figsize=(10, 6))
plt.title('Speedup vs Buffer Size for Different Prefix Types')
plt.xlabel('Buffer Size')
plt.ylabel('Speedup')
plt.legend(title='Prefix Type')
plt.xticks(rotation=0)
plt.tight_layout()
plt.savefig('speedup_plot.png')  # Save the plot
plt.show()