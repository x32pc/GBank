package com.x32pc.github.api;

import com.x32pc.github.GBank;

public class APIImplementation implements API {

    private final GBank gBank;

    public APIImplementation(GBank main) {
        this.gBank = main;
    }

    @Override
    public double getAmountCurrency(String playeruuid, String currency) {
        return gBank.currencyManager.getAmountCurrency(playeruuid, currency);
    }
}
