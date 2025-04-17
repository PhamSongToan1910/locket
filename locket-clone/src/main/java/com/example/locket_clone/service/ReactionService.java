package com.example.locket_clone.service;

import com.example.locket_clone.entities.Reaction;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.entities.response.GetReactionResponse;

import java.util.List;
import java.util.Set;

public interface ReactionService {
    String addReaction(AddReactionPost addReactionPost);

    List<Reaction> getReactions(Set<String> reactionIds);

    void deleteReaction(String reactionId);
}
