package com.retro.domain.member.application;

import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.application.dto.response.OAuth2AppleMemberInfo;
import com.retro.domain.member.application.exception.MemberNotRegisteredException;
import com.retro.domain.member.domain.MemberRepository;
import com.retro.domain.member.domain.entity.Member;
import com.retro.domain.member.domain.entity.Term;
import com.retro.global.common.exception.ErrorCode;
import com.retro.global.common.jwt.JwtProvider;
import com.retro.global.common.jwt.JwtToken;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

}
