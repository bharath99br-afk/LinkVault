package com.linkvault.backend.bank.dto;

public class BankResponse {

    private Long id;
    private String name;

    public BankResponse() {
    }

    public BankResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}