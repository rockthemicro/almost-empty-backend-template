package com.xentrom.backend.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Output {
    private StatusEnum statusEnum = StatusEnum.SUCCESS;
    private List<String> messages = new ArrayList<>();

    public Output addMessage(String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);

        return this;
    }

    public enum StatusEnum {
        SUCCESS,
        WARNING,
        ERROR
    }
}
