package com.example.exampleapp;

import android.app.Service;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.FiscalDocument;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionAllSubpositionsFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePositionFooter;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;

public class ExtraPrintService extends IntegrationService {

    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText("Самый верх\n"),
                                        //Штрихкод с контрольной суммой если она требуется для выбранного типа штрихкода
                                        //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //Изображение
                                        //new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        new PrintableText("После кассового чека\n")
                                }
                        ));
                        //Добавляем к каждой позиции чека продажи необходимые данные
                        Receipt r = ReceiptApi.getReceipt(ExtraPrintService.this, Receipt.Type.SELL);
                        if (r != null) {
                            setPrintExtras.add(new SetPrintExtra(
                                    //Данные печатаются после итога и списка оплат, до текста “всего оплачено”
                                    new PrintExtraPlacePrintGroupSummary(null),
                                    new IPrintable[]{
                                            //new PrintableText("EXTRA:" + r.getHeader().getExtra()),
                                            //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                            new PrintableText("После всех итогов и оплат\n")
                                    }
                            ));
                            for (Position p : r.getPositions()) {
                                List<ExtraKey> list = new ArrayList<>(p.getExtraKeys());
                                setPrintExtras.add(new SetPrintExtra(
                                        //Данные печатаются в позиции в чеке, до подпозиций
                                        new PrintExtraPlacePositionFooter(p.getUuid()),
                                        new IPrintable[]{
                                                //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                                new PrintableText("До подпозиций ______\n")
                                        }
                                ));
                                setPrintExtras.add(new SetPrintExtra(
                                        //Данные печатаются в позиции в чеке, после всех подпозиций
                                        new PrintExtraPlacePositionAllSubpositionsFooter(p.getUuid()),
                                        new IPrintable[]{
                                                //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                                new PrintableText("После подпозиций______\n")
                                        }
                                ));

                            }
                        }
                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
        );
        return map;
    }
}


