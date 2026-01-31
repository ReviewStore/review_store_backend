package com.retro.domain.retro.infrastructure;

import com.retro.domain.retro.domain.entity.Retro;
import com.retro.domain.retro.domain.repository.RetroRepository;
import com.retro.domain.retro.infrastructure.jpa.RetroJPARepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RetroRepositoryImpl implements RetroRepository {

  private final RetroJPARepository retroJPARepository;

  @Override
  public Retro save(Retro retro) {
    return retroJPARepository.save(retro);
  }

  @Override
  public Optional<Retro> findById(Long retroId) {
    return retroJPARepository.findById(retroId);
  }

  @Override
  public void delete(Retro retro) {
    retroJPARepository.delete(retro);
  }
}
