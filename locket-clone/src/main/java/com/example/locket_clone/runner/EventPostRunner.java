package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.*;
import com.example.locket_clone.entities.response.ResponseData;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReactionService;
import com.example.locket_clone.service.ReportPostService;
import com.example.locket_clone.service.UnreadPostService;
import com.example.locket_clone.utils.Constant.Constant;
import com.example.locket_clone.utils.Constant.ResponseCode;
import com.example.locket_clone.utils.s3Utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EventPostRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<ObjectRequest> requests = new ConcurrentLinkedQueue<>();

    private final ReactionService reactionService;
    private final PostService postService;
    private final ReportPostService reportPostService;
    private final S3Service s3Service;
    private final UnreadPostService unreadPostService;

    private final int MAX_THREAD_POOL = 5;
    private final ExecutorService schedule = Executors.newFixedThreadPool(MAX_THREAD_POOL);


    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < MAX_THREAD_POOL; i++) {
            schedule.execute(() -> {
                while (true) {
                    ObjectRequest objectRequest = requests.poll();
                    if (objectRequest != null) {
                        proccessEventReaction(objectRequest);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private void proccessEventReaction(ObjectRequest objectRequest) {
        try {
            switch (objectRequest.getType()) {
                case Constant.API.ADD_REACTION -> addReaction(objectRequest);
                case Constant.API.REPORT_POST -> reportPost(objectRequest);
                case Constant.API.HIDE_POST -> hidePost(objectRequest);
                case Constant.API.DELETE_POST -> deletePost(objectRequest);
                case Constant.API.ADD_POST_TO_UNREAD_POST -> addUnreadPost(objectRequest);
                case Constant.API.CHANGE_UNREAD_POST_STATUS -> changeUnreadPostStatus(objectRequest);
                case Constant.API.DELETE_POST_BY_ADMIN -> deletePost(objectRequest);
                case Constant.API.UPDATE_POST_BY_ADMIN -> deletePostByAdminV2(objectRequest);
                case Constant.API.UPDATE_REPORT_POST_BY_ADMIN -> deleteReportPostByAdmin(objectRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addReaction(ObjectRequest objectRequest) {
        AddReactionPost reactionPost = (AddReactionPost) objectRequest.getData();
        Post post = postService.findbyId(reactionPost.getPostId());
        if (post != null && post.getFriendIds().contains(reactionPost.getUserId()) && !post.getUserId().equals(reactionPost.getUserId())) {
            String reactionId = reactionService.addReaction(reactionPost);
            postService.addReactionToPost(post, reactionId);
        }
    }

    private void reportPost(ObjectRequest objectRequest) {
        ReportPostRequest reportPostRequest = (ReportPostRequest) objectRequest.getData();
        Post post = postService.findbyId(reportPostRequest.getPostId());
        if (Objects.nonNull(post) && post.getFriendIds().contains(reportPostRequest.getUserId())) {
            reportPostService.addReportPost(reportPostRequest.getUserId(), reportPostRequest.getPostId());
        }
    }

    private void hidePost(ObjectRequest objectRequest) {
        HidePostRequest request = (HidePostRequest) objectRequest.getData();
        Post post = postService.findbyId(request.getPostId());
        if (Objects.nonNull(post) && post.getFriendIds().contains(request.getUserId()) && !post.getUserId().equals(request.getUserId())) {
            postService.hidePost(post, request.getUserId());
        }
    }

    private void deletePost(ObjectRequest objectRequest) {
        DeletePostRequest deletePostRequest = (DeletePostRequest) objectRequest.getData();
        Post post = postService.findbyId(deletePostRequest.getPostId());
        if(objectRequest.getType() == Constant.API.DELETE_POST) {
            if (Objects.nonNull(post) && post.getUserId().equals(deletePostRequest.getUserId())) {
                postService.deletePost(post.getId().toString());
                s3Service.deleteFile(s3Service.getFileNameFromURl(post.getImageURL()));
                post.getReactionIds().forEach(reactionService::deleteReaction);
            }
        } else if(objectRequest.getType() == Constant.API.DELETE_POST_BY_ADMIN) {
            if(Objects.nonNull(post)) {
                postService.deletePost(post.getId().toString());
                s3Service.deleteFile(s3Service.getFileNameFromURl(post.getImageURL()));
                post.getReactionIds().forEach(reactionService::deleteReaction);
            }
        }
    }

    private void addUnreadPost(ObjectRequest objectRequest) {
        System.out.println("========= addUnreadPost ===========");
        AddUnreadPostRequest request = (AddUnreadPostRequest) objectRequest.getData();
        String postId = request.getPostId();
        request.getUserIds().forEach(userId -> unreadPostService.addUnreadPost(userId, postId));
    }

    private void changeUnreadPostStatus(ObjectRequest objectRequest) {
        String userId = (String) objectRequest.getData();
        unreadPostService.deleteUnreadPost(userId);
    }

    private void deletePostByAdminV2(ObjectRequest objectRequest) {
        UpdateReportPostByAdmin request = (UpdateReportPostByAdmin) objectRequest.getData();
        postService.updatePostByAdmin(request);
    }

    private void deleteReportPostByAdmin(ObjectRequest objectRequest) {
        UpdateReportPostByAdmin request = (UpdateReportPostByAdmin) objectRequest.getData();
        reportPostService.updateStatusAndActionReportPostByAdmin(request);
    }
}
