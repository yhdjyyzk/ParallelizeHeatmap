/**
 * Created by yuan on 16-1-3.
 */

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.spark.{SparkConf, SparkContext}

object DataProcess {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("dataProcess").setMaster(Path.localSparkMaster)
    val sc = new SparkContext(conf)
    val huzhouData = sc.textFile("hdfs://localhost:9000/huzhou_cell_records/HuZhou.csv").map(line => line.split(",")).collect()
    val bHuzhou = sc.broadcast(huzhouData)

    val config = new Configuration()
    config.addResource(new Path(Path.localConfPath + "/core-site.xml"))
    config.addResource(new Path(Path.localConfPath + "/hdfs-site.xml"))
    config.addResource(new Path(Path.localConfPath + "/yarn-site.xml"))
    val fs = FileSystem.get(config)
    val list = fs.listStatus(new Path("hdfs://localhost:9000/huzhou_cell_records/"))


    for (f <- list) {
      if (!f.getPath().toString().endsWith(".csv")) {
        val data = sc.textFile(f.getPath().toString())

        val dataHuZhou = data.flatMap(line => line.split("\\|", -1))
          .filter(line => line != "")
          .map(line => line.split(";", -1))
          .filter(city => city(34).trim != "" && city(35).trim != "")
          .map(city => (city(34).trim, city(35).trim))

        val dataProcess = dataHuZhou.map {
          t =>
            val huzhou = bHuzhou.value
            var i: Int = 0
            var str: String = null
            var flag: Boolean = true
            while (i < huzhou.length && flag) {
              if (t._1 == huzhou(i)(2) && t._2 == huzhou(i)(3)) {
                str = huzhou(i)(5) + "," + huzhou(i)(6)
                flag = false
              }
              i += 1
            }
            str
        }

        val fileName = f.getPath().toString().split("/")(f.getPath().toString().split("/").length - 1)
        val newPath = new Path("hdfs://localhost:9000/huzhou_cell_records_lnglat/" + fileName)
        dataProcess.filter(line => line != null).saveAsTextFile(newPath.toString())
      }
    }

    sc.stop()
  }
}
