package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.EsVideo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EsVideoService {
	//删除
	void removeEsVideo(Long id);
	//更新
	EsVideo saveOrUpdateEsVideo(EsVideo esVideo);
	
	//根据id获取EsVideo
	EsVideo getEsVideoByVideoId(Long videoId);
 

	//最新Video列表，分页
	Page<EsVideo> listNewestEsVideo(String keyword, Pageable pageable);
 

	//最热博客列表，分页
	Page<EsVideo> listHotestEsVideo(String keyword, Pageable pageable);
	

	// 博客列表，分页
	Page<EsVideo> listEsVideo(Pageable pageable);

	//最新前4
	List<EsVideo> listTop4NewestEsVideo();

	//最热前4
	List<EsVideo> listTop4HotestEsVideo();

	//最新Video列表，依据标签分类
	Page<EsVideo> classNewestEsVideo(String keyword, Pageable pageable);


	//最热博客列表，依据标签分类
	Page<EsVideo> classHotestEsVideo(String keyword, Pageable pageable);

}
