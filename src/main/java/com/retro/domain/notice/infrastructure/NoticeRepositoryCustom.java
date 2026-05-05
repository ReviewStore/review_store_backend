package com.retro.domain.notice.infrastructure;

import com.retro.domain.notice.domain.entity.Notice;
import java.util.List;

public interface NoticeRepositoryCustom {

  List<Notice> findNoticesWithCursor(Long cursorId, int size);

}

