package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.EsTalk;
import jit.hf.agriculture.domain.Talk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Author: zj
 * Description:引用ElasticsearchRepository接口
 **/
public interface EsTalkRepository extends ElasticsearchRepository<EsTalk, Long> {
    //模糊查询(去重)
    List<EsTalk> findDistinctEsTalkByThemeContainingOrTagsContaining(String theme, String tags);
    EsTalk findOneById(Long id);
    Page<EsTalk> findDistinctEsTalkByThemeContainingOrTagsContaining(String theme, String tags, Pageable pageable);
}
