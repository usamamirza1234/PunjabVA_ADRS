package ast.adrs.va.IntimateDiseaseAuxiliaries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ast.adrs.va.IntroAuxiliaries.DModel_Animals;
import ast.adrs.va.R;
import ast.adrs.va.Utils.IAdapterCallback;

public class SpinnerFarmAdapter extends BaseAdapter {
    Context context;

    ArrayList<DModel_Animals> listData;
    LayoutInflater inflter;
    IAdapterCallback iAdapterCallback;

    public SpinnerFarmAdapter(Context context, ArrayList<DModel_Animals> listData) {
        this.context = context;
        this.listData = listData;

        inflter = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        int count = listData.size();
        return count > 0 ? count - 1 : count;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_spinner_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = convertView.findViewById(R.id.adptr_spnnr_itm_txt_item);
            viewHolder.txtName.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.spinner_item_bg));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(listData.get(position).getName());




        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_spinner_item_list, null);

            viewHolder = new ViewHolder();
            viewHolder.txtName = convertView.findViewById(R.id.adptr_spnnr_itm_txt_item);
            viewHolder.rlSpinner = convertView.findViewById(R.id.adptr_spnnr_itm_rlSpinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(listData.get(position).getName());

        return convertView;
    }

    public static class ViewHolder {
        TextView txtName;
        RelativeLayout rlSpinner;
    }
}
