package com.example.contatodeemergencia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// Adapter Class
public class MyAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Contato> mOriginalValues; // Original Values
    private ArrayList<Contato> mDisplayedValues;    // Values to be displayed
    private LayoutInflater inflater;
    private Context ctx;

    public MyAdapter(Context context, ArrayList<Contato> mContatoArrayList) {
        this.mOriginalValues = mContatoArrayList;
        this.mDisplayedValues = mContatoArrayList;
        inflater = LayoutInflater.from(context);
        this.ctx = context;
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout llContainer;
        TextView tvNome,tvNumero;
        ImageView ic_emergency, ic_call;
        CheckBox checkBoxEmergency;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.lv_layout, null);
            holder.llContainer = (LinearLayout)convertView.findViewById(R.id.llContainer);
            holder.tvNome = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvNumero = (TextView) convertView.findViewById(R.id.tv_details);
            holder.ic_emergency = (ImageView) convertView.findViewById(R.id.icon_emerg);
            holder.ic_call = (ImageView) convertView.findViewById(R.id.icon_call);
            holder.checkBoxEmergency = (CheckBox) convertView.findViewById(R.id.checkbox_emergency);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNome.setText(mDisplayedValues.get(position).nome);
        holder.tvNumero.setText(mDisplayedValues.get(position).numero+"");
        holder.ic_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chamada
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mDisplayedValues.get(position).numero));
                ctx.startActivity(intent);
            }
        });


        holder.checkBoxEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                boolean adicionado = false;
                if(isChecked){
                    Log.d("debug", mDisplayedValues.get(0).numero);
                    for (int i = 0; i < 5; i++){
                        if(mDisplayedValues.get(i).getNumero().equals("Livre") && adicionado == false){
                            mDisplayedValues.add(i, mDisplayedValues.get(position));
                            Log.d("debug", mDisplayedValues.get(0).nome);
                            adicionado = true;
                        }
                    }
                }else{
                }
                notifyDataSetChanged();
            }

        });


        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<Contato>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Contato> FilteredArrList = new ArrayList<Contato>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Contato>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *Product
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).nome;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Contato(mOriginalValues.get(i).nome,mOriginalValues.get(i).numero));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
