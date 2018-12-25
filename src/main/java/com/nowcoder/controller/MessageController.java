package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import util.WendaUtil;

import java.util.Date;

/**
 * @program: wenda
 * @description: MessageController
 * @author: Li Shuai
 * @create: 2018-12-25 11:29
 **/

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            User fromUser = hostHolder.getUser();
            if (fromUser == null) {
                // 999发送过去，popup.js就把用户退出了
                return WendaUtil.getJSONString(999, "未登录");
            }
            User toUser = userService.getUser(toName);
            if (toUser == null) {
                return WendaUtil.getJSONString(1, "目标用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setHasRead(false);
            message.setFromId(fromUser.getId());
            message.setToId(toUser.getId());
            message.setConversationId(fromUser.getId(), toUser.getId());
            messageService.addMessage(message);
        } catch (Exception e) {
            logger.error("发送私信失败");
            return WendaUtil.getJSONString(1, "发送失败");
        }
        // 0表示成功
        return WendaUtil.getJSONString(0);
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        return "letterDetail";
    }
}
