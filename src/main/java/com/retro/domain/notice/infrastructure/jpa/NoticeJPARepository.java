package com.retro.domain.notice.infrastructure.jpa;

import com.retro.domain.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJPARepository extends JpaRepository<Notice, Long> {

}
