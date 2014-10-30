package com.sunlights.common;

import com.sunlights.common.dal.CustomerVerifyCodeDao;
import com.sunlights.common.utils.CommonUtil;
import com.sunlights.common.utils.DateUtils;
import com.sunlights.common.utils.msg.Message;
import com.sunlights.common.utils.msg.MessageUtil;
import com.sunlights.common.dal.CustomerVerifyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.Logger;

import java.sql.Timestamp;
import java.util.Random;

/**
 * Created by Administrator on 2014/10/30.
 */
@Service
public class VerifyCodeService {

    @Autowired
    private CustomerVerifyCodeDao customerVerifyCodeDao;
    @Autowired
    private ParameterService parameterService;
    /**
     * <P>Description: 获取验证码</p>
     * @return
     */
    public String genVerificationCode(String mobilePhoneNo, String type, String deviceNo) {
        Logger.info("========mobilePhoneNo:" + mobilePhoneNo);
        Logger.info("========type:" + type);
        CommonUtil.getInstance().validateParams(mobilePhoneNo, type);
        if (!(AppConst.VERIFY_CODE_REGISTER.equals(type) || AppConst.VERIFY_CODE_RESETPWD.equals(type) || AppConst.VERIFY_CODE_RESET_ACCOUNT.equals(type))) {
            throw CommonUtil.getInstance().errorBusinessException(MsgCode.ACCESS_FAIL);
        }

        Timestamp currentTime = DateUtils.getCurrentTime();
        String verifyCode = null;
        CustomerVerifyCode preCustomerVerifyCode = customerVerifyCodeDao.findVerifyCodeByType(mobilePhoneNo, type);
        if (preCustomerVerifyCode != null) {
            //未失效返回以前的
            long VERIFYCODE_EXPIRY = parameterService.getParameterNumeric(IParameterConst.VERIFYCODE_EXPIRY);//验证码在失效时间
            if (currentTime.getTime() - preCustomerVerifyCode.getCreatedDatetime().getTime() <= VERIFYCODE_EXPIRY * AppConst.ONE_MINUTE) {
                verifyCode = preCustomerVerifyCode.getVerifyCode();
                Logger.info("===========verifyCode:" + verifyCode);
                return verifyCode;
            }else{
                preCustomerVerifyCode.setStatus(AppConst.VERIFY_CODE_STATUS_VALID);
                preCustomerVerifyCode.setUpdatedDatetime(currentTime);
                customerVerifyCodeDao.updateCustomerVerifyCode(preCustomerVerifyCode);
            }
        }

        verifyCode = randomVerifyCode(6);
        CustomerVerifyCode newUserVefiryCode = new CustomerVerifyCode();
        newUserVefiryCode.setVerifyType(type);
        newUserVefiryCode.setMobile(mobilePhoneNo);
        newUserVefiryCode.setVerifyCode(verifyCode);
        newUserVefiryCode.setCreatedDatetime(currentTime);
        newUserVefiryCode.setUpdatedDatetime(currentTime);
        newUserVefiryCode.setDeviceNo(deviceNo);
        customerVerifyCodeDao.saveCustomerVerifyCode(newUserVefiryCode);

        Logger.info("===========mobilePhoneNo:" + mobilePhoneNo);
        Logger.info("===========verifyCode:" + verifyCode);

        return verifyCode;
    }


    private static String randomVerifyCode(int size) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int num = random.nextInt(9);
            sb.append(num);
        }
        return sb.toString();
    }


    /**
     * 验证码验证
     * @return
     */
    public boolean validateVerifyCode(CustomerVerifyCodeVo customerVerifyCodeVo){
        CustomerVerifyCode customerVerifyCode = customerVerifyCodeDao.findVerifyCodeByType(customerVerifyCodeVo.getMobile(), customerVerifyCodeVo.getVerifyType());
        if (customerVerifyCode == null) {
            MessageUtil.getInstance().addMessage(new Message(Message.SEVERITY_ERROR, MsgCode.CERTIFY_NONE));
            return false;
        }
        if (!customerVerifyCode.getVerifyCode().equals(customerVerifyCodeVo.getVerifyCode())) {
            MessageUtil.getInstance().addMessage(new Message(Message.SEVERITY_ERROR, MsgCode.CERTIFY_ERROR));
            return false;
        }
        Timestamp currentTime = DateUtils.getCurrentTime();
        long verifyCodeExpiry = parameterService.getParameterNumeric(IParameterConst.VERIFYCODE_EXPIRY);
        if (currentTime.getTime() - customerVerifyCode.getCreatedDatetime().getTime() >= verifyCodeExpiry * AppConst.ONE_MINUTE) {// 验证码有效时间超时
            customerVerifyCode.setStatus(AppConst.VERIFY_CODE_STATUS_VALID);
            customerVerifyCode.setUpdatedDatetime(currentTime);
            customerVerifyCodeDao.updateCustomerVerifyCode(customerVerifyCode);
            MessageUtil.getInstance().addMessage(new Message(Message.SEVERITY_ERROR, MsgCode.CERTIFY_TIMEOUT));
            return false;
        }
        if (!customerVerifyCode.getDeviceNo().equals(customerVerifyCodeVo.getDeviceNo())) {
            MessageUtil.getInstance().addMessage(new Message(Message.SEVERITY_ERROR, MsgCode.CERTIFY_DEVICE_NOT_MATCH));
            return false;
        }

        customerVerifyCode.setStatus(AppConst.VERIFY_CODE_STATUS_VALID);
        customerVerifyCode.setUpdatedDatetime(currentTime);
        customerVerifyCodeDao.updateCustomerVerifyCode(customerVerifyCode);

        return true;
    }
}