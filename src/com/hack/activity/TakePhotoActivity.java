package com.hack.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.hack.core.Hack;
import com.hack.utils.FileUtils;

public class TakePhotoActivity extends BaseActivity {
	private static final int NONE = 0;
	private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private ImageView imageView = null;
    private Button btnTakePicture = null;
    MenuItem mSpinnerItem; 
    private String filename;
    Spinner spinner;
    Button add,ok,cancel;
    File picture;
    ArrayList<String> names;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);
        initView();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        filename = System.currentTimeMillis()+".jpg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),filename)));
        startActivityForResult(intent, PHOTO_GRAPH);
    }
    
    private void initView() {
		// TODO Auto-generated method stub
    	final EditText et = new EditText(TakePhotoActivity.this);
    	findViewById(R.id.add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(TakePhotoActivity.this).setTitle("请输入").setIcon(
					     android.R.drawable.ic_dialog_info).setView(
					    		 et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								System.out.println(et.getText());
								Hack.addGallery(getApplicationContext(), et.getText().toString());
								names = Hack.getGalleryArray(getApplicationContext());
								adapter=new ArrayAdapter<String>(TakePhotoActivity.this, android.R.layout.simple_list_item_1,names); 
								adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);  
								spinner.setAdapter(adapter);  
							}
						})
					     .setNegativeButton("取消", null).show();
			}
		});
    	findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (names.size() ==0){
					makeToast("您需要新建一次旅行");
					return;
				}
				File picture = new File(Environment.getExternalStorageDirectory() + "/" + filename);
				String name = spinner.getSelectedItem().toString()+Hack.getNextPhotoId(getApplicationContext());
				UploadMetaData metaData = new UploadMetaData();
				metaData.setPublicFlag(true);
				metaData.setTitle(name);
				Hack.uploadImage(TakePhotoActivity.this, FileUtils.getBytesFromFile(picture), name, metaData);
				Hack.galleryID = spinner.getSelectedItem().toString();
				finish();
			}
		});
    	findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	imageView = (ImageView)findViewById(R.id.imageview);
    	spinner = (Spinner)findViewById(R.id.spinner_gallery);
    	names = Hack.getGalleryArray(getApplicationContext());
    	adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);  
    	adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);  
    	spinner.setAdapter(adapter);  
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {  
  
            @Override  
            public void onItemSelected(AdapterView<?> arg0, View arg1,  
                    int arg2, long arg3) {  
                Hack.galleryID =  names.get(arg2); 
            }  
  
            @Override  
            public void onNothingSelected(AdapterView<?> arg0) {  
                // TODO Auto-generated method stub  
                  
            }  
        });  
	}
    
   
	

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE){
        	finish();
            return;
        }
        if (requestCode == PHOTO_GRAPH) {
            File picture = new File(Environment.getExternalStorageDirectory() + "/" + filename);
            //startPhotoZoom(Uri.fromFile(picture));
            imageView.setImageURI(Uri.fromFile(picture));
            return;
        }

        if (data == null){
        	finish();
        
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
