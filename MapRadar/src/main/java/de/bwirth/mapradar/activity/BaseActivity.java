package de.bwirth.mapradar.activity;
import android.animation.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.*;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import de.bwirth.mapradar.apputil.GooglePlacesActivity;
import de.bwirth.mapradar.main.MapApplication;
import de.ip.mapradar.R;

import java.util.*;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer, login and authentication, Action Bar tweaks, amongst others.
 */
public abstract class BaseActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    // Navigation drawer:
    private DrawerLayout mDrawerLayout;
    private TextView titleView;
    private ObjectAnimator mStatusBarColorAnimator;
    private ViewGroup mDrawerItemsListContainer;
    private Handler mHandler;
    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();
    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 200;
    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_HOME = 0;
    protected static final int NAVDRAWER_ITEM_EVENTS = 1;
    protected static final int NAVDRAWER_ITEM_SEARCH = 2;
    protected static final int NAVDRAWER_ITEM_FAVORITES = 3;
    protected static final int NAVDRAWER_ITEM_HELP = 4;
    protected static final int NAVDRAWER_ITEM_ABOUT = 5;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 6;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_home,
            R.string.navdrawer_item_events,
            R.string.navdrawer_item_search,
            R.string.navdrawer_item_favs,
            R.string.navdrawer_item_help,
            R.string.navdrawer_item_about,
            R.string.navdrawer_item_settings,
    };
    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_explore_black_48dp,
            R.drawable.ic_event_grey,
            R.drawable.ic_search_grey,
            R.drawable.ic_favorite_grey,
            R.drawable.ic_help_black,
            R.drawable.ic_info_black,
            R.drawable.ic_settings_black
    };
    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;
    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<>();
    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;
    // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // Primary toolbar and drawer toggle
    private android.support.v7.widget.Toolbar mActionBarToolbar;
    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;
    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mOnDrawerClosedRunnable;
    private int mThemedStatusBarColor;
    private int mNormalStatusBarColor;
    private int mProgressBarTopWhenActionBarShown;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mHandler = new Handler();
        mToast= new Toast(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
//        getActionBarToolbar();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mThemedStatusBarColor = getResources().getColor(R.color.theme_primary);
        mNormalStatusBarColor = mThemedStatusBarColor;
    }

    private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
