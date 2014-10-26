package com.athome.zubaliy.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.Data;

/**
 * Created by zubaliy on 26/10/14.
 */

@DatabaseTable(tableName = "ActivityLog")
@Data
public class ActivityLog {

    @DatabaseField(generatedId = true)
    private Integer id;
    //    @DatabaseField(dataType = DataType.DATE_STRING)
    @DatabaseField
    private Date connected;
    @DatabaseField
    private Date disconnected;
    @DatabaseField
    private Integer difference;


    public ActivityLog() {
        // ORMLite needs a no-arg constructor
    }

}
