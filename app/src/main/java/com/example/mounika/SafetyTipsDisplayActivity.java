package com.example.mounika;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mounika.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SafetyTipsDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips_display);

        String categoryTitle = getIntent().getStringExtra("category_title");
        String[] safetyTips = readSafetyTipsFromFile(categoryTitle.toLowerCase() + "_tips.txt");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSafetyTips);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SafetyTipsAdapter(safetyTips));
    }

    private String[] readSafetyTipsFromFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().split("\n");
    }

    static class SafetyTipsAdapter extends RecyclerView.Adapter<SafetyTipsAdapter.TipViewHolder> {

        private String[] tips;

        public SafetyTipsAdapter(String[] tips) {
            this.tips = tips;
        }

        @NonNull
        @Override
        public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.safety_tip_item, parent, false);
            return new TipViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
            String tip = tips[position];
            holder.tvTipContent.setText(tip);
        }

        @Override
        public int getItemCount() {
            return tips.length;
        }

        class TipViewHolder extends RecyclerView.ViewHolder {
            TextView tvTipContent;

            public TipViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTipContent = itemView.findViewById(R.id.tvTipContent);
            }
        }
    }
}
