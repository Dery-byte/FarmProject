package com.alibou.book.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    PAYMENT_CONFIRMATION("payment_success"),
    RESET_PASSWORD("reset_password");
    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}
