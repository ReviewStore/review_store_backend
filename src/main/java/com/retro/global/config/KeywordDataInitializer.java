package com.retro.global.config;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.retro.domain.retro.domain.entity.Keyword;
import com.retro.domain.retro.domain.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class KeywordDataInitializer implements ApplicationRunner {

  private final KeywordRepository keywordRepository;

  @Override
  public void run(ApplicationArguments args) {
    List<Keyword> keywords = keywordRepository.findAll();

    if (CollectionUtils.isNotEmpty(keywords)) {
      return;
    }

    List<Keyword> newKeywords = new ArrayList<>();
    Set<String> duplicateCheck = new HashSet<>();

    // 데이터 추가를 위한 헬퍼 메소드 (중복 체크 포함)
    addKeyword(newKeywords, duplicateCheck, "기획/경영",
        "기획, PM, PO, Product Manager, Product Owner, 서비스기획, 프로덕트매니저, 프로덕트 오너, 게임기획, Game Planner, 레벨디자인, 경리, 회계, 장부, 더존, ERP, 임원, 정산, 비용처리, 경영지원, 지원, 관리, 총무, General Affairs, GA, 비서, 임원보좌, Secretary, 수행기사, 리셉션, 사업기획, 전략, 신규사업, 사업전략, Business Planning, 사업개발, BD, Business Development, 신사업, 제휴, 상품, Product Planner, 제품기획, 런칭, 웹기획, 앱기획, 스토리보드, 운영, 오퍼레이터, Ops, 매니징, 서비스운영, 경영전략, Strategy, 전시, 박람회, 큐레이터, 행사, Exhibition, 컨설팅, 경영컨설턴트, 전략컨설팅, 자문, Consultant, 이벤트, Event, 프로모션, 컨벤션, PCO");

    addKeyword(newKeywords, duplicateCheck, "교육",
        "교육, HRD, 교육담당, 학사관리, 교직, 커리큘럼, 교사, 교구, 유치원, 보육교사, 유아교육, 강사, 교수, 전문강사, 학습지, 방문교사, 과외, 멘토, 튜터");

    addKeyword(newKeywords, duplicateCheck, "개발/데이터",
        "데이터, BI, Business Intelligence, Tableau, 시각화, 지표, CTO, 기술이사, 최고기술책임자, Tech Lead, iOS, Swift, Objective-C, 모바일, QA, 품질관리, 테스트, QC, 소프트웨어 테스트, 게임개발, 서버, 클라이언트, Unity, 언리얼, Unreal, 기술지원, 인프라, 엔지니어, 유지보수, 솔루션, 보안, 네트워크, 서버운영, 정보보안, 해킹, Security, Infra, 데이터 분석가, Data Analyst, DA, 통계, SQL, 파이썬, R, 데이터 사이언티스트, Data Scientist, 머신러닝, AI, 모델링, 데이터 엔지니어, DE, Data Engineer, ETL, 파이프라인, 빅데이터, 머신러닝 엔지니어, ML, 딥러닝, AI 알고리즘, 백엔드, Backend, Java, Spring, Node.js, Python, Go, 프레임워크, 하둡, Spark, 분산처리, 소프트웨어, SW, 응용프로그램, C, C++, C#, Windows, 임베디드, 안드로이드, Android, Kotlin, Java, 웹개발, 웹, 퍼블리셔, Full-stack, 웹퍼블리셔, HTML, CSS, 퍼블리싱, 코딩, 퍼블리싱, 클라우드, Cloud, AWS, Azure, GCP, Devops, 엔지니어, 프론트엔드, Frontend, React, Vue, Angular, Javascript, JS, 하드웨어, HW, 반도체, 회로설계, 펌웨어, 임베디드, 블록체인, 스마트컨트랙트, 이더리움, 코인, 암호화, 보안, 보안전문가, 보안관제, 해킹, 침해사고");

    addKeyword(newKeywords, duplicateCheck, "금융/재무",
        "금융, 세무, 세무사, Tax, 세무회계, 세금, 자금, RM, 심사, 외환, 리서치, Analyst, 투자, 증권, 분석, 은행, 창구, 여신, 수신, 여수신, 자산운용, 운용, 투자운용, 자금운용, 투자자문, 재무, 재무회계, Finance, 회계, FP&A, 회계감사, IR, 보험, 언더라이팅, 보상, 보험영업, 손해사정, 투자유치, 자산관리, 자금조달, 공인회계사, CPA, KICPA, AICPA, 관세사, 세무사");

    addKeyword(newKeywords, duplicateCheck, "디자인",
        "디자인, UI, UX, 프로토타이핑, 그래픽, GUI, 웹디자인, 모바일디자인, 브랜딩, 로고, CI, BI, Brand Identity, BX, 건축, 설계, 인테리어, CAD, 도면, 광고, 카피라이팅, 포스터, 배너, 제품디자인, 산업디자인, 3D, 렌더링, 의상, 패션, VMD, 스타일리스트, 영상디자인, 영상편집, 모션그래픽, Motion, 애프터이펙트, 3D 디자인, 모델링, 캐릭터, 애니메이션, 게임디자인, 원화, UX디자인, 서비스디자인, UX리서치, UI/UX");

    addKeyword(newKeywords, duplicateCheck, "마케팅/광고/조사",
        "마케팅, CRM, 퍼포먼스, 마케터, 그로스, 매체, 홍보, PR, 광고, 광고기획, 광고대행, AE, Account Executive, 카피라이터, 바이럴, SNS, 콘텐츠마케팅, 인플루언서, 브랜드마케팅, 브랜드매니저, BM, 시장조사, 설문, 리서치, 여론조사, 디지털마케팅, 온라인마케팅, 키워드광고, 검색광고, SA, DA, 광고리서치, 미디어바잉");

    addKeyword(newKeywords, duplicateCheck, "미디어",
        "미디어, 방송, 영상, 연출, 제작, Producer, PD, 작가, 뉴스, 보도, 취재, 잡지, 영상편집, 편집, 촬영, 카메라, 사진, 포토그래퍼, 스튜디오, 조명, 음향, 사운드, 오디오, 믹싱, 크리에이티브, 기획, 기획자, 콘텐츠에디터, 에디터, 유튜브, 채널운영, 스트리밍");

    addKeyword(newKeywords, duplicateCheck, "법률/법무",
        "법무, 사내변호사, 법무팀, 변호사, 변리사, 노무사, 컴플라이언스, 준법감시, 지적재산권, 특허, 상표");

    addKeyword(newKeywords, duplicateCheck, "생산/제조",
        "생산, 품질, 제조, 공정, 공정설계, 품질관리, 품질보증, 공장, 생산관리, 생산직, 기능직, 조립, 설비, 기구설계, 기계설계, 공정기술, 안전, 환경, 보건, SHE, 유해물질");

    addKeyword(newKeywords, duplicateCheck, "서비스",
        "서비스, 매장관리, 점장, 홀서비스, 서빙, 캐셔, 안내, 발렛, 고객상담, CS, 콜센터, 텔레마케팅, 해피콜, AS, 수리, 사후관리, 비서, 리셉션, 보안, 경비, 경호, 안내데스크, 요리, 조리, 제빵, 바리스타, 주방, 홀서빙, 영양사");

    addKeyword(newKeywords, duplicateCheck, "영업",
        "영업, 판매, 영업기획, 영업관리, 해외영업, 수출입, 국내영업, 법인영업, B2B, 기술영업, 솔루션영업, 자동차영업, 제약영업, 보험영업, 아웃바운드, 영업지원");

    addKeyword(newKeywords, duplicateCheck, "유통/물류",
        "유통, 물류, 창고, SCM, 물류센터, 배송, 택배, 운송, 화물, 지입, 구매, 자재, 구매관리, 소싱, 머천다이저, MD, 상품기획, VMD");

    addKeyword(newKeywords, duplicateCheck, "의료/바이오",
        "의료, 간호, 간호사, 간호조무사, 물리치료사, 작업치료사, 임상병리사, 방사선사, 치과위생사, 치기공사, 약사, 한약사, 제약, 바이오, 임상, R&D, 연구원, 의사, 전문의, 한의사, 수의사, 병원코디네이터, 의료행정");

    addKeyword(newKeywords, duplicateCheck, "인사/총무",
        "인사, HR, HRM, 채용, 리크루팅, 교육, HRD, 보상, 평가, 급여, 노무, 노사관계, 조직문화, 총무, 관재, 자산관리, 복리후생, 행사기획");

    keywordRepository.saveAll(newKeywords);
  }

  /**
   * 키워드를 콤마(,) 기준으로 분리하여 리스트에 추가하는 헬퍼 메소드
   *
   * @param list        저장할 키워드 리스트
   * @param check       중복 체크를 위한 Set
   * @param category    카테고리명
   * @param keywordsStr 콤마로 연결된 키워드 문자열
   */
  private void addKeyword(List<Keyword> list, Set<String> check, String category,
      String keywordsStr) {
    String[] split = keywordsStr.split(",");
    for (String k : split) {
      String trimmed = k.trim();
      if (!trimmed.isEmpty() && !check.contains(trimmed)) {
        list.add(Keyword.of(trimmed, category));
        check.add(trimmed);
      }
    }
  }
}