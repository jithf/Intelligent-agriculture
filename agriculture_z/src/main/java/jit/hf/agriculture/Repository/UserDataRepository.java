package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: jit.hf
 * Description:
 * Date: Created in 下午5:23 18-6-3
 **/
public interface UserDataRepository extends JpaRepository<UserData,Long> {

    UserData findByDate(String date);
}
