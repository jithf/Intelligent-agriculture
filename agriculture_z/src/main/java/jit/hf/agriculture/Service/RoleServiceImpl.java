package jit.hf.agriculture.Service;

import jit.hf.agriculture.Repository.SysRoleRepository;
import jit.hf.agriculture.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: zj
 */
@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Override
    public SysRole Add(SysRole name) {
        return sysRoleRepository.save(name);
    }

    @Override
    public SysRole getOneByName(String name) {
        return sysRoleRepository.findOneByName(name);
    }
}
