package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.domain.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午12:38 18-4-25
 **/
public interface UserInformationRepository extends JpaRepository<UserInformation,Long> {
     UserInformation findOneByUserId(Long id);
     UserInformation findOneByUsername(String username);
     UserInformation deleteByUserId(Long id);
}
