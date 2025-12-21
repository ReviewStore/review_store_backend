package com.retro.global.common.jwt;

import com.retro.domain.auth.application.dto.response.ApplePublicKeyDto.ApplePublicKey;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.LocatorAdapter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class MyKeyLocator extends LocatorAdapter<java.security.Key> {
  private List<ApplePublicKey> publicKeyList;

  public MyKeyLocator(List<ApplePublicKey> publicKeyList) {
    this.publicKeyList = publicKeyList;
  }

  @Override
  protected Key locate(JwsHeader header) {
    System.out.println(header);
    Optional<ApplePublicKey> optionalPublicKey = publicKeyList.stream().filter(applePublicKey ->
        applePublicKey.getKid().equals(header.getKeyId())
    ).findFirst();

    if (optionalPublicKey.isEmpty()) {
      throw new RuntimeException(
          "일치하는 public key 가 없습니다. " + header.getKeyId() + ", " + publicKeyList.toString());
    }

    ApplePublicKey publicKey = optionalPublicKey.get();

    BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getN()));
    BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(publicKey.getE()));

    try {
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      KeySpec keySpec = new RSAPublicKeySpec(n, e);

      return keyFactory.generatePublic(keySpec);
    } catch (Exception error) {
      throw new RuntimeException("[애플 로그인] public key 추출 실패: " + error.getMessage(), error);
    }

  }
}
