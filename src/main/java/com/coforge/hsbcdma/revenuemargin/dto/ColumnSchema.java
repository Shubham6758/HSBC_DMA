package com.coforge.hsbcdma.revenuemargin.dto;

public class ColumnSchema {

    private Integer columnNumber;

    private String columnName;

    private String sourceName;

    private String expectedDataType;

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(
            Integer columnNumber) {

        this.columnNumber =
                columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(
            String columnName) {

        this.columnName =
                columnName;
    }
    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(
            String sourceName) {

        this.sourceName =
                sourceName;
    }

    public String getExpectedDataType() {
        return expectedDataType;
    }

    public void setExpectedDataType(
            String expectedDataType) {

        this.expectedDataType =
                expectedDataType;
    }
}
