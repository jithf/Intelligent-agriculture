package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.VoteSecondTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteSecondTalkRepository extends JpaRepository<VoteSecondTalk,Long> {
    VoteSecondTalk findOneByAuthor(String author);
}
