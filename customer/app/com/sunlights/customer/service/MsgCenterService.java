package com.sunlights.customer.service;

import com.sunlights.common.vo.PageVo;
import com.sunlights.customer.vo.MsgCenterDetailVo;
import com.sunlights.customer.vo.MsgCenterVo;
import models.CustomerMsgReadHistory;

import java.util.List;

/**
 * <p>Project: financeplatform</p>
 * <p>Title: MsgCenterService.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */
public interface MsgCenterService {

    /**
     * 登录后消息中心分页查询
     *
     * @param pageVo
     * @return
     */
    public List<MsgCenterVo> findMsgCenterVoListWithLogin(PageVo pageVo);

    public List<MsgCenterVo> findMsgCenterVoList(PageVo pageVo);

    public MsgCenterDetailVo findMsgCenterDetail(Long msgId, String sendType);

    public void saveMsgReadHistory(CustomerMsgReadHistory customerMsgReadHistory);

    /**
     * 未读数量记录
     *
     * @return
     */
    public int countUnReadNum(String customerId, String deviceNo);

    /**
     * 推送信息设置
     *
     * @param registrationId
     * @param deviceNo
     * @return
     */
    public void enablePush(String registrationId, String deviceNo, String platform);

    public void disablePush(String registrationId, String deviceNo);


}
