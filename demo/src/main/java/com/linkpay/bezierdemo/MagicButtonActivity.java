package com.linkpay.bezierdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lcm.magicbutton.MagicButton;

public class MagicButtonActivity extends AppCompatActivity {

    private MagicButton magicButton;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_button);


        magicButton = (MagicButton) findViewById(R.id.magicButton);

        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magicButton.startAnimator();
            }
        });
    }
}
