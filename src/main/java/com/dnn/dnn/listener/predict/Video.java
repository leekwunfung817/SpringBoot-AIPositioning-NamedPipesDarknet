//package com.dnn.dnn.listener.predict;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import javax.annotation.PostConstruct;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.videoio.VideoCapture;
//import org.opencv.videoio.Videoio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class Video implements Runnable{
//
//    private MarvinVideoInterface    videoAdapter;
//    private MarvinImage             videoFrame;
//
//    public Video(){
//        try{
//            // Create the VideoAdapter used to load the video file
//            videoAdapter = new MarvinJavaCVAdapter();
//            videoAdapter.loadResource("./res/snooker.wmv");
//
//            // Start the thread for requesting the video frames 
//            new Thread(this).start();
//        }
//        catch(MarvinVideoInterfaceException e){e.printStackTrace();}
//    }
//
//    @Override
//    public void run() {
//        try{
//            while(true){
//                // Request a video frame
//                videoFrame = videoAdapter.getFrame();
//            }
//        }catch(MarvinVideoInterfaceException e){e.printStackTrace();}
//    }
//
//    public static void main(String[] args) {
//        Video m = new Video();
//    }
//}
