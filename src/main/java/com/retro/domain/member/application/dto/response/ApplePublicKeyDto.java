package com.retro.domain.member.application.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplePublicKeyDto {

  // 애플 응답의 "keys" 필드와 이름을 반드시 맞춰야 합니다.
  private List<ApplePublicKey> keys;

  @Getter
  @NoArgsConstructor
  public static class ApplePublicKey {
    private String kty;
    private String kid;
    private String use;
    private String alg;
    private String n;
    private String e;

    public ApplePublicKey(String kty, String kid, String use, String alg, String n, String e) {
      this.kty = kty;
      this.kid = kid;
      this.use = use;
      this.alg = alg;
      this.n = n;
      this.e = e;
    }
  }
}