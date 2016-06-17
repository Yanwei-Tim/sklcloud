package com.skl.cloud.service.user.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.skl.cloud.common.exception.BusinessException;
import com.skl.cloud.dao.user.PermissionMapper;
import com.skl.cloud.dao.user.UserPermissionMapper;
import com.skl.cloud.model.user.Permission;
import com.skl.cloud.model.user.Role;
import com.skl.cloud.service.user.PermissionService;

/**
 * 权限实现类
 * @ClassName: PermissionServiceImpl
 * @Description: TODO
 * <p>Creation Date: 2016年6月1日 and by Author: zhaonao </p>
 *
 * @author $Author: zhaonao $
 * @date $Date: 2016-06-07 17:39:56 +0800 (Tue, 07 Jun 2016) $
 * @version  $Revision: 9494 $
 */
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionMapper permissionMapper;

	@Autowired
	private UserPermissionMapper userPermissionMapper;

	@Override
	@Transactional
	public void addPermission(Permission permission) throws BusinessException {
		permissionMapper.insertPermission(permission);
	}

	@Override
	@Transactional
	public void deletePermissionById(Long permissionId) throws BusinessException {
		// 权限数据不存在时，需删除以下表信息
		// 1. 权限表信息
		permissionMapper.deletePermission(permissionId);

		// 2. 用户&角色表信息
		List<Role> roleList = userPermissionMapper.queryRolePermission(permissionId);
		for (Role role : roleList) {
			userPermissionMapper.deleteUserRole(null, role.getId(), null);
		}

		// 3. 角色&权限表信息
		userPermissionMapper.deleteRolePermission(null, permissionId);
	}

	@Override
	@Transactional
	public void deletePermissionsByIds(List<Long> permissionIds) throws BusinessException {
		for (Long permissionId : permissionIds) {
			this.deletePermissionById(permissionId);
		}
	}

	@Override
	@Transactional
	public void updatePermission(Permission permission) throws BusinessException {
		permissionMapper.updatePermission(permission);
	}

	@Override
	@Transactional(readOnly = true)
	public Permission queryPermissionById(Long permissionId) throws BusinessException {
		Permission permission = new Permission();
		permission.setId(permissionId);
		return permissionMapper.queryPermission(permission);
	}
}
