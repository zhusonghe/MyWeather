package com.example.administrator.mini_weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/21.
 */
public class Text extends Activity implements View.OnClickListener {

    Bundle bundle = new Bundle();
    String str = "";
    ImageView tes ;
    EditText edt;
    @Override
    protected void  onCreate(Bundle save){
         super.onCreate(save);
         setContentView(R.layout.test);


        tes = (ImageView) findViewById(R.id.title_back);
        tes.setOnClickListener(this);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        edt = (EditText) findViewById(R.id.text111);
        str = edt.getText().toString();
        SharedPreferences SharedPreferences1= getSharedPreferences("testone", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = SharedPreferences1.edit();
        editor.putString("content",str);
        editor.commit();
        Toast.makeText(this, "数据成功写入SharedPreferences！" , Toast.LENGTH_LONG).show();
        Log.d("test","Activity暂停！");
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences2= getSharedPreferences("testone", Activity.MODE_PRIVATE);
        String string = sharedPreferences2.getString("content","");
        Toast.makeText(this,string,Toast.LENGTH_LONG).show();
        Log.d("test","Activity恢复！");
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("test","Activity终止!");
    }

   @Override
   protected void  onSaveInstanceState(Bundle lala){
       lala=bundle;
       Log.d("test","Activitybao保存！");
   }

    @Override
    public void  onClick(View v){
        if (v.getId()==R.id.title_back){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

    }




}
