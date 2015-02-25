package de.ip.mapradar.view;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;
import de.ip.mapradar.R;
import de.ip.mapradar.activity.BusinessSmallCardRecyclerAdapter;
import de.ip.mapradar.model.Business;

import java.util.Arrays;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class MultipleCardView extends LinearLayout implements BusinessSmallCardRecyclerAdapter.OnItemClickListener {
    public MultipleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultipleCardView(final Activity c, Business[] businesses) {
        super(c);
        LinearLayout container = (LinearLayout) c.getLayoutInflater().inflate(R.layout.card_holder, this);
        final RecyclerView recList = (RecyclerView) container.findViewById(R.id.multiple_card_recyclerview);
        recList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(c, 1, GridLayoutManager.HORIZONTAL,
                false);
        recList.setLayoutManager(llm);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        BusinessSmallCardRecyclerAdapter adapter = new BusinessSmallCardRecyclerAdapter(Arrays.asList(businesses), this);
        recList.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(Business busi, View v, int pos) {

    }
}
