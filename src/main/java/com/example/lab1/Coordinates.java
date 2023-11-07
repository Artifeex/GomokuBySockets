package com.example.lab1;

import java.io.Serializable;

public class Coordinates implements Serializable {

    private int rowIndex;
    private int columnIndex;

    public Coordinates(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}
