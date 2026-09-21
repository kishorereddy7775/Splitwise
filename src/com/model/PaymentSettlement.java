package com.model;

import java.util.Map;

public record PaymentSettlement(Map<User,Map<User,Double>> payments) {

}
