package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: zj
 */
public interface VideoRepository extends JpaRepository<Video,Long> {
    public Video findOneById(Long id);
    public Video findOneByTitle(String title);
    public List<Video> findOneByAuthor(String author);
    Page<Video> findAllByExaminationTrue(Pageable pageable);
    Page<Video> findAll(Pageable pageable);
    List<Video> findDistinctByTitleContainingOrTagsContaining(String title,String tags);
    Page<Video> findDistinctByTitleContainingOrTagsContaining(String title,String tags,Pageable pageable);
    Page<Video> findAllByExaminationFalse(Pageable pageable);
    Integer countDistinctByTagsContaining(String tags);
}
