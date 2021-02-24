package com.example.letseat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letseat.Interface.ItemClickListener;
import com.example.letseat.Model.Food;
import com.example.letseat.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    FirebaseRecyclerOptions<Food> options;


    //SEARCH FUNCTIONALITY
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    FirebaseRecyclerOptions<Food> searchOptions;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;



    String categoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //FIREBASE
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference().child("Foods");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //GET INTENT HERE
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId != null)
        {
            loadListFood(categoryId);
        }

        //SEARCH
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        //materialSearchBar.setSpeechMode(false); NO NEED, WE ALREADY DEFINED IT IN THE XML
        loadSuggest();//NEED TO WRITE FUNCTION TO LOAD SUGGESTED SEARCH FROM FIREBASE
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //WHEN USER TYPES TEXT, WE WILL CHANGE SUGGEST LIST
                List<String> suggest = new ArrayList<>();
                for (String search:suggestList) {
                    //LOOP IN SUGGEST LIST
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //WHEN SEARCH BAR IS CLOSED, RESTORE ORIGINAL SUGGEST ADAPTER
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }


            @Override
            public void onSearchConfirmed(CharSequence text) {
                //WHEN SEARCH IS FINISHED, SHOW RESULT OF ADAPTER
                startNewSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startNewSearch(CharSequence text) {
        searchOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("name").equalTo(text.toString()), Food.class).build();
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(searchOptions) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                String image = model.getImage();
                Uri imageUrl = Uri.parse(image);
                holder.food_name.setText(model.getName());
                Picasso.get()
                        .load(imageUrl)
                        .into(holder.food_image);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //START NEW ACTIVITY
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //SEND FOOD ID TO NEW ACTIVITY
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(v);
            }
        };
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);//SET ADAPTER FOR RECYCLERVIEW TO SEARCH RESULT

    }

    private void loadSuggest() {
        foodList.orderByChild(String.valueOf("menuId".equals(categoryId)))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot:snapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());//ADD NAME OF FOOD TO SUGGEST LIST
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void loadListFood(String categoryId) {
        options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodList.orderByChild("menuId").equalTo(categoryId), Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                String image = model.getImage();
                Uri imageUrl = Uri.parse(image);
                holder.food_name.setText(model.getName());
                Picasso.get()
                        .load(imageUrl)
                        .into(holder.food_image);

                final Food local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //START NEW ACTIVITY
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //SEND FOOD ID TO NEW ACTIVITY
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);

                return new FoodViewHolder(v);

            }
        };

        //SET ADAPTER
        adapter.startListening();
        Log.d("TAG", ""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);
    }
}