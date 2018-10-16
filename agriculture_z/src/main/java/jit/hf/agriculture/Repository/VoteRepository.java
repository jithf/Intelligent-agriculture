package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:36 18-4-18
 **/
public interface VoteRepository extends JpaRepository<Vote, Long> {

}
