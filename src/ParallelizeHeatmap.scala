//import org.apache.spark.sql.SQLContext
//import org.apache.spark.{SparkConf, SparkContext}
//
//import scala.util.control.Breaks
//
///**
// * Created by yuan on 15-12-30.
// */
//case class City(city1: String, city2: String, station_lng: String, station_lat: String, location: String, lng: String, lat: String)
//
//object ParallelizeHeatmap {
//  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("ParallelizeHeatmap").setMaster("spark://localhost:7077")
//    val sc = new SparkContext(conf)
//    val sqlContext = new SQLContext(sc)
//    //    val sqlContext = new SQLContext(sc)
//
//    //    val data=sc.textFile("hdfs://localhost:9000/zhejiang_cell_records/BS_n_utf8.csv")
//    //    val tbl_city=data.map(line=>line.split(",",-1)).map(c=>City(c(0),c(1),c(2),c(3),c(4),c(5),c(6))).toDF()
//    //    tbl_city.registerTempTable("tbl_city")
//    //
//    //    val result=sqlContext.sql("select * from tbl_city where city1='台州'")
//    ////    result.toJSON.saveAsTextFile()
//    //    result.foreach(println)
//    ////    result.rdd ++ result.rdd
//    ////    sc.parallelize(List((1,2),(3,4))).map(t=>t._1)
//    //
//    //    val hz_city=sqlContext.read.json("hdfs://localhost:9000/zhejiang_cell_records/city_info/hz_city")
//    //    hz_city.registerTempTable("hz_city")
//    //
//    //    val data_hz=sc.textFile("hdfs://localhost:9000/zhejiang_cell_records/12-22/*/*HUZ*rtt*")
//    //    val data_hz_filter=data_hz.flatMap(line=>line.split("\\|",-1))
//    //                              .filter(line=>line!="")
//    //                              .map(line=>line.split(";",-1))
//    //                              .filter(city=>city(34).trim !="" && city(35).trim !="")
//    //                              .map(city=>(city(34).trim,city(35).trim))
//    //
//    ////    case class LngLat(lng:String,lat:String)
//    ////    val tbl_data_hz_filter=data_hz.flatMap(line=>line.split("\\|",-1)).filter(line=>line!="").map(line=>line.split(";",-1)).filter(city=>city(34).trim !="" && city(35).trim !="").map(city=>LngLat(city(34),city(35))).toDF()
//    ////    tbl_data_hz_filter.foreach(r=>sqlContext.sql("select lat,lng from hz_city where station_lat='"+r(0)+"' and station_lng='"+r(1)+"'"))
//    //
//    ////    val hz_lngLat=data_hz_filter.collect.map(t=>sqlContext.sql("select lat,lng from hz_city where station_lat='"+t._1+"' and station_lng='"+t._2+"'"))
//
//    //    val hz_city=sqlContext.read.json("hdfs://localhost:9000/zhejiang_cell_records/city_info/hz_city")
//    //    hz_city.registerTempTable("hz_city")
//    //    //hz_city.printSchema()
//    //
//    //    case class LngLat(lng:String,lat:String)
//    //
//    //    val data_hz=sc.textFile("hdfs://localhost:9000/zhejiang_cell_records/12-22/*HUZ*rtt*")
//    //
//    //    val data_hz_filter=data_hz.flatMap(line=>line.split("\\|",-1)).filter(line=>line!="").map(line=>line.split(";",-1)).filter(city=>city(34).trim !="" && city(35).trim !="").map(city=>(city(34).trim,city(35).trim))
//    //
//    //    //val hz_lngLat=data_hz_filter.collect().map(t=>sqlContext.sql("select lat,lng from hz_city where station_lat='"+t._1+"' and station_lng='"+t._2+"'"))
//    //    val hz_city_array=hz_city.rdd.collect
//    //
//    //    import util.control.Breaks._
//    //
//    //    val hz_lngLat=data_hz_filter.map{
//    //      t=>
//    //        var i=0
//    //        while(i< hz_city_array.length){
//    //          if(t._1==hz_city_array(5) && t._2==hz_city_array(6)){
//    //              hz_city_array(2)+","+hz_city_array(3)
//    //            //i=hz_city_array.length
//    //            break()
//    //          }
//    //          i+=1
//    //        }
//    //        hz_city_array(2)+","+hz_city_array(3)
//    //    }
//    //
//    //    hz_lngLat.saveAsTextFile("file:/home/yuan/test.txt")
//
//    /*val width = 5
//    val height = 2
//    val numPic = width * height
//    val core = 4
//
//    sc.parallelize(0 until core).foreach {
//      n =>
//        for (im <- Range(n, numPic, core)) {
//          val x: Int = im % width
//          val y: Int = im / width
//
//          val points=new util.ArrayList[Point]()
//
//          for(i<-0 until 250){
//            points.add(new Point(Math.floor(Math.random()*400).toInt,Math.floor(Math.random()*400).toInt))
//          }
//
//          val image=new Heatmap().createHeatmap(256,256,15,140,points)
//          ImageUtils.writeImage(image, "hdfs://localhost:9000/zhejiang_cell_records/imageTest/" + x + "_" + y + System.currentTimeMillis()+".png")
//        }
//    }*/
//
//    //经纬度范围
//    val hzMinLng: Double = 119.236436
//    val hzMaxLng: Double = 120.489062
//    val hzMinLat: Double = 30.73325
//    val hzMaxLat: Double = 31.178782
//
//    //初始地图长宽
//    val width: Int = 230
//    val height: Int = 172
//    //初始地图层级
//    var level: Int = 8
//    val core: Int = 4
//
//    //    val data=sc.textFile("hdfs://localhost:9000/zhejiang_cell_records/lngLat/hz_lngLat/*")
//    val data: org.apache.spark.rdd.RDD[org.apache.spark.sql.Row] = sqlContext.read.json("hdfs://localhost:9000/zhejiang_cell_records/lngLat/hz_lngLat/*").rdd
//
//    while (level < 13) {
//      val w_n = width / 256 + 1
//      val h_n = height / 256 + 1
//
//      val blocks = w_n * h_n
//
//      sc.parallelize(0 until core).map{
//        k=>
////          for(im<-Range())
//      }
//      level += 1
//    }
//    sc.stop()
//  }
//}
