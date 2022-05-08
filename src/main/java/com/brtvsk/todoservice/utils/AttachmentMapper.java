package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.model.dto.AttachmentRequest;
import com.brtvsk.todoservice.model.dto.AttachmentResponse;
import com.brtvsk.todoservice.model.entity.Attachment;
import org.mapstruct.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring",
        uses = {OptionalMapper.class})
public interface AttachmentMapper {
    AttachmentResponse toAttachmentResponse(Attachment entity);

    Attachment fromAttachmentRequest(AttachmentRequest dto);

    default List<Attachment> map(Optional<List<AttachmentRequest>> attachmentRequestList) {
        return attachmentRequestList.orElse(Collections.emptyList()).stream().map(this::fromAttachmentRequest).toList();
    }

}
