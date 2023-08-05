package com.tdd.chapter03;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * withDayOfMonth() : 해당 월의 날짜를 지정된 값으로 변경하는 기능 제공
 */
public class ExpiryDateCalculator {
    public LocalDate calculateExpiryDate(PayData payData) {

        // 10만원을 납부하면 1년 서비스를 제공하므로 addedMonths를 12로 해주고 아닐 경우에는 만원으로 나눈다.
        int addedMonths = payData.getPayAmount() == 100_000 ? 12 : payData.getPayAmount() / 10_000; // 만료일을 계산할 때 추가할 개월 수

        if (payData.getFirstBillingDate() != null) {
            return expiryDateUsingFirstBillingDate(payData, addedMonths);
        } else {
            return payData.getBillingDate().plusMonths(addedMonths);
        }
    }

    private LocalDate expiryDateUsingFirstBillingDate(PayData payData, int addedMonths) {
        LocalDate candidateExp = payData.getBillingDate().plusMonths(addedMonths); // 후보 만료일 구함
        final int dayOfFirstBilling = payData.getFirstBillingDate().getDayOfMonth(); // 첫 납부일의 일자

        if (dayOfFirstBilling != candidateExp.getDayOfMonth()) { // 첫 납부일의 일자와 후보 만료일의 일자가 다르다면 ex) 1월 "31"일 -> 2월 "28"일 경우 3월 "31"일로 맞춰주기 위함
            final int dayLenOfCandiMon = YearMonth.from(candidateExp).lengthOfMonth(); // 후보 만료일이 포함된 달의 마지막 날 ex) 후보 만료일이 4월이면 "30"일

            if (dayLenOfCandiMon < payData.getFirstBillingDate().getDayOfMonth()) { // 후보 만료일이 포함된 달의 마지막 날 < 첫 납부일의 일자 ex) 4월 "30"일 < 3월 "31"일
                return candidateExp.withDayOfMonth(dayLenOfCandiMon);
            }
            return candidateExp.withDayOfMonth(dayOfFirstBilling);

        } else {
            return candidateExp;
        }
    }
}
