package de.bwirth.mapradar.view;
import android.app.Activity;
import android.content.*;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;
import de.bwirth.mapradar.model.Business;
import de.ip.mapradar.R;
import de.bwirth.mapradar.activity.BusinessSmallCardRecyclerAdapter;

import java.util.Arrays;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 09.12.2014            <br></code>
 * Description:                    <br>
 */
public class MultipleCardView extends LinearLayout {
    private BusinessSmallCardRecyclerAdapter.OnItemClickListener onItemClickListener;
    RecyclerView recList;
    public MultipleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultipleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultipleCardView(final Activity c, BusinessSmallCardRecyclerAdapter.OnItemClickListener onItemClickListener) {
        super(c);
        this.onItemClickListener = onItemClickListener;
        LinearLayout container = (LinearLayout) c.getLayoutInflater().inflate(R.layout.card_holder, this);
        recList = (RecyclerView) container.findViewById(R.id.multiple_card_recyclerview);
        recList.setHasFixedSize(true);
        GridLayoutManager llm = new GridLayoutManager(c, 1, GridLayoutManager.HORIZONTAL,
                false);
        recList.setLayoutManager(llm);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
    }

    public void setAdapter(Business[] businesses) {
        BusinessSmallCardRecyclerAdapter adapter = new BusinessSmallCardRecyclerAdapter(Arrays.asList(businesses), onItemClickListener);
        recList.setAdapter(adapter);
    }
}
