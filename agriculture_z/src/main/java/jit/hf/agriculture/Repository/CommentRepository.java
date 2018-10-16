package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午6:12 18-4-17
 **/
public interface CommentRepository extends JpaRepository<Comment,Long> {
    public Comment findOneById(Long id);
    public Integer countByVideoId(Long id);
}
