package edu.upb.barber.integration;

public class StereumCreateChargeRequest {
    private String country;
    private String amount;
    private String currency;
    private String network;
    private String charge_reason;
    private String reservation_validity_time;
    private Customer customer;

    public static class Customer {
        private String name;
        private String lastname;
        private String document_number;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getDocument_number() {
            return document_number;
        }

        public void setDocument_number(String document_number) {
            this.document_number = document_number;
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCharge_reason() {
        return charge_reason;
    }

    public void setCharge_reason(String charge_reason) {
        this.charge_reason = charge_reason;
    }

    public String getReservation_validity_time() {
        return reservation_validity_time;
    }

    public void setReservation_validity_time(String reservation_validity_time) {
        this.reservation_validity_time = reservation_validity_time;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
