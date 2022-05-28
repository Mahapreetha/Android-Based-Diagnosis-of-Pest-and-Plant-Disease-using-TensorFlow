package greymatter.com.plantsaver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class PestActivity extends AppCompatActivity {
    int Image_Request_Code = 7;
    ImageView imgview;
    Uri FilePathUri;
    TextView tv;
    String pest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest);

        imgview=findViewById(R.id.image);
        Button btn =findViewById(R.id.btnupload);
        tv = findViewById(R.id.textView6);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PestActivity.this,WebActivity.class);
                intent.putExtra("name",pest);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();
            File file = new File(FilePathUri.getPath());
            String name = file.getName().replace(".jpg","");

            pest = name;
            tv.setText("Pest Name : "+name);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imgview.setImageBitmap(bitmap);
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
