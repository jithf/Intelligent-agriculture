package jit.hf.agriculture.Repository;

import jit.hf.agriculture.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * Author: jit.hf
 * Description:UserRepository 接口 继承于CrudRepository
 * Date: Created in 下午7:06 18-3-22
 **/
public interface UserRepository extends JpaRepository<User,Long> {
    public User findOneById(Long id);
    public User findOneByUsername(String username);
    public User deleteById(Long id);
    public Page<User> findAll(Pageable pageable);
    public List<User> findDistinctByUsernameContainingOrNicknameContaining(String username,String nickname);
    public Page<User> findDistinctByUsernameContainingOrNicknameContaining(String username,String nickname,Pageable pageable);
    public Page<User> findAllByAuditNot(String audit,Pageable pageable);
    Integer countDistinctByPermissionEquals(String permission);
}
