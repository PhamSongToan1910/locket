package com.example.locket_clone.runner;

import com.example.locket_clone.entities.Post;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.service.PostService;
import com.example.locket_clone.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EventPostRunner implements CommandLineRunner {

    public static ConcurrentLinkedQueue<AddReactionPost> reactions = new ConcurrentLinkedQueue<>();
    private final ReactionService reactionService;
    private final PostService postService;

    private final ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor(Thread::new);

    private static final long INITIAL_DELAY_MS = 0;
    private static final long PERIOD_MS = 500;



    @Override
    public void run(String... args) throws Exception {
        schedule.scheduleAtFixedRate(this::proccessEventReaction, INITIAL_DELAY_MS, PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void proccessEventReaction() {
        try{
            AddReactionPost reactionPost;
            while((reactionPost = reactions.poll()) != null){
                Post post = postService.findbyId(reactionPost.getPostId());
                if(post != null && post.getFriendIds().contains(reactionPost.getUserId()) && !post.getUserId().equals(reactionPost.getUserId())){
                    String reactionId = reactionService.addReaction(reactionPost);
                    postService.addReactionToPost(post, reactionId);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
