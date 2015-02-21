package com.athome.zubaliy.sqlite.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.Data;

/**
 * Entity.
 * <p/>
 * Difference is calculated when in setDisconnected()
 *
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

    public void setDisconnected(final Date disconnected) {
        this.disconnected = disconnected;
        this.difference = (int) (disconnected.getTime() - connected.getTime());
    }

}
