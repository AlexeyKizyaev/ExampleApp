package com.example.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;

import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.inventory.ProductQuery;

public class MainActivity extends IntegrationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_getProduct).setOnClickListener(view -> getProduct());
    }

    public void getProduct() {
        var products = new ProductQuery().price.lower(new BigDecimal(500), true)
                .and().name.like("TestQR", null).limit(1000)
                .execute(getApplicationContext());

        if (products != null && products.moveToFirst()) {
            StringBuilder productInfo = new StringBuilder();
            do {
                // Получение данных из курсора
                String name = products.getString(products.getColumnIndex("NAME")); // колонка "NAME"
                BigDecimal price = new BigDecimal(products.getString(products.getColumnIndex("PRICE_OUT"))); // колонка "PRICE_OUT"


                // Формирование строки
                productInfo.append("Название: ").append(name)
                        .append(", Цена: ").append(price)
                        .append("\n");
            } while (products.moveToNext());

            // Отображение данных
            Toast.makeText(getApplicationContext(), productInfo.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Продукты не найдены", Toast.LENGTH_SHORT).show();
        }

        // Закрываем Cursor для освобождения ресурсов
        if (products != null) {
            products.close();
        }
    }
}