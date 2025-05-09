package com.example.locket_clone.controller;

import com.example.locket_clone.config.CurrentUser;
import com.example.locket_clone.config.security.CustomUserDetail;
import com.example.locket_clone.entities.Conversation;
import com.example.locket_clone.entities.LastMessage;
import com.example.locket_clone.entities.Message;
import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.User;
import com.example.locket_clone.entities.response.GetConversationHistoryResponse;
import com.example.locket_clone.entities.response.ListConversationResponse;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.repository.InterfacePackage.MessageRepository;
import com.example.locket_clone.service.ConversationService;
import com.example.locket_clone.service.LastMessageService;
import com.example.locket_clone.service.MessageService;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.UserService;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.DateTimeConvertUtils;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
    PostService postService;
    MessageRepository messageRepository;

    @GetMapping("/list-conversation")
    public ResponseData<List<ListConversationResponse>> listConversation(@CurrentUser CustomUserDetail customUserDetail,
                                                                         @RequestParam("skip") int skip,
                                                                         @RequestParam("take") int take) {
        String userId = customUserDetail.getId();
        List<LastMessage> lastMessageList = lastMessageService.getLastMessages(skip, take);
        List<ListConversationResponse> responseList = lastMessageList.stream().map(lastMessage -> messageService.getMessageById(lastMessage.getMessageId()))
                .filter(Objects::nonNull)
                .map(this::convertMessageToListConversationResponse)
                .toList();
        responseList = responseList.stream().map(listConversationResponse -> this.addAvtAndNameToListConversationResponse(listConversationResponse, userId)).toList();
        return new ResponseData<>(ResponseCode.SUCCESS, "success", responseList);
    }

    @GetMapping("/get-conversation-history")
    public ResponseData<List<GetConversationHistoryResponse>> getConversationHistory(@CurrentUser CustomUserDetail customUserDetail,
                                                                             @RequestParam("conversation_id") String conversationId,
                                                                             @RequestParam("skip") int skip,
                                                                             @RequestParam("take") int take) {
        System.out.println("take: " + take);
        List<Message> listMessage = messageService.getMessages(conversationId, skip, take);
        List<GetConversationHistoryResponse> response = listMessage.stream().map(message -> {
            GetConversationHistoryResponse getConversationHistoryResponse = new GetConversationHistoryResponse();
            ModelMapperUtils.toObject(message, getConversationHistoryResponse);
            getConversationHistoryResponse.setId(message.getId().toString());
            if(StringUtils.hasLength(message.getPostId())) {
                Post post = postService.findbyId(message.getPostId());
                getConversationHistoryResponse.setPostURL(post.getImageURL());
            }
            return getConversationHistoryResponse;
        }).toList();
        return new ResponseData<>(ResponseCode.SUCCESS, "success", response);
    }

    @GetMapping("/total-unread")
    public ResponseData<Long> getUnread(@CurrentUser CustomUserDetail customUserDetail) {
        String userId = customUserDetail.getId();
        long unreadMessageNumber = messageService.countUnreadMessageByUserReceiverId(userId);
        return new ResponseData<>(ResponseCode.SUCCESS, "success", unreadMessageNumber);
    }

    @PostMapping("/update-read-status")
    public ResponseData updateReadStatus(@CurrentUser CustomUserDetail customUserDetail,
                                         @RequestParam("message_id") String messageId) {
        messageService.updateReadStatus(messageId);
        return new ResponseData<>(ResponseCode.SUCCESS, "success");
    }

    private ListConversationResponse convertMessageToListConversationResponse(Message message) {
        ListConversationResponse response = new ListConversationResponse();
        ModelMapperUtils.toObject(message, response);
        if(System.currentTimeMillis() - message.getCreatedAt().toEpochMilli() <= 86400000) {
            response.setCreatedAt(message.getCreatedAt().toString().substring(11, 16));
        } else {
            response.setCreatedAt(DateTimeConvertUtils.convertDateToString(Date.from(message.getCreatedAt())));
        }
        return response;
    }

    private ListConversationResponse addAvtAndNameToListConversationResponse(ListConversationResponse response, String userId) {
        String conversationId = response.getConversationId();
        Conversation conversation = conversationService.getConversationById(conversationId);
        if (conversation != null && conversation.getUserIds().contains(userId)) {
            String friendId = conversation.getUserIds().stream().filter(id -> !id.equals(userId)).findFirst().get();
            User user = userService.findUserById(friendId);
            response.setUserSenderId(user.getId().toString());
            response.setAvt(user.getAvt());
            response.setName(user.getFullName());
        }
        return response;
    }
}
