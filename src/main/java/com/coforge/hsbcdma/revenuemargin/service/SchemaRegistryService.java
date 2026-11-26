package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.dto.ColumnSchema;

import java.util.ArrayList;
import java.util.List;

public class SchemaRegistryService {

    public List<ColumnSchema> getVmDumpExpectedSchema() {

        List<ColumnSchema> schema =
                new ArrayList<>();

        schema.add(
                buildSchema(
                        0,
                        "Resource ID",
                        "VM Dump",
                        "String"
                )
        );

        schema.add(
                buildSchema(
                        1,
                        "Resource Name",
                        "VM Dump",
                        "String"
                )
        );

        schema.add(
                buildSchema(
                        2,
                        "cTool ID",
                        "VM Dump",
                        "String"
                )
        );

        return schema;

    }

    private ColumnSchema buildSchema(
            Integer columnNumber,
            String columnName,
            String sourceName,
            String expectedDataType) {

        ColumnSchema schema =
                new ColumnSchema();

        schema.setColumnNumber(
                columnNumber
        );

        schema.setColumnName(
                columnName
        );

        schema.setSourceName(
                sourceName
        );

        schema.setExpectedDataType(
                expectedDataType
        );

        return schema;

    }

}
