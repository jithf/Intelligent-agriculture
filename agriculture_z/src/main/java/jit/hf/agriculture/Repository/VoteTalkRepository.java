package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.VoteTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteTalkRepository extends JpaRepository<VoteTalk,Long>{
    VoteTalk findOneByAuthor(String author);
}
