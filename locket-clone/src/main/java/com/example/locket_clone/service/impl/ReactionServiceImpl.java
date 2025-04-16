package com.example.locket_clone.service.impl;

import com.example.locket_clone.entities.Reaction;
import com.example.locket_clone.entities.request.AddReactionPost;
import com.example.locket_clone.entities.response.GetReactionResponse;
import com.example.locket_clone.repository.InterfacePackage.ReactionRepository;
import com.example.locket_clone.service.ReactionService;
import com.example.locket_clone.utils.ModelMapper.ModelMapperUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            reactionRepository.save(reaction);
            return reaction.getId().toString();
        }
        Reaction newReaction = new Reaction();
        ModelMapperUtils.toObject(addReactionPost, newReaction);
        newReaction.getIcons().add(addReactionPost.getReactType());
        return reactionRepository.save(newReaction).getId().toString();
    }

    @Override
    public List<Reaction> getReactions(Set<String> listReactionId) {
        return listReactionId.stream().map(reactionId -> reactionRepository.findById(reactionId).orElse(null))
                .filter(Objects::nonNull).toList();
    }

}
