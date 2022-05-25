package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    public void saveMember() throws Exception {
        //given
        Member member = createMember();
        //when
        Member savedMember = memberService.saveMember(member);
        //then
        assertThat(member.getAddress()).isEqualTo(savedMember.getAddress());
        assertThat(member.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(member.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(member.getPassword()).isEqualTo(savedMember.getPassword());
        assertThat(member.getRole()).isEqualTo(savedMember.getRole());
    }

    @Test

    public void duplicateSaveTest() throws Exception {
        //given
        Member member1 = createMember();
        Member member2 = createMember();
        //when
        memberService.saveMember(member1);
        //then
        Throwable e = assertThrows(IllegalStateException.class, () -> {
            memberService.saveMember(member2);
        });
    }


}