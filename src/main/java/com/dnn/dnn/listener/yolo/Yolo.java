//package com.dnn.dnn.listener.yolo;
//
//import org.opencv.core.*;
//import org.opencv.dnn.*;
//import org.opencv.utils.*;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.videoio.VideoCapture;
//
////import com.streambase.com.gs.collections.impl.Counter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferByte;
//import java.awt.image.WritableRaster;
//import java.io.ByteArrayInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//
//import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//
//public class Yolo {
//
//	private static List<String> getOutputNames(Net net) {
//		List<String> names = new ArrayList<>();
//
//		List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
//		List<String> layersNames = net.getLayerNames();
//
//		outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));// unfold and create R-CNN layers from the
//																			// loaded YOLO model//
//		return names;
//	}
//
//	public static void main(String[] args) throws InterruptedException {
//
//		try {
//			System.load("C:\\opencv-4.0.0\\build\\java\\x64\\opencv_java400.dll");
////		System.load("C:\\Users\\subhatta\\Downloads\\opencv\\build\\java\\x64\\opencv_java400.dll"); // Load the openCV
//			// 4.0 dll //
//			String modelWeights = "ann/ctr/ctr4_last.weights"; // Download and load only wights for YOLO , this is
//																// obtained
//																// from
//			// official YOLO site//
//			String modelConfiguration = "ann/ctr/ctr4.cfg";// Download and load cfg file for YOLO , can be obtained from
//															// official site//
//			String filePath = "OutGateDemo1.mp4"; // My video file to be analysed//
//			Mat frame = new Mat(); // define a matrix to extract and store pixel info from video//
//			Mat dst = new Mat();
//			// cap.read(frame);
//			System.out.println("JFrame");
//			JFrame jframe = new JFrame("Video"); // the lines below create a frame to display the resultant video with
//													// object detection and localization//
//			System.out.println("JFrame");
//			JLabel vidpanel = new JLabel();
//			jframe.setContentPane(vidpanel);
//			jframe.setSize(600, 600);
//			jframe.setVisible(true);// we instantiate the frame here//
//
//			System.out.println("JFrame");
//			Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights); // OpenCV DNN supports models trained
//																				// from
//																				// various frameworks like Caffe and
//																				// TensorFlow. It also supports various
//																				// networks architectures based on
//																				// YOLO//
//			// Thread.sleep(5000);
//
//			System.out.println("JFrame");
//			// Mat image =
//			// Imgcodecs.imread("D:\\yolo-object-detection\\yolo-object-detection\\images\\soccer.jpg");
//			Size sz = new Size(288, 288);
//
//			System.out.println("JFrame");
//			List<Mat> result = new ArrayList<>();
//			List<String> outBlobNames = getOutputNames(net);
//
//			System.out.println("println");
////			while (true) {
//
//			VideoCapture cap = new VideoCapture(filePath);// Load video using the videocapture method//
//
//			cap.open(filePath);
//			System.out.println("if (cap.read(frame)) {" + cap.isOpened());
//			while (cap.read(frame)) {
//				System.out.println("Read frame");
//				Mat blob = Dnn.blobFromImage(frame, 0.00392, sz, new Scalar(0), true, false); // We feed one frame
//																								// of
//																								// video into the
//																								// network at a
//																								// time, we
//																								// have to convert
//																								// the
//																								// image to a blob.
//																								// A
//																								// blob is a
//																								// pre-processed
//																								// image
//																								// that serves as
//																								// the
//																								// input.//
//				net.setInput(blob);
//
//				net.forward(result, outBlobNames); // Feed forward the model to get output //
//
//				// outBlobNames.forEach(System.out::println);
//				// result.forEach(System.out::println);
//
//				float confThreshold = 0.6f; // Insert thresholding beyond which the model will detect objects//
//				List<Integer> clsIds = new ArrayList<>();
//				List<Float> confs = new ArrayList<>();
//				List<Rect> rects = new ArrayList<>();
//				for (int i = 0; i < result.size(); ++i) {
//					// each row is a candidate detection, the 1st 4 numbers are
//					// [center_x, center_y, width, height], followed by (N-4) class probabilities
//					Mat level = result.get(i);
//					for (int j = 0; j < level.rows(); ++j) {
//						Mat row = level.row(j);
//						Mat scores = row.colRange(5, level.cols());
//						Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
//						float confidence = (float) mm.maxVal;
//						Point classIdPoint = mm.maxLoc;
//						if (confidence > confThreshold) {
//							int centerX = (int) (row.get(0, 0)[0] * frame.cols()); // scaling for drawing the
//																					// bounding
//																					// boxes//
//							int centerY = (int) (row.get(0, 1)[0] * frame.rows());
//							int width = (int) (row.get(0, 2)[0] * frame.cols());
//							int height = (int) (row.get(0, 3)[0] * frame.rows());
//							int left = centerX - width / 2;
//							int top = centerY - height / 2;
//
//							clsIds.add((int) classIdPoint.x);
//							confs.add((float) confidence);
//							rects.add(new Rect(left, top, width, height));
//						}
//					}
//				}
//				float nmsThresh = 0.5f;
//				Mat confsMat = Converters.vector_float_to_Mat(confs);
////				Mat grayMat = new Mat();
////			    Imgproc.cvtColor(confsMat, grayMat, Imgproc.COLOR_BGR2GRAY);
//				System.out.println(confsMat);
//				MatOfFloat confidences = new MatOfFloat(confsMat);
//				Rect[] boxesArray = rects.toArray(new Rect[0]);
//				MatOfRect boxes = new MatOfRect(boxesArray);
//				MatOfInt indices = new MatOfInt();
//				Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices); // We draw the bounding boxes
//																						// for
//																						// objects here//
//
//				int[] ind = indices.toArray();
//				int j = 0;
//				for (int i = 0; i < ind.length; ++i) {
//					int idx = ind[i];
//					Rect box = boxesArray[idx];
//					Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(0, 0, 255), 2);
//					// i=j;
//
//					System.out.println(idx);
//				}
//				// Imgcodecs.imwrite("D://out.png", image);
//				// System.out.println("Image Loaded");
//				ImageIcon image = new ImageIcon(Mat2bufferedImage(frame)); // setting the results into a frame and
//																			// initializing it //
//				vidpanel.setIcon(image);
//				vidpanel.repaint();
//				// System.out.println(j);
//				// System.out.println("Done");
//
//			}
////			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
////	}
//	private static BufferedImage Mat2bufferedImage(Mat image) { // The class described here takes in matrix and renders
//																// the video to the frame //
//		MatOfByte bytemat = new MatOfByte();
//		Imgcodecs.imencode(".jpg", image, bytemat);
//		byte[] bytes = bytemat.toArray();
//		InputStream in = new ByteArrayInputStream(bytes);
//		BufferedImage img = null;
//		try {
//			img = ImageIO.read(in);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return img;
//	}
//}