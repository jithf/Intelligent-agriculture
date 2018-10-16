package jit.hf.agriculture.Repository;


import jit.hf.agriculture.domain.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: zj
 */
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {
    public SysRole findOneByName(String name);
}
