package com.sunlights.common;

import com.sunlights.common.exceptions.BusinessRuntimeException;
import com.sunlights.common.models.Parameter;
import com.sunlights.common.utils.CommonUtil;
import com.sunlights.common.dal.ParameterDao;
import com.sunlights.common.utils.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Project: fsp</p>
 * <p>Title: ParameterServiceImpl.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */


public class ParameterService {
    public Map<String, String> params = new HashMap<String, String>();


    private ParameterDao parameterDao;

    public void loadAllParameter() {
        List<Parameter> list = parameterDao.loadAllParameter();
		if (list != null && list.size() != 0) {
			for (Parameter parameter : list) {
				params.put(parameter.getName(), parameter.getValue());
			}
		}
    }

    public void clearAll(){
        params.clear();
    }

    public String getParameterByName(String name){
        String value = params.get(name);
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        return value;
    }

    /**
     * 获取参数
     * @param name
     * @return
     */
    public long getParameterNumeric(String name) throws BusinessRuntimeException {
        String value = params.get(name);
        if (value == null || "".equals(value.trim())) {
            throw CommonUtil.getInstance().errorBusinessException(MsgCode.MISSING_PARAM_CONFIG, name);
        }
        long returnValue = 0;
        try {
            returnValue = Long.valueOf(value);
        } catch (Exception e) {
            throw CommonUtil.getInstance().errorBusinessException(MsgCode.PARAM_IS_NOT_NUMBER, name);
        }
        return returnValue;
    }

    @Transactional
    public Parameter addParameter(String name, String value, String description){
        if(name == null || "".equals(name.trim())){
            throw CommonUtil.getInstance().errorBusinessException(MsgCode.ACCESS_FAIL);
        }

        Timestamp currentTime = DBHelper.getCurrentTime();
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(value);
        parameter.setDescription(description);
        parameter.setCreatedDatetime(currentTime);
        parameter.setUpdatedDatetime(currentTime);

        parameterDao.addParameter(parameter);

        return parameter;
    }

    @Transactional
    public Parameter updateParameter(Parameter parameter){
        return parameterDao.updateParameter(parameter);
    }
}
