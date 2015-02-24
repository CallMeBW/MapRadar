package de.ip.mapradar.activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import de.ip.mapradar.*;
import de.ip.mapradar.adapter.ImagePagerAdapter;
import de.ip.mapradar.main.MapApplication;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 14.01.2015            <br></code>
 * Description:                    <br>
 */
public class TutorialActivity extends ActionBarActivity {
    private int[] bitmapRes, stringDescrRes, stringTitleRes;
    private ViewPager viewpager;
    private ImageView viewIndicator;
    private Button button0, button1;
    private ViewGroup container;
    private int[] colorBGs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int[] indicatorRes = new int[]{
                R.drawable.ic_viewpager_indicator_0,
                R.drawable.ic_viewpager_indicator_1,
                R.drawable.ic_viewpager_indicator_2,
                R.drawable.ic_viewpager_indicator_3,
                R.drawable.ic_viewpager_indicator_4
        };
        bitmapRes = new int[]{
                R.drawable.tutorial1,
                R.drawable.tutorial2,
                R.drawable.tutorial3,
                R.drawable.tutorial4,
                R.drawable.tutorial5
        };
        stringDescrRes = new int[]{
                R.string.tutorial_1,
                R.string.tutorial_2,
                R.string.tutorial_3,
                R.string.tutorial_4,
                R.string.tutorial_5,
        };
        stringTitleRes = new int[]{
                R.string.tutorial_title_1,
                R.string.tutorial_title_2,
                R.string.tutorial_title_3,
                R.string.tutorial_title_4,
                R.string.tutorial_title_5,
        };
        colorBGs = new int[]{
                getResources().getColor(R.color.theme_primary),
                getResources().getColor(R.color.theme_primary),
                getResources().getColor(R.color.theme_primary),
                getResources().getColor(R.color.theme_primary),
                getResources().getColor(R.color.theme_primary)
        };

        ImagePagerAdapter adapter = new ImagePagerAdapter(stringTitleRes, bitmapRes, stringDescrRes);
        setContentView(R.layout.tutorial_layout);
        viewpager = (ViewPager) findViewById(R.id.tutorial_viewpager);
        viewIndicator = (ImageView) findViewById(R.id.tutorial_viewpager_indicator);
        button0 = (Button) findViewById(R.id.tutorial_button_skip);
        button1 = (Button) findViewById(R.id.tutorial_button_continue);
        container = (ViewGroup) findViewById(R.id.tutorial_master_layout);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        container.setBackgroundColor(colorBGs[0]);

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 5) {
                    finish();
                    return;
                }
                viewIndicator.setImageDrawable(getResources().getDrawable(indicatorRes[i]));
                button1.setText(i == 4 ? "Los geht's!" : "Weiter");
                button0.setVisibility(i >= 4 ? View.GONE : View.VISIBLE);
                container.setBackgroundColor(colorBGs[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewpager.getCurrentItem() < 4) {
                    viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
                } else {
                    finish();
                }
            }
        });
        MapApplication.getInstance().setFirstRunned();
    }
}
