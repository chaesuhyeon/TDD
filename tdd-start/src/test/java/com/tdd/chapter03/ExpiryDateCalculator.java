package com.tdd.chapter03;

import java.time.LocalDate;

public class ExpiryDateCalculator {
    public LocalDate calculateExpiryDate(PayData payData) {

        int addedMonths = 1; // 만료일을 계산할 때 추가할 개월 수

        if (payData.getFirstBillingDate() != null) {
            LocalDate candidateExp = payData.getBillingDate().plusMonths(addedMonths); // 후보 만료일 구함
            if (payData.getFirstBillingDate().getDayOfMonth() != candidateExp.getDayOfMonth()) { // 첫 납부일의 일자와 후보 만료일의 일자가 다르다면 ex) 1월 "31"일 -> 2월 "28"일 경우 
                return candidateExp.withDayOfMonth( // 첫 납부일의 일자를 후보 만료일의 일자로 사용
                        payData.getFirstBillingDate().getDayOfMonth()
                );
            }
        }
        return payData.getBillingDate().plusMonths(addedMonths);
    }
/*    public LocalDate calculateExpiryDate(LocalDate billingDate, int payAmount) {
        return billingDate.plusMonths(1);
    }*/
}
