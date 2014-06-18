package de.l3s.boilerpipe.extract;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.Image;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleSentencesExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.ImageExtractor;

public class Binocular {

	private static final int IMAGE_AREA = 20000; // Optimal image size
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	private static final String OUTFILE = "output.txt";
	private static final String INPUTFILE = "urls.txt";

	public static void main(final String[] args) throws Exception {

		if (args.length == 0) // first argument is the URL for which the text
								// has to be extracted.
			System.exit(1);

		/**
		 * Validate if the URL is correct. If not then exit the program.
		 */
		String url = args[0];

		String useFullExtractor = null;
		if (args.length == 2) {
			useFullExtractor = args[1];
		}
		
		URL urlObject = null;
		URI uri = null;
		String hostBase = null;
		try {
			urlObject = new URL(url);
			uri = new URI(url);
			hostBase = findHostNameOfURl(uri);
		} catch (MalformedURLException e) {
			exitProgramWithException(e);
		} catch (URISyntaxException e) {
			exitProgramWithException(e);
		}

		Article article = new Article(new String(url.getBytes("UTF-8")));

		PrintStream out = null;
		try {
			out = new PrintStream(System.out, true, "UTF-8");
			TextDocument processedDoc = (TextDocument) ArticleSentencesExtractor.INSTANCE
					.getText(urlObject);

			article.setTitle(processedDoc.getTitle());
			article.setAuthors(processedDoc.getAuthors());
			article.setDescription(processedDoc.getDescription());
			article.setKeywords(processedDoc.getKeywords());
			article.setContent(processedDoc.getContent());

			// Parse the images from the article.
			ArrayList<ArticleImage> articleImages = getProcessImagesForArticle(
					processedDoc, urlObject, hostBase, useFullExtractor);
			article.setImages(articleImages);

			String jsonString = new Gson().toJson(article);
			byte[] utf8JsonString = jsonString.getBytes();
			out.println(new String(utf8JsonString));

		} catch (Exception e) {
			exitProgramWithException(e);
		} finally {
			out.close();
			System.exit(0);
		}
	}
	
	public static String findHostNameOfURl(URI uri) throws IOException {
		
		String result = null;
		String hostURl = uri.getScheme() + "://" + uri.getHost();
		result = uri.getHost();
		HttpURLConnection con = null;
		
		try {
			con = (HttpURLConnection)new URL(hostURl).openConnection();
			con.setInstanceFollowRedirects( false );
			con.connect();
			
			String location = con.getHeaderField( "Location" );
			
			if (location != null) {
				URL netUrl = new URL(location);
				String host = netUrl.getHost();
				result = host;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return result;
	}

	public static ArrayList<ArticleImage> getProcessImagesForArticle(
			TextDocument processedDoc, URL url, String domain, String useFullExtractor) {
		ArrayList<ArticleImage> imgList = new ArrayList<ArticleImage>();
		ArrayList<ArticleImage> result = new ArrayList<ArticleImage>();
		try {
			int area = 0;
			int imageWidth = 0;
			int imageHeight = 0;
			boolean setMain = false;
			BufferedImage awtImg = null;
			HashMap<String, String> imgUrlMap = new HashMap<String, String>();

			// Process rest of the images.
			BoilerpipeExtractor extractor = null;
			if (useFullExtractor != null) {
				extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
			} else {
				extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
			}

			
			final ImageExtractor ie = ImageExtractor.INSTANCE;
			List<Image> imgUrls = ie.process(url, extractor);
			int areaMax = Integer.MIN_VALUE;

			for (Image img : imgUrls) {
				
				String imURL = img.getSrc();

				if (imURL.contains("/ads/") || imURL.contains("-ad-") || imURL.contains("advertisement") || imURL.contains("logo.gif")) {
					continue;
				}

				String imageDomain = null;

				try {
					imageDomain = getDomainName(imURL);
				} catch (Exception e) {
				}

				if (imageDomain == null) {
					imURL = "http://" + domain + imURL;
				}
				
				if (imgUrlMap.containsKey(imURL)) {
					continue;
				}
				
				// Create a URL for the image's location
				URL imUrlProcess = new URL(imURL);

				// Get the image
				try {
					awtImg = ImageIO.read(imUrlProcess);
				} catch (Exception e) {
					continue;
				}

				if (awtImg == null) {
					continue;
				} 

				int imWidth = awtImg.getWidth();
				int imHeight = awtImg.getHeight();
				int areaIm = imWidth * imHeight;

				// Cases based on height
				if (imURL.contains("/pgatour/") && imWidth == 669 && imHeight == 219) {
					continue;
				}
				// Cases based on height
				if (imURL.contains("nfl") && imWidth == 635 && imHeight == 100) {
					continue;
				}
				
				if (areaIm < IMAGE_AREA) {
					continue;
				}
				
				ArticleImage ai = new ArticleImage(imURL, imWidth, imHeight, areaIm);
				
				if (areaIm > areaMax) {
					areaMax = areaIm;
				} 
				imgList.add(ai);
			}

			// String imageURL = processedDoc.getImage();
			// if (imageURL != null) {
			// 	URL imgUrlProcess = new URL(imageURL);
			// 	imgUrlMap.put(imageURL, "");

			// 	// Get the image.
			// 	try {
			// 		awtImg = ImageIO.read(imgUrlProcess);
			// 	} catch (Exception e) {

			// 	}
			// 	if (awtImg != null) {
			// 		imageWidth = awtImg.getWidth();
			// 		imageHeight = awtImg.getHeight();
			// 		area = imageWidth * imageHeight;
			// 		ArticleImage ai = new ArticleImage(imageURL, imageWidth,
			// 				imageHeight, area);
			// 		if (area >= areaMax) {
			// 			areaMax = area;
			// 		}
			// 		//imgList.add(ai);
			// 	}
			// }
			
			boolean mainAdded = false;
			for(int i = 0; i < imgList.size(); i++) {
				ArticleImage imageItem = imgList.get(i);
				
				if (areaMax == imageItem.getArea() && mainAdded == false) {
					if (imageItem.getArea() >= IMAGE_AREA) {
						imageItem.setMain(1);
						mainAdded = true;
					}
				}
				
				result.add(imageItem);
			}

		} catch (Exception e) {
			exitProgramWithException(e);
		}
		return result;
	}

	public static void exitProgramWithException(Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}

	public static void exitProgram() {
		System.exit(1);
	}

	public static StringBuilder formatOutputBuffer(StringBuilder sb, String text) {
		return sb.append(text).append("\n\n");
	}

	public static String getDomainName(String url) throws MalformedURLException {
		URL netUrl = new URL(url);
		String host = netUrl.getHost();

		if (host.startsWith("www")) {
			host = host.substring("www".length() + 1);
		}
		return host;
	}
}
