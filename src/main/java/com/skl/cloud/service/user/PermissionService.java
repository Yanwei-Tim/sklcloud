package com.skl.cloud.service.user;

import java.util.List;

import com.skl.cloud.common.exception.BusinessException;
import com.skl.cloud.model.user.Permission;

/**
 * 
 * @ClassName: PermissionService
 * @Description: TODO
 * <p>Creation Date: 2016年5月27日 and by Author: zhaonao </p>
 *
 * @author $Author: zhaonao $
 * @date $Date: 2016-06-07 17:39:56 +0800 (Tue, 07 Jun 2016) $
 * @version  $Revision: 9494 $
 */
public interface PermissionService {

	/**
	 * 
	 * TODO(增加权限)
	 * <p>Creation Date: 2016年6月7日 and by Author: zhaonao </p>
	 * @param permission
	 * @throws BusinessException
	 * @return void
	 * @throws
	 *
	 */
	public void addPermission(Permission permission) throws BusinessException;

	/**
	 * 
	 * TODO(删除权限)
	 * <p>Creation Date: 2016年6月7日 and by Author: zhaonao </p>
	 * @param permissionId
	 * @throws BusinessException
	 * @return void
	 * @throws
	 *
	 */
	public void deletePermissionById(Long permissionId) throws BusinessException;

	/**
	 * 
	 * TODO(批量删除权限)
	 * <p>Creation Date: 2016年6月2日 and by Author: zhaonao </p>
	 * @param permissionIds
	 * @throws BusinessException
	 * @return void
	 * @throws
	 *
	 */
	public void deletePermissionsByIds(List<Long> permissionIds) throws BusinessException;

	/**
	 * 
	 * TODO(更新权限)
	 * <p>Creation Date: 2016年6月7日 and by Author: zhaonao </p>
	 * @param permission
	 * @throws BusinessException
	 * @return void
	 * @throws
	 *
	 */
	public void updatePermission(Permission permission) throws BusinessException;

	/**
	 * 
	 * TODO(查询权限)
	 * <p>Creation Date: 2016年6月7日 and by Author: zhaonao </p>
	 * @param permissionId
	 * @return
	 * @throws BusinessException
	 * @return Permission
	 * @throws
	 *
	 */
	public Permission queryPermissionById(Long permissionId) throws BusinessException;
}
