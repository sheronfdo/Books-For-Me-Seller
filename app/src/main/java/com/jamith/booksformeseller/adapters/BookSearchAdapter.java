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

import java.util.List;

public class BookSearchAdapter extends RecyclerView.Adapter<BookSearchAdapter.ViewHolder> {

    private Context context;
    private List<Book> bookList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookSearchAdapter(Context context, List<Book> bookList, OnItemClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        Glide.with(context)
                .load(book.getCoverImage())
                .placeholder(R.drawable.books_placeholder)
                .error(R.drawable.books_placeholder)
                .into(holder.coverImageView);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(book));


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, authorTextView;
        ImageView coverImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            coverImageView = itemView.findViewById(R.id.coverImageView);
        }
    }
}