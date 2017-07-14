package com.linkpay.bezierdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lcm.magicbutton.MagicButton;
import com.lcm.magicbutton.OnCheckChangeListenter;

public class MagicButtonActivity extends AppCompatActivity {

    private MagicButton magicButton;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_button);


        magicButton = (MagicButton) findViewById(R.id.magicButton);
        magicButton.setOnCheckChangeListenter(new OnCheckChangeListenter() {
            @Override
            public void onCheck(boolean isChecked) {
                Toast.makeText(getApplicationContext(), isChecked ? "checked!" : "unChecked!!!", Toast.LENGTH_SHORT).show();
            }
        });

        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magicButton.setChecked(!magicButton.isChecked());
            }
        });
    }
}
