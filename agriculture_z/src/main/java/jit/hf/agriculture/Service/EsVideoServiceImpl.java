package jit.hf.agriculture.Service;

import java.util.List;
import jit.hf.agriculture.Repository.EsVideoRepository;
import jit.hf.agriculture.domain.EsVideo;
import org.elasticsearch.search.SearchParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;


/**
 * EsBlog 服务.
 * 
 * @since 1.0.0 2017年4月12日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class EsVideoServiceImpl implements EsVideoService {
	@Autowired
	private EsVideoRepository esVideoRepository;
	
	private static final Pageable TOP_4_PAGEABLE = new PageRequest(0, 4);
	private static final String EMPTY_KEYWORD = "";

	//删除
	@Override
	public void removeEsVideo(Long id) {
		esVideoRepository.delete(id);
	}

	//更新
	@Override
	public EsVideo saveOrUpdateEsVideo(EsVideo esVideo) {
		return esVideoRepository.save(esVideo);
	}

	//根据id获取EsVideo
	@Override
	public EsVideo getEsVideoByVideoId(Long videoId) {
		return esVideoRepository.findByVideoId(videoId);
	}

	//最新Video列表，分页
	@Override
	public Page<EsVideo> listNewestEsVideo(String keyword, Pageable pageable) throws SearchParseException {
		Page<EsVideo> pages = null;
		Sort sort = new Sort(Direction.DESC,"uptime");
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		pages = esVideoRepository.findDistinctEsVideoByTitleContainingOrDescriptionContainingOrTagsContaining(keyword,keyword,keyword,pageable);
 
		return pages;
	}

	//最热博客列表，分页
	@Override
	public Page<EsVideo> listHotestEsVideo(String keyword, Pageable pageable) throws SearchParseException{
 
		Sort sort = new Sort(Direction.DESC,"clicks");
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		return esVideoRepository.findDistinctEsVideoByTitleContainingOrDescriptionContainingOrTagsContaining( keyword, keyword, keyword, pageable);
	}

	// 博客列表，分页
	@Override
	public Page<EsVideo> listEsVideo(Pageable pageable) {
		return esVideoRepository.findAll(pageable);
	}

	//最新前4
	@Override
	public List<EsVideo> listTop4NewestEsVideo() {
		Page<EsVideo> page = this.listNewestEsVideo(EMPTY_KEYWORD, TOP_4_PAGEABLE);
		return page.getContent();
	}

	//最热前4
	@Override
	public List<EsVideo> listTop4HotestEsVideo() {
		Page<EsVideo> page = this.listHotestEsVideo(EMPTY_KEYWORD, TOP_4_PAGEABLE);
		return page.getContent();
	}

	//最新Video列表，依据标签分类
	@Override
	public Page<EsVideo> classNewestEsVideo(String keyword, Pageable pageable) throws SearchParseException {
		Page<EsVideo> pages = null;
		Sort sort = new Sort(Direction.DESC,"uptime");
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}

		pages = esVideoRepository.findDistinctEsVideoByTagsContaining(keyword,pageable);

		return pages;
	}

	//最热video列表，依据标签分类
	@Override
	public Page<EsVideo> classHotestEsVideo(String keyword, Pageable pageable) throws SearchParseException{

		Sort sort = new Sort(Direction.DESC,"clicks");
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}

		return esVideoRepository.findDistinctEsVideoByTagsContaining(keyword,pageable);
	}

}
