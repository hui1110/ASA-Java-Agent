package com.microsoft.azure.spring.java.accelerator.model;

import java.util.Date;

public class DumpSetting {

    private Date lastModifiedDate;

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "DumpSetting{" +
            "lastModifiedDate=" + lastModifiedDate +
            '}';
    }
}
