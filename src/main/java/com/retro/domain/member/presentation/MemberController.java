package com.retro.domain.member.presentation;

import com.retro.domain.member.application.MemberService;
import com.retro.domain.member.application.dto.request.MemberAgreeTermsRequest;
import com.retro.domain.member.domain.entity.Member;
import com.retro.global.common.dto.ApiResponse;
import com.retro.global.common.jwt.JwtToken;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
  private final MemberService memberService;




}
