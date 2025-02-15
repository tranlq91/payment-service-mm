package org.example.entity;

import org.example.enums.BillingState;

public class BillingEntity extends BaseEntity {
    private String type;
    private String dueDate;
    private BillingState state;

    public BillingEntity() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public BillingState getState() {
        return state;
    }

    public void setState(BillingState state) {
        this.state = state;
    }

    public String getProvider() {
        return super.getProvider();
    }

    public void setProvider(String provider) {
        super.setProvider(provider);
    }

}