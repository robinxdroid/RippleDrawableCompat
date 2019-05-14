package net.robinx.rippledrawablecompat;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.robinx.rippledrawable.RippleDrawableCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv1 = findViewById(R.id.tv_1);
        TextView tv2 = findViewById(R.id.tv_2);
        TextView tv3 = findViewById(R.id.tv_3);

        RippleDrawableCompat.createCenterRipple(tv1, Color.RED);

        RippleDrawableCompat.createRipple(tv2, Color.RED);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleDrawableCompat.createRippleCompat(tv3, Color.RED);
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
