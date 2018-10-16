package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.EsTalk;
import jit.hf.agriculture.domain.Talk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TalkRepository extends JpaRepository<Talk,Long> {

    Talk findOneById(Long id);
    Page<Talk> findAll(Pageable pageable);
    //Page<Talk> findAllBySort(Sort sort, Pageable pageable);
    List<EsTalk> findDistinctEsTalkByThemeContainingOrTagsContaining(String theme, String tags);
    Page<EsTalk> findDistinctEsTalkByThemeContainingOrTagsContaining(String theme, String tags, Pageable pageable);
    Integer countDistinctByTagsContaining(String tags);
}
