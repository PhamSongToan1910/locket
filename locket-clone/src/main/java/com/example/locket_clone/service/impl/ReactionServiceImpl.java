package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Reaction;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.repository.InterfacePackage.ReactionRepository;
import com.example.locket_clone.service.ReactionService;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionServiceImpl implements ReactionService {

    ReactionRepository reactionRepository;

    @Override
    public String addReaction(AddReactionPost addReactionPost) {
        Reaction reaction = reactionRepository.findReactionByUserIdAndPostId(addReactionPost.getUserId(), addReactionPost.getPostId());
        if (reaction != null) {
            if(reaction.getIcons().size() >= 4) {
                reaction.getIcons().remove(reaction.getIcons().getFirst());
                reaction.getIcons().add(addReactionPost.getReactType());
            } else {
                reaction.getIcons().add(addReactionPost.getReactType());
            }
            return reaction.getId().toString();
        }
        Reaction newReaction = new Reaction();
        ModelMapperUtils.toObject(newReaction, addReactionPost);
        newReaction.getIcons().add(addReactionPost.getReactType());
        return reactionRepository.save(newReaction).getId().toString();
    }
}
