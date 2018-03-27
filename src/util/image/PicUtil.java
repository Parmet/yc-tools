package util.image;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import org.apache.commons.io.FilenameUtils;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class PicUtil {

	/**
	 * 由于ie不支持tif文件,须将tif文件中的图片全部转换成jpg格式。
	 * 
	 * @param tifFilePath:tif文件路径
	 * @return List:jpg文件名集合
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static List convertImgs(String tifFilePath) throws IOException {
		if (tifFilePath.trim().equals("")) {
			throw new IOException("没找到要上传的tif文件");
		}
		FileSeekableStream fsStream = new FileSeekableStream(tifFilePath);
		TIFFDecodeParam tifDparam = null;
		TIFFEncodeParam tifEparam = new TIFFEncodeParam();
		JPEGEncodeParam jpgEparam = new JPEGEncodeParam();
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", fsStream,
				tifDparam);
		// 总页数
		int numPages = dec.getNumPages();
		tifEparam.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
		tifEparam.setLittleEndian(false);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>上传的tif文件总共有 " + numPages
				+ " 页>>>>>>>>>>>>>>>>>>>>>>");
		// 设置输出目录
		String jpgPath = tifFilePath.substring(0, tifFilePath.lastIndexOf("."));
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>jpeg生成路径 " + jpgPath
				+ " >>>>>>>>>>>>>>>>>>>>>>");
		// 保留tif文件
		List<String> lst = new ArrayList<String>();
		lst.add(FilenameUtils.getName(tifFilePath));
		// 转换、输出jpg
		for (int i = 0; i < numPages; i++) {
			RenderedImage page = dec.decodeAsRenderedImage(i);
			File f = new File(jpgPath + "_" + i + ".jpg");
			lst.add(f.getName());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>第" + i + "张图片:"
					+ f.getCanonicalPath() + "<<<<<<<<<<<<<<<<<<<<");
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(page);
			pb.add(f.toString());
			pb.add("JPEG");
			pb.add(jpgEparam);
			RenderedOp r = JAI.create("filestore", pb);
			r.dispose();
		}
		return lst;
	}
}
