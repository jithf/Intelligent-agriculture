package jit.hf.agriculture.Service;

import jit.hf.agriculture.domain.Doc;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Author: zj
 */
public interface DocService {
    //新增、编辑、保存文档
    Doc saveOrUpdateDoc(Doc doc);
    //根据用户名获取用户
    Doc getDocByTitle(String title);

    Doc getDocById(Long id);

    List<Doc> getDocPage(Pageable pageable);

    List<Doc> searchDoc(String key);

    List<Doc> searchDocPage(String key,Pageable pageable);
}
