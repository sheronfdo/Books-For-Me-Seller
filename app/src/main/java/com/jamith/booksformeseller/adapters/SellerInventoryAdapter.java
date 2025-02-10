package com.jamith.booksformeseller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jamith.booksformeseller.R;
import com.jamith.booksformeseller.model.Book;
import com.jamith.booksformeseller.model.BookStockData;

import java.util.List;

public class SellerInventoryAdapter extends RecyclerView.Adapter<SellerInventoryAdapter.ViewHolder> {

    private Context context;
    private List<BookStockData> bookList;

    public SellerInventoryAdapter(Context context, List<BookStockData> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookStockData book = bookList.get(position);

        // Load book details
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.stockTextView.setText("Stock: " + book.getStock());
        holder.priceTextView.setText("Price: LKR " + book.getPrice());
        holder.conditionTextView.setText("Condition: " + book.getCondition());

        // Load cover image using Glide
        Glide.with(context)
                .load(book.getCoverImage())
                .placeholder(R.drawable.books_placeholder)
                .error(R.drawable.books_placeholder)
                .into(holder.coverImageView);


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageView;
        TextView titleTextView, authorTextView, stockTextView, priceTextView, conditionTextView, locationTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.SellerInventoryActivityCoverImageView);
            titleTextView = itemView.findViewById(R.id.SellerInventoryActivityTitleTextView);
            authorTextView = itemView.findViewById(R.id.SellerInventoryActivityAuthorTextView);
            stockTextView = itemView.findViewById(R.id.SellerInventoryActivityStockTextView);
            priceTextView = itemView.findViewById(R.id.SellerInventoryActivityPriceTextView);
            conditionTextView = itemView.findViewById(R.id.SellerInventoryActivityConditionTextView);
        }
    }
}