//            mSwipeRefreshLayout.setcol
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onRefreshPulled(mSwipeRefreshLayout);
                }
            });
        }
    }

    /**
     * Overwrite this method no be notified when the sliderefreshlayout with the default id was pulled
     *
     * @param mSwipeRefreshLayout
     */
    protected void onRefreshPulled(SwipeRefreshLayout mSwipeRefreshLayout) {
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.theme_primary));

        ScrollView navDrawer = (ScrollView)
                mDrawerLayout.findViewById(R.id.navdrawer_base);
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer_hamburger);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {

                // run deferred action, if we have one
                if (mOnDrawerClosedRunnable != null) {
                    mOnDrawerClosedRunnable.run();
                    mOnDrawerClosedRunnable = null;
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
//        if (!PrefUtils.isWelcomeDone(this)) {
//            // first run of the app starts with the nav drawer open
//            PrefUtils.markWelcomeDone(this);
//            mDrawerLayout.openDrawer(Gravity.START);
//        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    // Subclasses can override this for custom behavior
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
    }

    protected void onNavDrawerSlide(float offset) {
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    /**
     * Populates the navigation drawer with the appropriate items.
     */
    private void populateNavDrawer() {
        mNavDrawerItems.clear();
        mNavDrawerItems.add(NAVDRAWER_ITEM_HOME);
//        mNavDrawerItems.add(NAVDRAWER_ITEM_EVENTS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEARCH);
        mNavDrawerItems.add(NAVDRAWER_ITEM_FAVORITES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        mNavDrawerItems.add(NAVDRAWER_ITEM_HELP);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        createNavDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            if (isSpecialItem(getSelfNavDrawerItem()) || getSelfNavDrawerItem() == NAVDRAWER_ITEM_HOME) {
                // if in settings or home activity return to last actiity
                super.onBackPressed();
            } else {
                // go to home activity
                goToNavDrawerItem(NAVDRAWER_ITEM_HOME);
            }
        }
    }

    private void createNavDrawerItems() {
        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }
        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(PrefUtils.PREF_ATTENDEE_AT_VENUE)) {
//            LOGD(TAG, "Attendee at venue preference changed, repopulating nav drawer and menu.");
//            populateNavDrawer();
//            invalidateOptionsMenu();
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        trySetupSwipeRefresh();
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // TODO Hier kommen alle Actions aus dem Optionsmenu hin, die in mehreren Activities erscheinen
            // TODO z.B. Über, oder karte, ...
//            case R.id.menu_map:
//                startActivity(new Intent(this, UIUtils.getMapActivityClass(this)));
//                finish();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected MaterialDialog createCategoryChooserDialog() {
        String[] caz = MapApplication.getInstance().getGoogleCategoryNames();
        final Integer[] selectedIndizes = MapApplication.getInstance().getSelectedCategoryIndices();
        return new MaterialDialog.Builder(this)
                .items(caz)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .title(R.string.choose_categories)
                .itemsCallbackMultiChoice(selectedIndizes, new MaterialDialog.ListCallbackMulti() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        onCategoriesSelected(integers);
                    }
                })

                .build();
    }

    protected void onCategoriesSelected(Integer[] integers) {
        MapApplication.getInstance().setSelectedCategoryIndices(Arrays.asList(integers));
    }

    private void goToNavDrawerItem(int item) {
        Intent intent = null;
        switch (item) {
            case NAVDRAWER_ITEM_HOME:
                intent = new Intent(this, ExploreActivity.class);
                break;
//            case NAVDRAWER_ITEM_EVENTS:
//                intent = new Intent(this, EventsActivity.class);
//                break;
            case NAVDRAWER_ITEM_SEARCH:
//                intent = new Intent(this, SearchActivity.class);
                intent = new Intent(this, GooglePlacesActivity.class);
                break;
            case NAVDRAWER_ITEM_FAVORITES:
                intent = new Intent(this, FavouritesActivity.class);
                break;
            case NAVDRAWER_ITEM_HELP:
                intent = new Intent(this, TutorialActivity.class);
                break;
            case NAVDRAWER_ITEM_ABOUT:
                intent = new Intent(this, AboutActivity.class);
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            if (!isSpecialItem(item)) {
                finish();
            }
        }
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
//            View mainContent = findViewById(R.id.main_content);
//            if (mainContent != null) {
//                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
//            }
        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    protected void toast(Object msg,boolean isLong){
        mToast.cancel();
        mToast = Toast.makeText(this,String.valueOf(msg),isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void enableFadingActionBar(RecyclerView recyclerView, int offsetDP, int resolvedColor) {
        final int[] scrollOffsetXY = {0, 0};
        final float elevation = getActionBarToolbar().getElevation();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final float logicalDensity = metrics.density;
        final float whole = offsetDP * logicalDensity;
        final int[] rgb = {Color.red(resolvedColor), Color.green(resolvedColor), Color.blue(resolvedColor)};
        getActionBarToolbar().setBackgroundColor(Color.argb(0, rgb[0], rgb[1], rgb[2]));

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollOffsetXY[1] += dy;
                float percentage = (float) scrollOffsetXY[1] / whole;
                percentage = Math.min(percentage, 1);
                percentage = Math.max(percentage, 0);
                getActionBarToolbar().setBackgroundColor(Color.argb((int) (255 * percentage), rgb[0], rgb[1], rgb[2]));
                getActionBarToolbar().setElevation(elevation);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    protected void enableFadingActionBar(ObservableScrollView scrollView, int offsetDP, int resolvedColor) {
        final String title = getTitle().toString();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final float logicalDensity = metrics.density;
        final float whole = offsetDP * logicalDensity;
        final int[] rgb = {Color.red(resolvedColor), Color.green(resolvedColor), Color.blue(resolvedColor)};
        getActionBarToolbar().setBackgroundColor(Color.argb(0, rgb[0], rgb[1], rgb[2]));
        setTitle("");

        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                float percentage = (float) y / whole;
                percentage = Math.min(percentage, 1);
                percentage = Math.max(percentage, 0);
                getActionBarToolbar().setBackgroundColor(Color.argb((int) (255 * percentage), rgb[0], rgb[1], rgb[2]));
                onContentScrolled(percentage,y);
            }
        });
    }

    /**
     * Gets called if you have set enableFadingActionBar
     *
     * @param percentage How many pixels the content has scrolled in relative to the offset specified in enableFadingActionBar
     */
    protected void onContentScrolled(float percentage, int newY) {

    }

    protected android.support.v7.widget.Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                titleView = (TextView) findViewById(R.id.toolbar_title);
                setTitle(getTitle());
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (getActionBarToolbar() != null) {
            if (titleView == null) {
                getActionBarToolbar().setTitle(title);
            } else {
                titleView.setText(title);
            }
        }
        //        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
//        setTaskDescription(new ActivityManager.TaskDescription("Activity 2",bmp,getResources().getColor(R.color.theme_primary)));
    }

    protected TextView getTitleView() {
        return titleView;
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            // we are done
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        if (titleId > 0) {
            titleView.setText(getString(titleId));
        }
        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS
                || itemId == NAVDRAWER_ITEM_HELP
                || itemId == NAVDRAWER_ITEM_ABOUT;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        if (selected) {
            view.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.theme_primary) :
                getResources().getColor(android.R.color.black));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.theme_primary) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    public void setNormalStatusBarColor(int color) {
        mNormalStatusBarColor = color;
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(mNormalStatusBarColor);
        }
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(
                mDrawerLayout,
                (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK)
                .setDuration(250);
        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        for (View view : mHideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }
}
