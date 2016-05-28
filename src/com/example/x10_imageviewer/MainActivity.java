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
		//�˷��������߳��б����ã���������ˢ��ui
		public void handleMessage(Message msg) {
			switch (msg.what) {
			//���سɹ�����Ϣ
			case 1:
				//��λͼ����ʾ��imageview
				iv.setImageBitmap((Bitmap)msg.obj);
				Toast.makeText(MainActivity.this, "��������������", Toast.LENGTH_SHORT).show();
				break;
			//����ʧ�ܵ���Ϣ
			case 0:
				Toast.makeText(MainActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
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
    	//����ͼƬ
		//1.ȷ����ַ
		final String path = "http://192.168.1.101:8080/wolf.jpg";
		final File file = new File(getCacheDir(),getFileName(path));
		
		//�����ļ��Ƿ����
    	if(file.exists()){
    		//���������ڣ��ӻ����ȡ
    		Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
    		iv.setImageBitmap(bm);
    		Toast.makeText(MainActivity.this, "�ӻ����ж�ȡ����", Toast.LENGTH_SHORT).show();
    	}else{
    		//������治���ڣ�����������
    		Thread thread = new Thread(){
        		@Override
        		public void run() {
        			
        			try{
        				//2.����ַ��װ��һ��URL����
        				URL url = new URL(path);
        				//3.��ȡ�ͻ��˺ͷ���˵����Ӷ��󣬴˿̻�û�д�������
        				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        				//4.�����Ӷ����ʼ��
        				  //�������󷽷���ע���д
        				conn.setRequestMethod("GET");
        				  //�������ӳ�ʱ
        				conn.setConnectTimeout(5000);
        				  //���ö�ȡ��ʱ
        				conn.setReadTimeout(5000);
        				//5.�����������������������
        				conn.connect();
        				//�����Ӧ��Ϊ200��˵����Ӧ�ɹ�
        				
        				if(conn.getResponseCode() == 200){
        					//��÷�������Ӧͷ�����������������ݾ��ǿͻ������������
        					InputStream is = conn.getInputStream();
        					
        					//������������ݣ�д���ֻ����أ���������
        					FileOutputStream fos = new FileOutputStream(file);
        					byte[] b = new byte[1024];
        					int len = 0;
        					
        					while((len = is.read(b)) != -1 ){
        						fos.write(b, 0, len);
        					}
        					fos.close();
        					
        					//�ӱ��ػ�������λͼ��
        					Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
        					
//        					Bitmap bm = BitmapFactory.decodeStream(is);
        					
//        					ImageView iv = (ImageView) findViewById(R.id.iv);
//        					//��λͼ����ʾ��imageview
//        					iv.setImageBitmap(bm);
        					
        					
        					
        					Message msg = handler.obtainMessage();
        					//��Ϣ�����Я������
        					msg.obj = bm;
        					//���
        					msg.what = 1;
        					//����Ϣ���������߳���Ϣ����
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
