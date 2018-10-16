package jit.hf.agriculture.Repository;


import jit.hf.agriculture.domain.Doc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: zj
 * Description:文件类资源
 */
public interface DocRepository extends JpaRepository<Doc,Long> {
    public Doc findOneById(Long id);
    public Doc findOneByTitle(String title);
    public Page<Doc> findAll(Pageable pageable);
    public List<Doc> findDistinctByTitleContainingOrDescriptionContaining(String title,String description);
    public Page<Doc> findDistinctByTitleContainingOrDescriptionContaining(String title,String description,Pageable pageable);
    Integer countDistinctByTitleContaining(String key);
}
