package jit.hf.agriculture.Service;


import jit.hf.agriculture.domain.SysRole;

/**
 * Author: zj
 */
public interface RoleService {
    public SysRole Add(SysRole name);

    SysRole getOneByName(String name);
}
