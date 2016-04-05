/**
 * Created by YuanZhaokang on 2016/3/27 0027.
 */

import java.io.File
import java.net.URI
import java.util
import javax.imageio.ImageIO

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.{SparkConf, SparkContext}

object Draw {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Draw").setMaster(Path.localSparkMaster)
    val sc = new SparkContext(conf)

    val core: Int = 16
    val zIndex: Int = 10

    val lngLat = sc.textFile(Path.localHdfsIp + "/p*")
    lngLat.persist()

    Range(10, 15).foreach {
      k =>
        val index: Int = k //层级
      val multi = index - zIndex

        val startX: Int = 851 * Math.pow(2, multi).toInt
        val startY: Int = 418 * Math.pow(2, multi).toInt
        val endX: Int = 854 * Math.pow(2, multi).toInt + multi
        val endY: Int = 421 * Math.pow(2, multi).toInt + multi

        //        val lngLat = sc.textFile(Path.localHdfsIp+ "/p*")
        val pointsInfo = lngLat.map {
          line =>
            val coordinates = line.split(",")
            val lng = coordinates(0).toDouble
            val lat = coordinates(1).toDouble
            val tile = getTileNumber(lng, lat, index)

            val x: Int = tile._1
            val y: Int = tile._2
            val z: Int = tile._3

            val pixel = getPixelCoordinate(x, y, z, lng, lat)
            pixel
        }

        val p = Array.ofDim[util.ArrayList[Point]](endX-startX+1,endY-startY+1)

        for (i <- 0 until endX - startX + 1) {
          for (j <- 0 until endY - startY + 1) {
            p(i)(j) = new util.ArrayList[Point]()
            val points = pointsInfo.filter(p => p._1 == startX + i && p._2 == startY + j).map(p => (p._4, p._5))
            points.collect().map(t => p(i)(j).add(new Point(t._1.toInt, t._2.toInt)))
          }
        }

        sc.parallelize(startX to endX).foreach(
          column =>
            for (row <- startY to endY) {
              val render = new Heatmap()
              val image = render.render(256, 256, 10, 120, p(column - startX)(row - startY))
              val conf = new Configuration()
              val path = new Path(Path.localHdfsIp + index + "/" + column + "/")
              val fs = FileSystem.get(URI.create(path.toString()), conf)
              if (!fs.exists(path))
                fs.mkdirs(path)
              ImageUtils.writeImage(image, path.toString() + "/" + row + ".png")
//              ImageIO.write(image, "png", new File("/home/wind/yuanzk/lnglat/" + row + ".png"))
            }
        )
    }
    sc.stop()
  }

  def getTileNumber(lng: Double, lat: Double, z: Int): Tuple3[Int, Int, Int] = {
    val x: Double = Math.pow(2, z - 1) * (lng / 180 + 1)
    val y: Double = Math.pow(2, z - 1) * (1 - (Math.log(Math.tan(Math.PI * lat / 180) + Math.sqrt(1 + Math.pow(Math.tan(Math.PI * lat / 180), 2)))) / Math.PI)
    (x.toInt, y.toInt, z.toInt)
  }

  def getPixelCoordinate(x: Int, y: Int, z: Int, lng: Double, lat: Double): Tuple5[Int, Int, Int, Double, Double] = {
    val m = ((lng / 180 + 1) / Math.pow(2, 1 - z) - x) * 256
    val n = ((1 - Math.log(Math.tan((lat + 90) * Math.PI / 360)) / Math.PI) / Math.pow(2, 1 - z) - y) * 256
    (x, y, z, m, n)
  }
}
