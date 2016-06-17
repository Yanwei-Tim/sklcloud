package com.skl.cloud.controller.ipc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.skl.cloud.common.exception.BusinessError;
import com.skl.cloud.common.xml.JAXBGenerator;
import com.skl.cloud.controller.common.SKLController;
import com.skl.cloud.controller.ipc.dto.ResponseStatusIO;
import com.skl.cloud.foundation.mvc.model.ModelKeys;
import com.skl.cloud.foundation.mvc.model.SKLModel;

@Controller
public class IpcController extends SKLController {
    protected Logger log = LoggerFactory.getLogger(IpcController.class);

    @ExceptionHandler(Exception.class)
    public void exceptionHandle(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        SKLModel sklModel = (SKLModel) request.getAttribute(ModelKeys.MODEL_INFO_KEY);
        if (sklModel == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
		BusinessError err = super.handleException(ex);
        try {
            JAXBGenerator generator = new JAXBGenerator(sklModel.getResponseName());
            ResponseStatusIO rs = new ResponseStatusIO(err.getCode(), err.getMsg());
            generator.addParam("ResponseStatus", rs);
            generator.writeTo(response.getOutputStream());
        } catch (Exception e) {
            log.error("生成回复xml出错", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
