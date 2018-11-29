package test.szreach.com.test;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.itemHolder> {

    private List<Bean> data;
    private onItemClick onItemClick;

    public ItemAdapter(List<Bean> data) {
        this.data=data;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new itemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, final int position) {
        Bean bean=data.get(position);
        holder.bindData(bean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick!=null){
                    onItemClick.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class itemHolder extends RecyclerView.ViewHolder{

        TextView tv;

        public itemHolder(View itemView) {
            super(itemView);
            tv=itemView.findViewById(R.id.item_tv);
        }

        private void bindData(Bean bean){
            tv.setText(bean.getTv());
        }


    }


    public interface onItemClick{
        void onItemClick(int position);
    }

    public void setOnItemClick(onItemClick onItemClick){
        this.onItemClick=onItemClick;
    }
}
