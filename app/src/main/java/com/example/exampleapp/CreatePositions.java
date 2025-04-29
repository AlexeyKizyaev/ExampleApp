package com.example.exampleapp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.receipt.DocumentType;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.TaxNumber;
import ru.evotor.framework.receipt.attribute.VeterinaryAttribute;
import ru.evotor.framework.receipt.position.Mark;
import ru.evotor.framework.receipt.position.SettlementMethod;

public class CreatePositions {

    public List<PositionAdd> createPositionsAddList() {

        List<PositionAdd> positions = new ArrayList<>();

        /*for (int i = 0; i < 3; i++) {

            var subPositions = List.of(
                    Position.Builder.newInstance(
                            UUID.randomUUID().toString(),
                            null,
                            "Субпозиция",
                            new Measure("дроб", 3, 255),
                            BigDecimal.ONE,
                            BigDecimal.TEN
                    ).build()
            );

            Position positionToBeAdded = Position.Builder.newInstance(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "Зажигалка" + i,
                    new Measure("Шт", 0, 11),
                    new BigDecimal(30),
                    new BigDecimal(1)
            ).setTaxNumber(TaxNumber.VAT_7).setSubPositions(subPositions).build();

            positions.add(new PositionAdd(positionToBeAdded));

        }*/

        Position positionToBeAdded2 = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Ветеринарный препарат по рецепту",
                new Measure("Шт", 0, 41),
                new BigDecimal(30),
                new BigDecimal(3)
        ).setTaxNumber(TaxNumber.NO_VAT).toVeterinaryMarked(new Mark.RawMark(UUID.randomUUID().toString())).setVeterinaryAttribute(
                new VeterinaryAttribute(
                        VeterinaryAttribute.VeterinaryDocumentType.VPT,
                        "1",
                        new Date().toString()
                )
        ).build();


        positions.add(new PositionAdd(positionToBeAdded2));

        return positions;
    }

    public List<Position> createPositionsList() {

        List<Position> positions = new ArrayList<>();

        Position positionToBeAdded = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Зажигалка",
                new Measure("шт", 0, 0),
                new BigDecimal(30),
                new BigDecimal(10000)
        ).setTaxNumber(TaxNumber.VAT_7).setSettlementMethod(new SettlementMethod.FullPrepayment()).build();

        Position positionToBeAdded2 = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Спички",
                new Measure("шт", 0, 0),
                new BigDecimal(30),
                new BigDecimal(3)
        ).setTaxNumber(TaxNumber.VAT_5).build();

        positions.add(positionToBeAdded);
        positions.add(positionToBeAdded2);

        return positions;
    }

    public List<PositionAdd> createMarkedPositionsAddList() {

        List<PositionAdd> markedPositionAddReceiptList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            Position positionToBeAdded = Position.Builder.newInstance(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "GodBeer" + i,
                    new Measure("л", 0, 41),
                    new BigDecimal(30),
                    new BigDecimal(1)).toBeerMarkedKeg(new Mark.RawMark(UUID.randomUUID().toString()), new BigDecimal(1), 12111111L, new BigDecimal(5)).build();


            markedPositionAddReceiptList.add(new PositionAdd(positionToBeAdded));

        }
        /*for (int i = 0; i < 5; i++) {

            Position positionToBeAdded = Position.Builder.newInstance(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "Marlboro" + i,
                    new Measure("шт", 0, 41),
                    new BigDecimal(30),
                    new BigDecimal(1)).toTobaccoMarked(new Mark.RawMark(UUID.randomUUID().toString())).build();


            markedPositionAddReceiptList.add(new PositionAdd(positionToBeAdded));

        }*/

        return markedPositionAddReceiptList;
    }
}
