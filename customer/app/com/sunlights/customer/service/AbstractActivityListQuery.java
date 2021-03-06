package com.sunlights.customer.service;

import com.sunlights.common.utils.CommonUtil;
import com.sunlights.common.vo.PageVo;
import com.sunlights.customer.ActivityConstant;
import com.sunlights.customer.ActivityPageUtil;
import com.sunlights.customer.factory.ActivityServiceFactory;
import com.sunlights.customer.vo.ActivityQueryContext;
import com.sunlights.customer.vo.ActivityVo;
import models.Activity;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangweiqun on 2014/12/17.
 */
public abstract class AbstractActivityListQuery implements ActivityListQuery {
    private ActivityService activityService = ActivityServiceFactory.getActivityService();

    @Override
    public List<ActivityVo> queryActivityList(ActivityQueryContext context) {
        List<Activity> allActivities = activityService.getAllActivities();

        List<Activity> activities = filter(allActivities, context);

        PageVo pageVo = context.getPageVo();
        List<Activity> pageActivities = ActivityPageUtil.page(activities, pageVo);

        List<ActivityVo> activityVos = new ArrayList<ActivityVo>();
        if (pageActivities == null) {
            return activityVos;
        }

        ActivityVo vo = null;
        for (Activity activity : pageActivities) {
            vo = new ActivityVo();
            vo.setId(activity.getId());
            vo.setName(activity.getTitle());
            vo.setImage(activityService.getFileFuleUrl(activity.getImage(), ActivityConstant.ACTIVITY_IMAGE_PATH));
            vo.setUrl(activityService.getFileFuleUrl(activity.getUrl(), ActivityConstant.ACTIVITY_HTML5_PATH) + "?activityId=" + activity.getId());
            vo.setStartDate(CommonUtil.dateToString(activity.getBeginTime(), CommonUtil.DATE_FORMAT_SHORT));
            vo.setEndDate(CommonUtil.dateToString(activity.getEndTime(), CommonUtil.DATE_FORMAT_SHORT));
            activityVos.add(vo);
        }
        return activityVos;
    }


    public List<Activity> filter(List<Activity> allActivities, ActivityQueryContext context) {
        List<Activity> result = new ArrayList<Activity>();
        //TODO 可能会有性能问题
        for (Activity activity : allActivities) {
            if (ActivityConstant.ACTIVITY_STATUS_FORBIDDEN.equals(activity.getStatus())) {
                continue;
            }
            if (activity.getBeginTime().getTime() > System.currentTimeMillis() || activity.getEndTime().getTime() < System.currentTimeMillis()) {
                continue;
            }

            if (StringUtils.isEmpty(activity.getImage()) || StringUtils.isEmpty(activity.getUrl())) {
                continue;
            }
            result.add(activity);

        }
        Logger.debug("result.length = " + result.size());
        return result;
    }

}
