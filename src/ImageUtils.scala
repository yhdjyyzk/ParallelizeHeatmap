import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.imageio.ImageIO

import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

/**
 * Created by k on 15-9-8.
 */
object ImageUtils {

  def readImage(hdfsPath: String): BufferedImage = {
    val configuration = new Configuration()
    configuration.addResource(new Path(Path.localConfPath + "/core-site.xml"))
    configuration.addResource(new Path(Path.localConfPath + "/hadoop/hdfs-site.xml"))
    configuration.addResource(new Path(Path.localConfPath + "/hadoop/yarn-site.xml"))
    val fs = FileSystem.get(configuration)
    val is = fs.open(new Path(hdfsPath))
    val buffer = IOUtils.toByteArray(is)
    val out = new ByteArrayOutputStream()
    out.write(buffer, 0, buffer.length)
    val b = out.toByteArray
    out.close()
    val picture = ImageIO.read(new ByteArrayInputStream(b))
    is.close()
    picture
  }

  def writeImage(image: BufferedImage, hdfsPath: String): Unit = {
    val configuration = new Configuration()
    configuration.addResource(new Path(Path.localConfPath + "/core-site.xml"))
    configuration.addResource(new Path(Path.localConfPath + "/hdfs-site.xml"))
    configuration.addResource(new Path(Path.localConfPath + "/yarn-site.xml"))
    val fs = FileSystem.get(configuration)
    val os = fs.create(new Path(hdfsPath))
    val baos = new ByteArrayOutputStream()
    val ios = ImageIO.createImageOutputStream(baos)
    ImageIO.write(image, "png", ios)
    val is = new ByteArrayInputStream(baos.toByteArray)
    IOUtils.copy(is, os)

    is.close()
    ios.close()
    baos.close()
    os.close()
  }
}
