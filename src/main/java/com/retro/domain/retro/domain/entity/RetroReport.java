package com.retro.domain.retro.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "retro_reports",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_retro_report_retro_reporter",
        columnNames = {"retro_id", "reporter_member_id"}
    )
)
public class RetroReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long retroReportId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "retro_id", nullable = false)
  private Retro retro;

  @Column(name = "reporter_member_id", nullable = false)
  private Long reporterMemberId;

  private RetroReport(Retro retro, Long reporterMemberId) {
    this.retro = retro;
    this.reporterMemberId = reporterMemberId;
  }

  public static RetroReport of(Retro retro, Long reporterMemberId) {
    return new RetroReport(retro, reporterMemberId);
  }

  public void addRetro(Retro retro) {
    this.retro = retro;
  }
}