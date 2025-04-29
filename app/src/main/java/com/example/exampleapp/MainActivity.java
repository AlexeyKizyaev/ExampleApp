package com.example.exampleapp;

import static ru.evotor.framework.receipt.ReceiptApi.getReceipt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ru.evotor.framework.FiscalDocument;
import ru.evotor.framework.component.PaymentDelegatorApi;
import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetPurchaserContactData;
import ru.evotor.framework.features.FeaturesApi;
import ru.evotor.framework.inventory.ProductQuery;
import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.query.Cursor;

public class MainActivity extends IntegrationActivity {

    CreatePositions positions = new CreatePositions();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            findViewById(R.id.button_getProduct).setOnClickListener(view -> getProduct());
            findViewById(R.id.button_getReceipt).setOnClickListener(view -> openReceipt());
            findViewById(R.id.button_getProductTaxNumber).setOnClickListener(view -> getProductTaxNumber());
            findViewById(R.id.button_getSellReceipt).setOnClickListener(view -> getReceipt(getApplicationContext(), Receipt.Type.SELL).getPositions().get(0).getVeterinaryAttribute().getDocumentNumber().toString());
            findViewById(R.id.button_getFeaturesStatus).setOnClickListener(view -> {
                FeaturesApi.INSTANCE.isVat5And7Active(this);
                Toast.makeText(getApplicationContext(), String.valueOf(FeaturesApi.INSTANCE.isVat5And7Active(this)), Toast.LENGTH_SHORT).show();
            });
        }
        catch (Throwable t) {
        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
        throw t;
        }
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

    public void openReceipt() {

        new OpenSellReceiptCommand(positions.createPositionsAddList(), null).process(
                this,
                new IntegrationManagerCallback() {
                    @Override
                    public void run(IntegrationManagerFuture integrationManagerFuture) {
                        try {
                            IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                            if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                                //Чтобы открыть другие чеки используйте методы NavigationApi.
                                startActivity(NavigationApi.createIntentForSellReceiptPayment(false));
                            }
                        } catch (IntegrationException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getProductTaxNumber() {

        List<Position> positionList = getReceipt(getApplicationContext(), Receipt.Type.SELL).getPositions();

        for (int i = 0; i < positionList.size(); i++) {

            String taxNumber = positionList.get(i).getTaxNumber().name();
            Toast.makeText(getApplicationContext(), taxNumber, Toast.LENGTH_LONG).show();
        }
        /*Cursor<FiscalReceipt> fiscalReceipt = ReceiptApi.getFiscalReceipts(
                getApplicationContext(), UUID.randomUUID().toString());

        Long fiscalReceiptNumber = fiscalReceipt.getValue().getDocumentNumber();

        Toast.makeText(getApplicationContext(), fiscalReceiptNumber.toString(), Toast.LENGTH_SHORT).show();*/

        /*ReceiptApi.getReceipt(getApplicationContext(), Receipt.Type.SELL).getPositions().forEach(position -> {
            String taxNumber = position.getTaxNumber().name();
            Toast.makeText(getApplicationContext(), taxNumber, Toast.LENGTH_LONG).show();
        });*/

    }


}