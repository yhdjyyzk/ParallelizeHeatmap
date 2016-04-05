import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;

/**
 * heatmap
 *
 * @author YuanZhaokang
 * @time 2015-12-28
 */
public class Heatmap {

//	private Store store = null;

	public Heatmap() {
		/*this.store = new Store();*/
	}

	public BufferedImage render(int width, int height, int radius,
			int opcatity, ArrayList<Point> points) {
		BufferedImage palette = createPalette();

		BufferedImage grayHeatmap = null;
		BufferedImage colorfulHeatmap = null;
		// Store store=new Store();

		try {
			grayHeatmap = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2d = grayHeatmap.createGraphics();

//			this.setData(points);

			for (Point p :points) {
				int x = p.getX();
				int y = p.getY();

				graphics2d.setPaint(new RadialGradientPaint(new Point2D.Double(
						x, y), radius, new float[] { 0f, 1.0f }, new Color[] {
						new Color(0, 0, 0, 120), new Color(0, 0, 0, 0) }));
				graphics2d.fillArc(x - radius, y - radius, 2 * radius,
						2 * radius, 0, 360);
			}

			ColorModel cm = ColorModel.getRGBdefault();
			colorfulHeatmap = new BufferedImage(grayHeatmap.getWidth(),
					grayHeatmap.getHeight(), BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < grayHeatmap.getWidth(); i++) {
				for (int j = 0; j < grayHeatmap.getHeight(); j++) {
					int alpha = cm.getAlpha(grayHeatmap.getRGB(i, j));
					if (alpha != 0) {
						int rgba = palette.getRGB(alpha, 0);

						int r = cm.getRed(rgba);
						int g = cm.getGreen(rgba);
						int b = cm.getBlue(rgba);
						int a = opcatity;

						colorfulHeatmap.setRGB(i, j,
								new Color(r, g, b, a).getRGB());
					} else
						colorfulHeatmap.setRGB(i, j, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return colorfulHeatmap;
		}

	}

	private BufferedImage createPalette() {
		BufferedImage palette = null;
		try {
			palette = new BufferedImage(256, 1, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D graphics2d = palette.createGraphics();
			graphics2d.setPaint(new LinearGradientPaint(
					new Point2D.Float(1, 0), new Point2D.Float(255, 0),
					new float[] { 0f, 0.25f, 0.5f, 0.75f, 1.0f }, new Color[] {
							new Color(0, 0, 255), new Color(0, 255, 255),
							new Color(0, 255, 0), new Color(255, 255, 0),
							new Color(255, 0, 0) }));
			graphics2d.fillRect(0, 0, 256, 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return palette;
		}
	}

//	public void addData(Point point) {
//		this.store.addData(point);
//	}
//
//	public void setData(ArrayList<Point> points) {
//		this.store.setData(points);
//	}
//
//	public ArrayList<Point> getData() {
//		return this.store.getData();
//	}
}

//class Store {
//	private ArrayList<Point> data = null;
//
//	public Store() {
//		data = new ArrayList<>();
//	}
//
//	public void addData(Point point) {
//		data.add(point);
//	}
//
//	public void setData(ArrayList<Point> points) {
//		data = points;
//		// System.out.println(data.get(499).getX()+","+data.get(499).getY());
//	}
//
//	public ArrayList<Point> getData() {
//		return data;
//	}
//}
