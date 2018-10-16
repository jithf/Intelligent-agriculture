package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.DocRepository;

import jit.hf.agriculture.domain.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: zj
 */

@Service
public class DocServiceImpl implements DocService{
    @Autowired
    private DocRepository docRepository;

    @Override
    public Doc saveOrUpdateDoc(Doc doc) {
        return docRepository.save(doc);
    }

    @Override
    public Doc getDocByTitle(String title) {
        return docRepository.findOneByTitle( title );
    }

    @Override
    public Doc getDocById(Long id) {
        return docRepository.findOneById( id );
    }

    @Override
    public List<Doc> getDocPage(Pageable pageable) {
        return docRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Doc> searchDoc(String key) {
        return docRepository.findDistinctByTitleContainingOrDescriptionContaining(key,key);
    }

    @Override
    public List<Doc> searchDocPage(String key, Pageable pageable) {
        return docRepository.findDistinctByTitleContainingOrDescriptionContaining(key,key,pageable).getContent();
    }

}
