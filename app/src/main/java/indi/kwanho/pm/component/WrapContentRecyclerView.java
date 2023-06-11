package indi.kwanho.pm.component;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class WrapContentRecyclerView extends RecyclerView {

    public WrapContentRecyclerView(Context context) {
        super(context);
    }

    public WrapContentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapContentRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}

