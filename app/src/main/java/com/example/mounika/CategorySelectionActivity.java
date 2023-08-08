package com.example.mounika;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mounika.R;

public class CategorySelectionActivity extends AppCompatActivity {

    private String[] categories = {
            "General", "Travel", "Personal Security", "Self-Defense Techniques", "Online Safety",
            // Add other categories here
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryAdapter());
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            String category = categories[position];
            holder.btnCategoryTitle.setText(category);
            holder.btnCategoryTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSafetyTipsDisplayActivity(category);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.length;
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {
            Button btnCategoryTitle;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                btnCategoryTitle = itemView.findViewById(R.id.btnCategoryTitle);
            }
        }
    }

    private void openSafetyTipsDisplayActivity(String categoryTitle) {
        Intent intent = new Intent(this, SafetyTipsDisplayActivity.class);
        intent.putExtra("category_title", categoryTitle);
        startActivity(intent);
    }
}
