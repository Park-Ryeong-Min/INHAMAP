package com.example.inhamap.Components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.inhamap.R;

public class NoiseDetectingWarningDialog extends Dialog {

    private Context context;
    private String title;
    private String content;

    private TextView titleTextView;
    private TextView contentTextView;
    private Button okButton;

    public NoiseDetectingWarningDialog(Context context, String title, String content){
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면을 흐리게 표현
        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        windowLayoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(windowLayoutParams);

        setContentView(R.layout.path_breakway_warning_popup);

        this.titleTextView = (TextView)findViewById(R.id.noise_detecting_title);
        this.titleTextView.setText(this.title);
        this.contentTextView = (TextView)findViewById(R.id.noise_detecting_content);
        this.contentTextView.setText(this.content);
        this.okButton = (Button)findViewById(R.id.noise_detecting_ok_button);
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
