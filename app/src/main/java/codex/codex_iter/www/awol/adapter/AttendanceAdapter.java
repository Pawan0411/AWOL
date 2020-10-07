package codex.codex_iter.www.awol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.AttendanceData;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.myViewHolder> {

    private Context ctx;
    private List<AttendanceData> dataList;
    private int pre_minimum_attendance;

    public AttendanceAdapter(Context context, List<AttendanceData> dataList, int pref_minimum_attendance) {
        this.ctx = context;
        this.dataList = dataList;
        this.pre_minimum_attendance = pref_minimum_attendance;
    }

    @NonNull
    @Override
    public AttendanceAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.item_attendance, parent, false);
        return new myViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.myViewHolder holder, int position) {
        SharedPreferences theme = ctx.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        holder.sub.setText(dataList.get(position).getSub());
        String p = dataList.get(position).getPercent();

        double percent = 0;
        if (p != null) {
            percent = Double.parseDouble(p);
        }

        if (percent >= pre_minimum_attendance + 10) {
            holder.ta.setBackgroundColor(Color.parseColor("#0BBE62"));
        } else if (percent >= pre_minimum_attendance) {
            holder.ta.setBackgroundColor(Color.parseColor("#FFFF66"));
        } else {
            holder.ta.setBackgroundColor(Color.parseColor("#F5FC0101"));
        }
        holder.ta.setText(dataList.get(position).getPercent() + "%");
        String s = dataList.get(position).getOld();
        if (!s.equals("")) {
            double n = Double.parseDouble(dataList.get(position).getPercent());
            double o = Double.parseDouble(s);
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        if (!s.equals("")) {
            double n = Double.parseDouble(dataList.get(position).getPercent());
            double o = Double.parseDouble(s);
            //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        holder.lu.setText(dataList.get(position).getUpd());
        holder.th.setText(dataList.get(position).getTheory() + dataList.get(position).getThat());
        holder.prac.setText(dataList.get(position).getLab() + dataList.get(position).getLabt());
        holder.ab.setText(dataList.get(position).getAbsent());
        holder.tc.setText(dataList.get(position).getClasses());
        holder.bunk_text.setText(dataList.get(position).getBunk_text_str());

        if (!dark) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.ta.setTextColor(Color.parseColor("#141831"));
            holder.lu.setTextColor(Color.parseColor("#141831"));
            holder.th.setTextColor(Color.parseColor("#141831"));
            holder.prac.setTextColor(Color.parseColor("#141831"));
            holder.ab.setTextColor(Color.parseColor("#141831"));
            holder.tc.setTextColor(Color.parseColor("#141831"));
            // mViewHolder.total.setTextColor(Color.parseColor("#141831"));
            holder.updated.setTextColor(Color.parseColor("#141831"));
            holder.absents.setTextColor(Color.parseColor("#141831"));
            holder.pract.setTextColor(Color.parseColor("#141831"));
            holder.theory.setTextColor(Color.parseColor("#141831"));
            holder.classes.setTextColor(Color.parseColor("#141831"));
            holder.bunk_text.setTextColor(Color.parseColor("#141831"));
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView sub, lu, th, prac, ab, tc, theory, updated, pract, classes, absents, bunk_text;
        Button ta;
        ImageView up;
        CardView cardView;

        public myViewHolder(@NonNull View view) {
            super(view);

//            total = view.findViewById(R.id.total);
            theory = view.findViewById(R.id.theory_t);
            updated = view.findViewById(R.id.updated);
            pract = view.findViewById(R.id.practicle);
            classes = view.findViewById(R.id.classes);
            absents = view.findViewById(R.id.absents);
            cardView = view.findViewById(R.id.card_view);
            sub = view.findViewById(R.id.sub);
            lu = view.findViewById(R.id.lu);
            th = view.findViewById(R.id.theory);
            prac = view.findViewById(R.id.prac);
            ab = view.findViewById(R.id.ab);
            tc = view.findViewById(R.id.tc);
            ta = view.findViewById(R.id.ta);
            bunk_text = view.findViewById(R.id.bunk_text);

//            tha=view.findViewById(R.id.tha);
//            la=view.findViewById(R.id.la);
            up = view.findViewById(R.id.up);
            //down=view.findViewById(R.id.down);

        }
    }
}
