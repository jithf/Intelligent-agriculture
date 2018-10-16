package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.Choice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午3:18 18-5-8
 **/
public interface ChoiceRepository extends JpaRepository<Choice,Long> {
     Choice findOneById(Long Id);
     List<Choice> findByUploadUsernameEquals(String uploadUsername);
     Page<Choice> findAll(Pageable pageable);
     List<Choice> findDistinctByTitleContainingOrTypeContaining(String title,String type);
     Page<Choice> findDistinctByTitleContainingOrTypeContaining(String title,String type,Pageable pageable);
     Integer countDistinctByTypeContaining(String type);
}
