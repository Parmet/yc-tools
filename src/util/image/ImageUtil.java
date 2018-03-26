package util.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtil {
	/** 图片格式：JPG */
	private static final String PICTRUE_FORMATE_JPG = "jpg";
	
	public static void pressText(String pressText, String targetImg,
			String fontName, int fontStyle, Color color, int fontSize) {
			FileOutputStream out = null;
			try {
				File _file = new File(targetImg);
				Image src = ImageIO.read(_file);
				int wideth = src.getWidth(null);
				System.out.println("wideth:"+wideth);
				int height = src.getHeight(null);
				System.out.println("height:"+height);
				BufferedImage image = new BufferedImage(wideth, height,

				BufferedImage.TYPE_BYTE_GRAY);

				Graphics g = image.createGraphics();

				g.drawImage(src, 0, 0, wideth, height, null);

				g.setColor(color);

				g.setFont(new Font(fontName, fontStyle, fontSize));

				/*g.drawString(pressText, wideth - fontSize - x, height - fontSize

				/ 2 - y);*/
				
				g.drawString(pressText, wideth/2-fontSize*2, height-fontSize-10);

				g.dispose();

				out = new FileOutputStream(targetImg);

				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);

				encoder.encode(image);

				out.close();

			} catch (Exception e) {

				System.out.println(e);
			}finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	/**
	 * 添加文字水印
	 * 
	 * @param targetImg
	 *            目标图片路径，如：C://myPictrue//1.jpg
	 * @param pressText
	 *            水印文字， 如：中国证券网
	 * @param fontName
	 *            字体名称， 如：宋体
	 * @param fontStyle
	 *            字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
	 * @param fontSize
	 *            字体大小，单位为像素
	 * @param color
	 *            字体颜色
	 * @param x
	 *            水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
	 * @param y
	 *            水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
	 * @param alpha
	 *            透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
	 * @param degree
	 *            水印旋转角度
	 */
	public static void pressText(String targetImg, String pressText, Font font, 
			Color color, int x, int y, float alpha, Integer degree) {
		try {
			File file = new File(targetImg);

			Image image = ImageIO.read(file);
			int width = image.getWidth(null);
			int height = image.getHeight(null);
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufferedImage.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
			if (null != degree) {
                g.rotate(Math.toRadians(degree),(double) bufferedImage.getWidth() / 2, (double) bufferedImage.getHeight() / 2);
            }
			
			g.setFont(font);
			g.setColor(color);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			int width_1 = font.getSize() * getLength(pressText);
			int height_1 = font.getSize();
			int widthDiff = width - width_1;
			int heightDiff = height - height_1;
			if (x < 0) {
				x = widthDiff / 2;
			} else if (x > widthDiff) {
				x = widthDiff;
			}
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}

			g.drawString(pressText, x, y + height_1);
			g.dispose();
			ImageIO.write(bufferedImage, PICTRUE_FORMATE_JPG, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("pressEnd");
	}
	
	public static void pressText(String targetImg, String pressText,
			String fontName, int fontStyle, int fontSize, Color color, int x,
			int y, float alpha, Integer degree) {
		Font font = new Font(fontName, fontStyle, fontSize);
		pressText(targetImg, pressText, font, color, x, y, alpha, degree);
	}
	
	/**
	 * 添加文字水印
	 * 
	 * @param targetImg
	 *            目标图片路径，如：C://myPictrue//1.jpg
	 * @param pressText
	 *            水印文字， 如：中国证券网
	 * @param fontName
	 *            字体名称， 如：宋体
	 * @param fontStyle
	 *            字体样式，如：粗体和斜体(Font.BOLD|Font.ITALIC)
	 * @param fontSize
	 *            字体大小，单位为像素
	 * @param color
	 *            字体颜色
	 * @param x
	 *            水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
	 * @param y
	 *            水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
	 * @param alpha
	 *            透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
	 */
	public static void pressText(String targetImg, String pressText,
			String fontName, int fontStyle, int fontSize, Color color, int x,
			int y, float alpha) {
		pressText(targetImg, pressText, fontName, fontStyle, fontSize, color, x, y, alpha, null);
	}
	
	/**
	 * http://www.qi788.com/info-43.html 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
	 * 
	 * @param text
	 * @return 字符长度，如：text="中国",返回 2；text="test",返回 2；text="中国ABC",返回 4.
	 */
	public static int getLength(String text) {
		int textLength = text.length();
		int length = textLength;
		for (int i = 0; i < textLength; i++) {
			if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
				length++;
			}
		}
		return (length % 2 == 0) ? length / 2 : length / 2 + 1;
	}
	

	public static void main(String[] args) throws Throwable {
		String fontPath="F:/project/chinauip.cfc5/WebRoot/images/temp/simkai.ttf";
		String tiffullpath="F:/project/chinauip.cfc5/WebRoot/images/temp/timg.jpg";
		Font font = null;  
		File file = new File(fontPath);
        FileInputStream fi = new FileInputStream(file);  
        BufferedInputStream fb = new BufferedInputStream(fi);  
        font = Font.createFont(Font.TRUETYPE_FONT, fb);  
        font = font.deriveFont(Font.BOLD, 50);  
		
		pressText(tiffullpath, " 广州市社会保险业务档案   仅供参考", font, Color.DARK_GRAY, -1, -1, 0.5f, -45);
	}
}
