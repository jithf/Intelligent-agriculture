package jit.hf.agriculture.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import jit.hf.agriculture.domain.EsVideo;

import java.util.List;

/**
 * Blog 存储库.
 * 
 * @since 1.0.0 2017年3月12日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
public interface EsVideoRepository extends ElasticsearchRepository<EsVideo, Long> {

    //分页模糊查询(去重)
    Page<EsVideo> findDistinctEsVideoByTitleContainingOrDescriptionContainingOrTagsContaining(String title, String description, String tags, Pageable pageable);

    EsVideo findByVideoId(Long VideoId);

    Page<EsVideo> findDistinctEsVideoByTagsContaining(String tag,Pageable pageable);
}


