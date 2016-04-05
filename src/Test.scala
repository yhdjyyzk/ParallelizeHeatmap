import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by yuan on 16-1-3.
 */
object Test {
  def main(args: Array[String]) {
    val image=new BufferedImage(400,400,BufferedImage.TYPE_4BYTE_ABGR)
    val graphics=image.createGraphics()
    graphics.setColor(Color.red)
    graphics.drawArc(-50,-50,100,100,0,360)
    graphics.setColor(Color.blue)
    graphics.fillArc(-50,-50,100,100,0,360)
    ImageIO.write(image,"jpg",new File("/home/yuan/workspace/IdeaProjects/ParallelizeHeatmap/source/p.jpg"))
  }
}
