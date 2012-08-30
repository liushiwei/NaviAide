package com.carit.imhere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carit.imhere.obj.PlaceSearchResult;

public class ParkingAdapter extends BaseAdapter {
    private PlaceSearchResult mResult;
    public PlaceSearchResult getResult() {
        return mResult;
    }

    public void setResult(PlaceSearchResult mResult) {
        this.mResult = mResult;
    }

    private Context mContext;
    private int mLayout;
    public ParkingAdapter(Context context,PlaceSearchResult result,int layout){
        mResult = result;
        mContext = context;
        mLayout = layout;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        if(mResult!=null)
        return mResult.getResults().length;
        else
            return 0;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewWrapper wrapper = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(mLayout, parent, false);
            wrapper = new ViewWrapper(row);
            row.setTag(wrapper);
        } else {
            wrapper = (ViewWrapper) row.getTag();
        }
        wrapper.getIcon().setImageResource(R.drawable.pin);
        wrapper.getMarkName().setText(mResult.getResults()[position].getName());
        wrapper.getMarkPlace().setText(mResult.getResults()[position].getVicinity());
        return row;
    }

    class ViewWrapper {
        View base;

        TextView markName = null;

        TextView markPlace = null;

        ImageView icon = null;


        int id = -1;

        ViewWrapper(View base) {
            this.base = base;
            id = -1;
        }

        TextView getMarkName() {
            if (markName == null) {
                markName = (TextView) base.findViewById(R.id.markName);
            }
            return (markName);
        }
        
        TextView getMarkPlace() {
            if (markPlace == null) {
                markPlace = (TextView) base.findViewById(R.id.markPlace);
            }
            return (markPlace);
        }

        ImageView getIcon() {
            if (icon == null) {
                icon = (ImageView) base.findViewById(R.id.mark);
            }

            return (icon);
        }

        int getId() {
            return id;
        }

        void setId(int deviceId) {
            id = deviceId;
        }

       
    }
    

}
