package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.CommentSecondTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentSecondTalkRepository extends JpaRepository<CommentSecondTalk,Long> {
    CommentSecondTalk findOneById(Long id);
}
