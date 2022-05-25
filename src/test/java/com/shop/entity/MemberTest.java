package com.shop.entity;

import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @WithMockUser(username = "gildong", roles = "USER") //시큐리티에서 로그인 상태로 가정하고 테스트
    public void auditing() throws Exception {
        //given
        Member member = new Member();
        memberRepository.save(member);

        em.flush();
        em.clear();
        //when
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(EntityNotFoundException::new);

        //then
        System.out.println("findMember.getCreateTime() = " + findMember.getCreateTime());
        System.out.println("findMember.getUpdateTime() = " + findMember.getUpdateTime());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getModifiedBy() = " + findMember.getModifiedBy());
    }
}