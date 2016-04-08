import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.util.ArrayList;

/**
 * heatmap
 *
 * @author YuanZhaokang
 * @time 2015-12-28
 */
public class Heatmap {
    public Heatmap() {
    }

    public BufferedImage render(int width, int height, int radius,
                                int opcatity, ArrayList<Point> points) {
        BufferedImage palette = createPalette();

        BufferedImage grayHeatmap = null;
        BufferedImage colorfulHeatmap = null;
        BufferedImage dst = null;

        try {
            grayHeatmap = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics2d = grayHeatmap.createGraphics();

            for (Point p : points) {
                int x = p.getX();
                int y = p.getY();

                graphics2d.setPaint(new RadialGradientPaint(new Point2D.Double(
                        x, y), radius, new float[]{0f, 0.7f, 1.0f}, new Color[]{
                        new Color(0, 0, 0, 120), new Color(0, 0, 0, 90), new Color(0, 0, 0, 0)}));
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

            int boxRadius = 3;
            try {
                dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < colorfulHeatmap.getWidth(); x++) {
                    for (int y = 0; y < colorfulHeatmap.getHeight(); y++) {
                        int r_count = 0;
                        int g_count = 0;
                        int b_count = 0;
                        int alpha_count = 0;

                        for (int i = x - boxRadius; i <= x + boxRadius; i++) {
                            for (int j = y - boxRadius; j <= y + boxRadius; j++) {
                                if (i < 0 || j < 0 || i > width-1 || j > height-1)
                                    continue;
                                int rgb = colorfulHeatmap.getRGB(i, j);
                                r_count += cm.getRed(rgb);
                                g_count += cm.getGreen(rgb);
                                b_count += cm.getBlue(rgb);
                                alpha_count += cm.getAlpha(rgb);
                            }
                        }

                        int r = (int) (r_count / Math.pow(boxRadius * 2 + 1, 2));
                        int g = (int) (g_count / Math.pow(boxRadius * 2 + 1, 2));
                        int b = (int) (b_count / Math.pow(boxRadius * 2 + 1, 2));
                        int a = (int) (alpha_count / Math.pow(boxRadius * 2 + 1, 2));

                        dst.setRGB(x, y, new Color(r, g, b, a).getRGB());
                    }
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return dst;
        }

    }

    private BufferedImage createPalette() {
        BufferedImage palette = null;
        try {
            palette = new BufferedImage(256, 1, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics2d = palette.createGraphics();
            graphics2d.setPaint(new LinearGradientPaint(
                    new Point2D.Float(1, 0), new Point2D.Float(255, 0),

                    new float[]{0f, 0.3f, 0.45f, 0.5f, 0.7f, 0.9f}, new Color[]{
                    new Color(0, 0, 255), new Color(0, 255, 255),
                    new Color(0, 255, 0), new Color(255, 255, 0),
                    new Color(255, 0, 0), new Color(255, 255, 255)}));

            graphics2d.fillRect(0, 0, 256, 1);
            try {
                ImageIO.write(palette, "png", new File("palette.png"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return palette;
        }
    }
}
