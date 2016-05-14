# ParallelizeHeatmap
Parallelize Heatmap Visualization using Spark.
尝试着用Apache Spark来处理数据，来绘制图像（heatmap，热力图），结果是有的，但是觉得这样的代码违反了使用Apache Spark的初衷，特别是数据点的分配使用了collect()算子，以后有待改进。可视化结果在这：https://github.com/yuanzhaokang/leaflet_heatmap.git.
