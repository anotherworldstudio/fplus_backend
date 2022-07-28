package com.kbeauty.gbt.ai;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.ByteBuffer;
//import java.util.List;
//
//import javax.imageio.ImageIO;
//
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.rekognition.AmazonRekognition;
//import com.amazonaws.services.rekognition.AmazonRekognitionClient;
//import com.amazonaws.services.rekognition.model.AgeRange;
//import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
//import com.amazonaws.services.rekognition.model.Attribute;
//import com.amazonaws.services.rekognition.model.BoundingBox;
//import com.amazonaws.services.rekognition.model.DetectFacesRequest;
//import com.amazonaws.services.rekognition.model.DetectFacesResult;
//import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
//import com.amazonaws.services.rekognition.model.DetectLabelsResult;
//import com.amazonaws.services.rekognition.model.Emotion;
//import com.amazonaws.services.rekognition.model.Eyeglasses;
//import com.amazonaws.services.rekognition.model.FaceDetail;
//import com.amazonaws.services.rekognition.model.Gender;
//import com.amazonaws.services.rekognition.model.Image;
//import com.amazonaws.services.rekognition.model.ImageQuality;
//import com.amazonaws.services.rekognition.model.Label;
//import com.amazonaws.services.rekognition.model.Landmark;
//import com.amazonaws.services.rekognition.model.Pose;
//import com.amazonaws.services.rekognition.model.Smile;
//import com.amazonaws.util.IOUtils;
//import com.beautej.beautage.poc.util.AwsFaceDedect;
// text
public class AmazonFaceBuilder {
//	public static void main(String[] args) throws Exception {
//		AwsFaceDedect dedect = new AwsFaceDedect();
//		dedect.process("E:\\beautage\\poc\\facelandmaker\\", "test.jpg");
//		
////		AwsFaceDedect.test();
//	}
//
//	public static void test() throws FileNotFoundException, IOException {
//		// 濡쒖뺄 �뙆�씪 寃쎈줈
//		String filePath = "E:\\beautage\\poc\\facelandmaker\\test.jpg";
//
//		// �씤�뭼�뒪�듃由쇱뿉 �뙆�씪 異붽�
//		ByteBuffer imageBytes;
//		try (InputStream inputStream = new FileInputStream(new File(filePath))) {
//			imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
//		}	
//
//		AmazonRekognition rekognitionClient = getRekognitionClient();
//		
////		 AWSCredentials credentials = null;
////		  try {
////		     credentials = new ProfileCredentialsProvider("default").getCredentials();
////		  } catch (Exception e) {
////		     throw new AmazonClientException("Cannot load the credentials: ", e);
////		  }
////		
////		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2)
////		.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
//
//		// AmazonWebServiceRequest瑜� �긽�냽 諛쏄퀬 �엳�쓬
//		DetectLabelsRequest request = new DetectLabelsRequest().withImage(new Image().withBytes(imageBytes))
//				.withMaxLabels(100) // �꽌鍮꾩뒪�뿉�꽌 �쓳�떟�쑝濡� 諛섑솚 �븷 理쒕� �젅�씠釉� �닔�엯�땲�떎.
//				.withMinConfidence(77F); // �젅�씠釉붿씠 諛섑솚 �븷 理쒖냼 �젙�솗�룄 �닔以��쓣 吏��젙�빀�땲�떎.
//
//		try {
//			// AmazonWebServiceResult<ResponseMetadata> �긽�냽 諛쏄퀬 �엳�쓬
//// �엯�젰�쑝濡� �젣怨듬맂 �씠誘몄� (JPEG �삉�뒗 PNG) �궡�쓽 �떎�젣 �뿏�떚�떚 �씤�뒪�꽩�뒪瑜� 媛먯�
//			DetectLabelsResult result = rekognitionClient.detectLabels(request);
//			List<Label> labels = result.getLabels(); // �젅�씠釉� 戮묎린
//
//			System.out.println("�뙆�씪 �쐞移�" + filePath);
//			for (Label label : labels) {
//				// getName �� �젅�씠釉� 利� 媛앹껜 異붿텧�븳 紐� , getConfidence().toString()�� �젅�씠釉붿쓽 �젙�솗�룄
//				System.out.println(label.getName() + ": " + label.getConfidence().toString());
//			}
//
//		} catch (AmazonRekognitionException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static AmazonRekognition getRekognitionClient() {
//		// AmazonRekognition - Amazon Rekognition�뿉 �븸�꽭�뒪�븯湲곗쐞�븳 �씤�꽣�럹�씠�뒪
//		// AmazonRekognitionClient aws 怨꾩젙 �뿰�룞�쓣 �쐞�븳
//		AmazonRekognition rekognitionClient = new AmazonRekognitionClient(
//				new BasicAWSCredentials("AKIAYBOGP5C75IFNP5LM", "fkwQFt00f1FQTS3ejwVuzMRWAT/sMUits1vBC9a2"));
//		return rekognitionClient;
//	}
//	
//	public void process(String filePath, String photo) throws Exception {
//
//		  AmazonRekognition amazonRekognition = getRekognitionClient();
//
//		  // Load image
//		  ByteBuffer imageBytes=null;
//		  BufferedImage image = null;
//
//		  try (InputStream inputStream = new FileInputStream(new File(filePath + photo))) {
//		     imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
//
//		  }
//		  catch(Exception e)
//		  {
//		      System.out.println("Failed to load file " + photo);
//		      System.exit(1);
//		  }
//
//		  //Get image width and height
//		  InputStream imageBytesStream;
//		  imageBytesStream = new ByteArrayInputStream(imageBytes.array());
//
//		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		  image=ImageIO.read(imageBytesStream);
//		  //ImageIO.write(image, "jpg", baos);
//
//		  int height = image.getHeight();
//		  int width = image.getWidth();
//
//		  System.out.println("Image Information:");
//		  System.out.println(photo);
//		  System.out.println("Image Height: " + Integer.toString(height));
//		  System.out.println("Image Width: " + Integer.toString(width));
//
//		  //Call detect faces and show face age and placement
//
//		  try{
//		    DetectFacesRequest request = new DetectFacesRequest()
//		           .withImage(new Image()
//		              .withBytes((imageBytes)))
//		           .withAttributes(Attribute.ALL);
//
//
//		      DetectFacesResult result = amazonRekognition.detectFaces(request);
//		      List <FaceDetail> faceDetails = result.getFaceDetails();
//		      
//		      if(faceDetails == null || faceDetails.isEmpty()) {
//		    	  System.out.println("No faces detected");
//		      }
//
//		      System.out.println("*********   Faces found : "+faceDetails.size());
//		      
//		      for (FaceDetail face: faceDetails) {
//		          addBoundingBoxPositions(image, height,
//		                  width,
//		                  face.getBoundingBox(),
//		                  result.getOrientationCorrection());
//		          
//		          ImageIO.write(image, "jpg", new File(filePath + "img/"+photo+"_outut.jpg"));
//		          System.out.println("==================================");
//		          
//		          Gender gender = face.getGender();
//		          Smile smile = face.getSmile();
//		          List<Emotion> emotions = face.getEmotions();
//		          Eyeglasses glasses = face.getEyeglasses();
//		          		          
//		          Pose pose = face.getPose();
//		          pose.getPitch();
//		          pose.getRoll();
//		          pose.getYaw();
//		          		          
//		          List<Landmark> landmarks = face.getLandmarks();
//		          
//		          float wPicxel = 0;
//		          float hPicxel = 0;
//		          
//		          for (Landmark landmark :  landmarks) {
//		        	  wPicxel = width * landmark.getX();
//		        	  hPicxel = height * landmark.getY();
//		        	  
//		              System.out.println("type : " + landmark.getType() + " x : " +  wPicxel + " y : " + hPicxel);
//		          }
//		          
//		          ImageQuality quality = face.getQuality();
//		          Float brightness = quality.getBrightness();
//		          Float sharpness = quality.getSharpness();
//		          
//		          
//		          System.out.println("brightness : " + brightness + " sharpness : " +  sharpness);
//		          		          
//		          System.out.println("==================================");
//		          AgeRange ageRange = face.getAgeRange();
//		          
//		          System.out.println("The detected face is estimated to be between "
//		               + ageRange.getLow().toString() + " and " + ageRange.getHigh().toString()
//		               + " years old. The gender is "+gender.getValue()+ ". The person is "+ (smile.isValue() ? "smiling" : "not smiling") +"."
//		               + " The person is "+ (glasses.isValue() ? "wearing " : "not wearing ") +"eyeglasses.");
//		          	System.out.print("Emotions shown ");
//		          	emotions.stream().filter(e -> (e.getConfidence() > 95.0F)).forEach( e -> System.out.print(e.getType() + ", "));
//		            System.out.println();
//		       }
//
//		   } catch (AmazonRekognitionException e) {
//		      e.printStackTrace();
//		   }
//
//		  amazonRekognition.shutdown();
//		}
//
//
//		public static void addBoundingBoxPositions(BufferedImage image, int imageHeight, int imageWidth, BoundingBox box, String rotation) {
//
//		  float left = 0;
//		  float top = 0;
//
//		  if(rotation==null){
//		      //System.out.println("No estimated estimated orientation. Check Exif data.");
//		      return;
//		  }
//		  //Calculate face position based on image orientation.
//		  switch (rotation) {
//		     case "ROTATE_0":
//		        left = imageWidth * box.getLeft();
//		        top = imageHeight * box.getTop();
//		        break;
//		     case "ROTATE_90":
//		        left = imageHeight * (1 - (box.getTop() + box.getHeight()));
//		        top = imageWidth * box.getLeft();
//		        break;
//		     case "ROTATE_180":
//		        left = imageWidth - (imageWidth * (box.getLeft() + box.getWidth()));
//		        top = imageHeight * (1 - (box.getTop() + box.getHeight()));
//		        break;
//		     case "ROTATE_270":
//		        left = imageHeight * box.getTop();
//		        top = imageWidth * (1 - box.getLeft() - box.getWidth());
//		        break;
//		     default:
//		        System.out.println("No estimated orientation information. Check Exif data.");
//		        return;
//		  }
//
//		  Graphics2D graph = image.createGraphics();
//		  Graphics2D g2d = image.createGraphics();
//		  g2d.setColor(Color.YELLOW);
//		  g2d.setStroke(new BasicStroke(3));
//		  g2d.drawRect(Math.round(left), Math.round(top), Math.round(imageHeight * box.getHeight()), Math.round(imageWidth * box.getWidth()));
//		  g2d.dispose();
//
//		  
//		  //Display face location information.
//		  /*
//		  System.out.println("Left: " + String.valueOf((int) left));
//		  System.out.println("Top: " + String.valueOf((int) top));
//		  System.out.println("Face Width: " + String.valueOf((int)(imageWidth * box.getWidth())));
//		  System.out.println("Face Height: " + String.valueOf((int)(imageHeight * box.getHeight())));
//		  */
//		  }
}
