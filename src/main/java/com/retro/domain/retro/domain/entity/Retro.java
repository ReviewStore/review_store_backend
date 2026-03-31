package com.retro.domain.retro.domain.entity;

import com.retro.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "retros")
public class Retro extends BaseEntity {

  private static final int BLIND_REPORT_THRESHOLD = 2;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long retroId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false, length = 100)
  private String companyName;

  @Column(nullable = false, length = 100)
  private String position;

  @Column(nullable = false)
  private LocalDate interviewDate;

  @Column(nullable = false, length = 20)
  private String interviewRound;

  private String interviewTags;

  @Column(columnDefinition = "TEXT")
  private String keepText;

  @Column(columnDefinition = "TEXT")
  private String problemText;

  @Column(columnDefinition = "TEXT")
  private String tryText;

  private String summary;

  @Column(nullable = false)
  private int reportCount;

  @Column(nullable = false)
  private boolean blinded;

  @OneToMany(mappedBy = "retro", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RetroReport> reports = new ArrayList<>();

  @OneToMany(mappedBy = "retro", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<InterviewQuestion> questions = new ArrayList<>();

  @Builder(access = AccessLevel.PRIVATE)
  private Retro(Long memberId, String companyName, String position, LocalDate interviewDate,
      String interviewRound, String interviewTags, String keepText,
      String problemText, String tryText, String summary) {
    this.memberId = memberId;
    this.companyName = companyName;
    this.position = position;
    this.interviewDate = interviewDate;
    this.interviewRound = interviewRound;
    this.interviewTags = interviewTags;
    this.keepText = keepText;
    this.problemText = problemText;
    this.tryText = tryText;
    this.summary = summary;
    this.reportCount = 0;
    this.blinded = false;
  }

  public static Retro of(Long memberId, String companyName, String position,
      LocalDate interviewDate,
      String interviewRound, String interviewTags, String keepText,
      String problemText, String tryText, String summary) {
    return Retro.builder()
        .memberId(memberId)
        .companyName(companyName)
        .position(position)
        .interviewDate(interviewDate)
        .interviewRound(interviewRound)
        .interviewTags(interviewTags)
        .keepText(keepText)
        .problemText(problemText)
        .tryText(tryText)
        .summary(summary)
        .build();
  }

  public void addQuestions(List<InterviewQuestion> interviewQuestions) {
    interviewQuestions.forEach(this::addQuestion);
  }

  public void addQuestion(InterviewQuestion question) {
    this.questions.add(question);
    question.assignToRetro(this);
  }

  public boolean isCreatedByViewer(Long creatorId, Long viewerId) {
    return creatorId.equals(viewerId);
  }

  public void markAsWithdrawnMember(Long deletedMemberId) {
    this.memberId = deletedMemberId;
  }

  public boolean isBlindedRetro() {
    return this.blinded;
  }


  public boolean report() {
    this.reportCount++;
    if (shouldBeBlinded()) {
      this.blinded = true;
      return true;
    }
    return false;
  }

  private boolean shouldBeBlinded() {
    return !this.blinded && this.reportCount >= BLIND_REPORT_THRESHOLD;
  }

  public void addReport(RetroReport report) {
    this.reports.add(report);
    report.addRetro(this);
  }

  public boolean isOwnedBy(Long memberId) {
    return this.memberId.equals(memberId);
  }

  public void update(String companyName, String position, LocalDate interviewDate,
      String interviewRound, String interviewTags, String keepText,
      String problemText, String tryText, String summary) {
    this.companyName = companyName;
    this.position = position;
    this.interviewDate = interviewDate;
    this.interviewRound = interviewRound;
    this.interviewTags = interviewTags;
    this.keepText = keepText;
    this.problemText = problemText;
    this.tryText = tryText;
    this.summary = summary;
  }

  public void replaceQuestions(List<InterviewQuestion> newQuestions) {
    this.questions.clear();
    if (newQuestions != null) {
      newQuestions.forEach(this::addQuestion);
    }
  }
}