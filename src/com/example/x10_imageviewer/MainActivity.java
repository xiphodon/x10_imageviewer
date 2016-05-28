package com.example.x10_imageviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	ImageView iv = null;
	Handler handler = new Handler(){
		@Override
		//此方法在主线程中被调用，可以用来刷新ui
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//返回成功的消息
			case 1:
				//把位图像显示至imageview
				iv.setImageBitmap((Bitmap)msg.obj);
				Toast.makeText(MainActivity.this, "从网上下载来的", Toast.LENGTH_SHORT).show();
				break;
			//返回失败的消息
			case 0:
				Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
				break;
			}
		
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
    }


    
    
    public void click(View v){
    	//下载图片
		//1.确定网址
		final String path = "http://192.168.1.101:8080/wolf.jpg";
		final File file = new File(getCacheDir(),getFileName(path));
		
		//缓存文件是否存在
    	if(file.exists()){
    		//如果缓存存在，从缓存读取
    		Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
    		iv.setImageBitmap(bm);
    		Toast.makeText(MainActivity.this, "从缓存中读取来的", Toast.LENGTH_SHORT).show();
    	}else{
    		//如果缓存不存在，从网上下载
    		Thread thread = new Thread(){
        		@Override
        		public void run() {
        			
        			try{
        				//2.把网址封装成一个URL对象
        				URL url = new URL(path);
        				//3.获取客户端和服务端的链接对象，此刻还没有创建链接
        				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        				//4.将连接对象初始化
        				  //设置请求方法，注意大写
        				conn.setRequestMethod("GET");
        				  //设置连接超时
        				conn.setConnectTimeout(5000);
        				  //设置读取超时
        				conn.setReadTimeout(5000);
        				//5.发送请求，与服务器建立连接
        				conn.connect();
        				//如果响应码为200，说明响应成功
        				
        				if(conn.getResponseCode() == 200){
        					//获得服务器响应头里面的流，流里的数据就是客户端请求的数据
        					InputStream is = conn.getInputStream();
        					
        					//读出流里的数据，写进手机本地，缓存起来
        					FileOutputStream fos = new FileOutputStream(file);
        					byte[] b = new byte[1024];
        					int len = 0;
        					
        					while((len = is.read(b)) != -1 ){
        						fos.write(b, 0, len);
        					}
        					fos.close();
        					
        					//从本地缓存生成位图像
        					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
        					
//        					Bitmap bm = BitmapFactory.decodeStream(is);
        					
//        					ImageView iv = (ImageView) findViewById(R.id.iv);
//        					//把位图像显示至imageview
//        					iv.setImageBitmap(bm);
        					
        					
        					
        					Message msg = handler.obtainMessage();
        					//消息对象可携带数据
        					msg.obj = bm;
        					//标记
        					msg.what = 1;
        					//把消息发送至主线程消息队列
        					handler.sendMessage(msg);
        				}else{
        					Message msg = handler.obtainMessage();
        					msg.what = 0;
        					handler.sendMessage(msg);
        				}
        				
        				
        			}catch(Exception e){
        				e.printStackTrace();
        			}
        		}
        	};
        	thread.start();
    	}
    	
    }
    
    
    
    public String getFileName(String path){
    	int index = path.lastIndexOf("/");
    	return path.substring(index + 1);
    }
    
    
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
