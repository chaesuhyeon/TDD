package com.tdd.chapter03;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExpiryDateCalculatorTest {

    @Test
    void 만원_납부하면_한달_뒤가_만료일이_됨() {

        // 예제 1
        assertExpiryDate(PayData.builder()
                .billingDate(LocalDate.of(2019, 3, 1))
                .payAmount(10_000)
                .build(),
                LocalDate.of(2019, 4,1));

        // 예제 2
        assertExpiryDate(PayData.builder()
                        .billingDate(LocalDate.of(2019, 5, 5))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 6, 5));
    }

    /**
     * 예외 상황 1
     * 납부일 2019-01-31 --> 만료일 2019-02-28
     * 납부일 2019-05-31 --> 만료일 2019-06-30
     * 납부일 2020-01-31 --> 만료일 2020-02-29
     * billingDate.plusMonths(1) -> 계산 알아서 잘 해줌
     */
    @Test
    void 납부일과_한달_뒤_일자가_같지_않음() {

        // 예제 1
        assertExpiryDate(PayData.builder()
                .billingDate(LocalDate.of(2019, 1, 31))
                .payAmount(10_000)
                .build(),
                LocalDate.of(2019, 2,28));

        // 예제 2
        assertExpiryDate(PayData.builder()
                        .billingDate(LocalDate.of(2019, 5, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2019, 6,30));

        // 예제 3
        assertExpiryDate(PayData.builder()
                        .billingDate(LocalDate.of(2020, 1, 31))
                        .payAmount(10_000)
                        .build(),
                LocalDate.of(2020, 2,29));
    }


    /**
     * 예외 상황 2 (1개월씩 요금 지불하는 상황)
     * 첫 납부일 2019-01-31 --> 만료일 2019-02-28에 1만원 납부 --> 다음 만료일 2019-03-31
     * 첫 납부일 2019-01-30 --> 만료일 2019-02-28에 1만원 납부 --> 다음 만료일 2019-03-30
     * 첫 납부일 2019-05-31 --> 만료일 2019-06-30에 1만원 납부 --> 다음 만료일 2019-07-31
     */
    @Test
    void 첫_납부일과_만료일_일자가_다를때_만원_납부(){

        // 예제 1
        PayData payData = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1,31))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(10_000)
                .build();
        assertExpiryDate(payData, LocalDate.of(2019, 3, 31));

        // 예제 2
        PayData payData2 = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1,30))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(10_000)
                .build();
        assertExpiryDate(payData2, LocalDate.of(2019, 3, 30));

    }

    @Test
    void 이만원_이상_납부하면_비례해서_만료일_계산() {

        // 2만원 납부
        assertExpiryDate(PayData.builder()
                .billingDate(LocalDate.of(2019, 3, 1))
                .payAmount(20_000)
                        .build(),
                LocalDate.of(2019,5,1));

        // 3만원 납부
        assertExpiryDate(PayData.builder()
                        .billingDate(LocalDate.of(2019, 3, 1))
                        .payAmount(30_000)
                        .build(),
                LocalDate.of(2019,6,1));
    }

    /**
     * 예외 상황 3 (2개월 이상씩 요금 지불하는 상황)
     * 첫 납부일 2019-01-31 --> 만료일 2019-02-28에 2만원 납부 --> 다음 만료일 2019-04-30 (31일도 아니고 28일도 아닌 30일)
     */
    @Test
    void 첫_납부일과_만료일_일자가_다를때_이만원_이상_납부() {

        // 예제 1
        PayData payData = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1,31))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(20_000)
                .build();
        assertExpiryDate(payData, LocalDate.of(2019, 4, 30));

        // 예제 2
        PayData payData2 = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 1,31))
                .billingDate(LocalDate.of(2019, 2, 28))
                .payAmount(40_000)
                .build();
        assertExpiryDate(payData2, LocalDate.of(2019, 6, 30));

        // 예제 3
        PayData payData3 = PayData.builder()
                .firstBillingDate(LocalDate.of(2019, 3,31))
                .billingDate(LocalDate.of(2019, 4, 30))
                .payAmount(30_000)
                .build();
        assertExpiryDate(payData3, LocalDate.of(2019, 7, 31));
    }

    @Test
    void 십만원을_납부하면_1년_제공() {

        // 예제 1
        assertExpiryDate(PayData.builder()
                        .billingDate(LocalDate.of(2019, 1, 28))
                        .payAmount(100_000)
                        .build(),
                LocalDate.of(2020,1,28));
    }

    /**
     * 중복 로직
     */
    private void assertExpiryDate(PayData payData, LocalDate expectedExpiryDate) {

        ExpiryDateCalculator cal = new ExpiryDateCalculator();
        LocalDate realExpiryDate = cal.calculateExpiryDate(payData);
        assertEquals(expectedExpiryDate, realExpiryDate);
    }
}
