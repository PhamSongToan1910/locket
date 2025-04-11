package com.example.locket_clone.service;

import com.example.locket_clone.entities.Reaction;
import com.example.locket_clone.entities.request.AddReactionPost;

public interface ReactionService {
    String addReaction(AddReactionPost addReactionPost);
}
