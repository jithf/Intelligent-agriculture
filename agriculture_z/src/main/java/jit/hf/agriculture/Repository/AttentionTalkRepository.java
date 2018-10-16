package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.AttentionTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttentionTalkRepository extends JpaRepository<AttentionTalk,Long> {
    AttentionTalk findOneByAuthor(String author);
}
