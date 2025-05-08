package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.Conversation;
import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.response.GetChatHistoryResponse;
import com.example.locket_clone.entities.response.ListConversationResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.ConversationService;
import com.example.locket_clone.service.LastMessageService;
import com.example.locket_clone.service.MessageService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/locket-clone/conversation")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConversationController {

    LastMessageService lastMessageService;
    MessageService messageService;
    ConversationService conversationService;
    UserService userService;

    @GetMapping("/list-conversation")
    public ResponseData<List<ListConversationResponse>> listConversation(@CurrentUser CustomUserDetail customUserDetail,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        String userId = customUserDetail.getId();
        Pageable pageable = PageRequest.of(page, size);
        List<LastMessage> lastMessageList = lastMessageService.getLastMessages(pageable);
        List<ListConversationResponse> responseList = lastMessageList.stream().map(lastMessage -> messageService.getMessageById(lastMessage.getMessageId()))
                .filter(Objects::nonNull)
                .map(this::convertMessageToListConversationResponse)
                .toList();
        responseList = responseList.stream().map(listConversationResponse -> this.addAvtAndNameToListConversationResponse(listConversationResponse, userId)).toList();
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @GetMapping("/get-conversation-history")
    public ResponseData<List<Message>> getConversationHistory(@CurrentUser CustomUserDetail customUserDetail,
                                                                             @RequestParam("conversation_id") String conversationId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Message> listMessage = messageService.getMessagesByConversationId(conversationId, pageable);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", listMessage);
    }

    private ListConversationResponse convertMessageToListConversationResponse(Message message) {
        ListConversationResponse response = new ListConversationResponse();
        ModelMapperUtils.toObject(message, response);
        return response;
    }

    private ListConversationResponse addAvtAndNameToListConversationResponse(ListConversationResponse response, String userId) {
        String conversationId = response.getConversationId();
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation != null && conversation.getUserIds().contains(userId)) {
            String friendId = conversation.getUserIds().stream().filter(id -> !id.equals(userId)).findFirst().get();
            User user = userService.findUserById(friendId);
            response.setAvt(user.getAvt());
            response.setName(user.getFullName());
        }
        return response;
    }
}
