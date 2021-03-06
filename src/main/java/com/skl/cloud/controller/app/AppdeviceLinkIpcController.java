package com.skl.cloud.controller.app;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.skl.cloud.controller.common.BaseController;
import com.skl.cloud.service.AppdeviceLinkIpcservice;
import com.skl.cloud.util.common.XmlUtil;

/**
 * 
 * @ClassName: AppdeviceLinkIpcController
 * @Description:APP用户关联IPC设备
 * @Description:APP用户解除自己关联的设备
 * @author guangbo
 * @date 2015年6月8日
 *
 */
@RequestMapping("skl-cloud/app/Security")
@Controller
public class AppdeviceLinkIpcController extends BaseController {
    private static final Logger logger = Logger.getLogger(AppdeviceLinkIpcController.class);

    @Autowired(required = true)
    private AppdeviceLinkIpcservice appdeviceLinkIpcservice;


    /**
     * APP用户关联IPC设备
     * @Title: deviceLink
     * @param inputstream
     * @param request
     * @param session
     * @return ResponseEntity<String> 
	 * @date 2015年6月8日
	 */
    @RequiresPermissions("assignRole:bindDevice:post")
    @RequestMapping("/AAA/users/deviceLink")
    public ResponseEntity<String> deviceLink(InputStream inputstream) {
    	Long userId = getUserId();
   	 try {
   		 appdeviceLinkIpcservice.addDeviceLink(inputstream,userId);
   		 return new ResponseEntity<String>(XmlUtil.mapToXmlRight(),HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(getErrorXml(e, this.getClass().getName()), HttpStatus.OK);
		}
    }

    /**
     * APP用户解除自己关联的前端设备
     */
    @RequestMapping("/AAA/users/deviceLink/remove/{SN}")
    public ResponseEntity<String> removeLinkIpc(@PathVariable("SN") String sn) {
		long userId = getUserId();
		try {
			appdeviceLinkIpcservice.deleteLinkIpc(sn, userId);
			return new ResponseEntity<String>(XmlUtil.mapToXmlRight(),HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e);
			return new ResponseEntity<String>(getErrorXml(e, this.getClass()
					.getName()), HttpStatus.OK);
		}
    }

    /**
     * 
     * <3.8 APP用户请求Install IPC设备>
     * <p>Creation Date: 2016年5月9日 and by Author: fulin </p>
     * @param req
     * @param resp
     * @return
     * @return ResponseEntity<String>
     * @throws
     *
     */
    @RequiresPermissions("assignRole:installDevice:post")
	@RequestMapping(value = "/AAA/users/deviceInstall", method = RequestMethod.POST)
	public ResponseEntity<String> installIpc(HttpServletRequest req, HttpServletResponse resp) {
		String sReturn = null;
		Long userId = getUserId();
		try {
			Map<String, Object> paraMap = XmlUtil.getRequestXmlParam(req);
			String sn = XmlUtil.convertToString(paraMap.get("SN"), false);

			// install 设备
			appdeviceLinkIpcservice.installDevice(userId,sn);
			
			// 返回xml
			sReturn = XmlUtil.mapToXmlRight();
		} catch (Exception e) {
			sReturn = getErrorXml(e, this.getClass().getName());
		}
		return new ResponseEntity<String>(sReturn, HttpStatus.OK);
	}
}
