package com.athome.zubaliy.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.Data;

/**
 * @author Andriy Zubaliy
 */

@DatabaseTable(tableName = "ActivityLog")
@Data
public class ActivityLog {

    @DatabaseField(generatedId = true)
    // exclude this field from serialisation
    private transient Integer id;
    @DatabaseField
    private Date connected;
    @DatabaseField
    private Date disconnected;
    @DatabaseField
    private Integer difference;

    public ActivityLog() {
        // ORMLite needs a no-arg constructor
    }

    /**
     * Constructor with connected time
     *
     * @param connected the
     */
    public ActivityLog(Date connected) {
        this.connected = connected;
    }

}
