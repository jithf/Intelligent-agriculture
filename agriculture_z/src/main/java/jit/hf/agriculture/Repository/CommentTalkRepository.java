package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.CommentTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentTalkRepository extends JpaRepository<CommentTalk,Long>{
    CommentTalk findOneById(Long id);
}
