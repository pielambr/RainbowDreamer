package be.pielambr.colorpicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import be.pielambr.rainbowdreamer.OnColorSelectedListener;
import be.pielambr.rainbowdreamer.RainbowDreamer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RainbowDreamer dreamer = new RainbowDreamer();
                dreamer.setColors(new int[] {
                        R.color.olive, R.color.orange,
                        R.color.red, R.color.blue,
                        R.color.green, R.color.orange,
                        R.color.brown, R.color.magenta,
                        R.color.gray});
                dreamer.setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void selectColor(int color) {
                        findViewById(R.id.hello).setBackgroundColor(color);
                    }
                });
                dreamer.setSelectedColor(R.color.red);
                dreamer.show(getSupportFragmentManager(), "be.pielambr.rainbowdreamer.BLUB");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
