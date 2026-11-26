package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.ColumnSchema;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class SchemaValidationService {

    public void printSchema(Row headerRow) {

        System.out.println("Schema:");

        headerRow.forEach(cell ->
                System.out.println(
                        cell.getColumnIndex()
                                + " -> "
                                + cell.toString()
                ));

    }

    public List<String> getHeaders(
            Row headerRow) {

        List<String> headers =
                new ArrayList<>();

        headerRow.forEach(cell ->
                headers.add(
                        cell.toString().trim()
                ));

        return headers;
    }

    public List<ColumnSchema> getSchema(
            Row headerRow) {

        List<ColumnSchema> schema =
                new ArrayList<>();

        headerRow.forEach(cell -> {

            ColumnSchema columnSchema =
                    new ColumnSchema();

            columnSchema.setColumnNumber(
                    cell.getColumnIndex()
            );

            columnSchema.setColumnName(
                    cell.toString().trim()
            );

            schema.add(columnSchema);

        });

        return schema;

    }

    public boolean validateMinimumSchema(
            List<String> headers) {

        return headers.contains("Resource ID")
                && headers.contains("Resource Name")
                && headers.contains("cTool ID");

    }
}