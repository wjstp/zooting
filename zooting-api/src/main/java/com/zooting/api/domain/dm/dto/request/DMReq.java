package com.zooting.api.domain.dm.dto.request;

import com.zooting.api.domain.file.dto.response.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "DM전송 시 요청 Dto")
public record DMReq(
        @Schema(description = "DM방 ID")
        @NotNull
        Long dmRoomId,
        @Schema(description = "메시지")
        @NotNull
        String message,
        @Schema(description = "발신자")
        @NotNull
        String sender,
        @Schema(description = "수신자")
        @NotNull
        String receiver,
        //TODO: file
        List<FileRes> files
) {
}