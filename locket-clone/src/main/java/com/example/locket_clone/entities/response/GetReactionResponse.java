package com.example.locket_clone.entities.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetReactionResponse {
    @JsonProperty("first_name")
    private String firstname;

    @JsonProperty("avt")
    private String avt;

    @JsonProperty("list_reactions")
    private LinkedList<Integer> reactions;
}
