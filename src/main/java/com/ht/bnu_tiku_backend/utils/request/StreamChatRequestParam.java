package com.ht.bnu_tiku_backend.utils.request;

import lombok.Data;

@Data
public class StreamChatRequestParam {
    String message;

    String modelId;

    String chatId;
}
