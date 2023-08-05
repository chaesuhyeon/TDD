package com.tdd.chapter07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegisterMockTest {
    private UserRegister userRegister;
    private WeakPasswordChecker mockPasswordChecker = Mockito.mock(WeakPasswordChecker.class);
    private MemoryUserRepository fakeRepository = new MemoryUserRepository();
    private EmailNotifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

    @BeforeEach
    void setUp() {
        userRegister = new UserRegister(mockPasswordChecker, fakeRepository, mockEmailNotifier);
    }

    @DisplayName("약한 암호면 가입 실패")
    @Test
    void weakPassword() {

        BDDMockito.given(mockPasswordChecker.checkPasswordWeak("pw"))
                .willReturn(true);
        assertThrows(WeakPasswordException.class, ()-> {
            userRegister.register("id", "pw", "email");
        });
    }

    @DisplayName("회원 가입 시 암호 검사 수행함")
    @Test
    void checkPassword() {
        userRegister.register("id", "pw", "email");
        BDDMockito.then(mockPasswordChecker) // 인자로 전달한 mockPasswordChecker 모의 객체의
                .should() // 특정 메서드가 호출됐는지 검증 하는데
                .checkPasswordWeak(BDDMockito.anyString()); // 임의의 String 타입의 인자를 이용해서 checkPasswordWeak() 메서드 호출 여부를 확인한다.
    }

    @DisplayName("이미 같은 ID가 존재하면 가입 실패")
    @Test
    void dupIdExist() {
        // 이미 같은 ID 존재하는 상황 만들기
        fakeRepository.save(new User("id", "pwd1", "email@email.com"));

        assertThrows(DupIdException.class, () -> {
            userRegister.register("id", "pw2", "email");
        });
    }

    @DisplayName("같은 ID가 없으면 가입 성공")
    @Test
    void noDupId_RegisterSuccess() {
        userRegister.register("id", "pw", "email");

        User savedUser = fakeRepository.findById("id"); // 가입 결과 확인
        assertEquals("id", savedUser.getId());
        assertEquals("email", savedUser.getEmail());
    }

    /**
     * ArgumentCaptor : 모의 객체 메서드를 호출할 때 전달한 객체를 담는 기능을 제공
     */
    @DisplayName("가입하면 메일을 전송함")
    @Test
    void whenRegisterThenSendEmail() {
        userRegister.register("id", "pw", "email@email.com");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        BDDMockito.then(mockEmailNotifier)
                        .should()
                        .sendRegisterEmail(captor.capture()); // 메서드를 호출할 때 전달한 인자가 ArgumentCaptor에 담긴다.

        String realEmail = captor.getValue(); // 보관한 인자 반환 받을 수 있음
        assertEquals("email@email.com", realEmail);
    }
}
