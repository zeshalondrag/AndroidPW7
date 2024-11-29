package com.example.paperdb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ProductActivity extends AppCompatActivity {

    private EditText titleText, sizeText, priceText;
    private ImageView itemImageView;
    private Button addButton, updateButton, deleteButton, selectImageButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private String selectedProductTitle;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        titleText = findViewById(R.id.titleText);
        sizeText = findViewById(R.id.sizeText);
        priceText = findViewById(R.id.priceText);
        itemImageView = findViewById(R.id.itemImageView);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        listView = findViewById(R.id.listView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getProductTitles());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedProductTitle = adapter.getItem(position);
            Product product = Paper.book().read(selectedProductTitle, null);
            if (product != null) {
                titleText.setText(product.getTitle());
                sizeText.setText(product.getSize());
                priceText.setText(String.valueOf(product.getPrice()));
                selectedImagePath = product.getImagePath();
                itemImageView.setImageURI(Uri.parse(selectedImagePath));
            }
        });

        addButton.setOnClickListener(view -> {
            String title = titleText.getText().toString();
            String size = sizeText.getText().toString();
            String priceStr = priceText.getText().toString();

            if (!title.isEmpty() && !size.isEmpty() && !priceStr.isEmpty() && selectedImagePath != null) {
                double price = Double.parseDouble(priceStr);
                Product product = new Product(title, size, price, selectedImagePath);
                Paper.book().write(title, product);
                updateProductList();
                clearInputs();
            } else {
                Toast.makeText(this, "Заполните все поля и выберите фото", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(view -> {
            if (selectedProductTitle == null) {
                Toast.makeText(ProductActivity.this, "Сначала выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = titleText.getText().toString();
            String size = sizeText.getText().toString();
            String priceStr = priceText.getText().toString();

            if (!title.isEmpty() && !size.isEmpty() && !priceStr.isEmpty() && selectedImagePath != null) {
                double price = Double.parseDouble(priceStr);
                Product updatedProduct = new Product(title, size, price, selectedImagePath);
                Paper.book().write(selectedProductTitle, updatedProduct);
                updateProductList();
                clearInputs();
            } else {
                Toast.makeText(this, "Заполните все поля и выберите фото", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(view -> {
            if (selectedProductTitle == null) {
                Toast.makeText(ProductActivity.this, "Сначала выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedProductTitle);
            updateProductList();
            clearInputs();
        });

        selectImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            selectedImagePath = selectedImage.toString();
            itemImageView.setImageURI(selectedImage);
        }
    }

    private List<String> getProductTitles() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }

    private void updateProductList() {
        adapter.clear();
        adapter.addAll(getProductTitles());
        adapter.notifyDataSetChanged();
    }

    private void clearInputs() {
        titleText.setText("");
        sizeText.setText("");
        priceText.setText("");
        itemImageView.setImageResource(R.drawable.ic_placeholder);
        selectedImagePath = null;
        selectedProductTitle = null;
    }
}