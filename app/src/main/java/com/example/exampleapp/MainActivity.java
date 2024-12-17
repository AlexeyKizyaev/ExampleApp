package com.example.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

        var products = new ProductQuery()
                .alcoholByVolume.between(new BigDecimal(40), new BigDecimal(70))
                .and(new ProductQuery()
                        .price.greater(new BigDecimal(100), true)
                        .or().tareVolume.greater(new BigDecimal(10), true)
                        .or(new ProductQuery()
                                .price.lower(new BigDecimal(5), true)
                                .and().name.like("Спиртное", null)
                        )
                ).sortOrder(new ProductQuery.SortOrder()
                        .price.asc()
                        .alcoholByVolume.desc()
                        .tareVolume.asc()
                ).limit(1000)
                .execute(getApplicationContext());
    }
}