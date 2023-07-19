package com.wgdetective.javashellservice.controller.mapper;

import com.wgdetective.javashellservice.controller.dto.CreateNodeDto;
import com.wgdetective.javashellservice.controller.dto.NodeDto;
import com.wgdetective.javashellservice.model.CreateNode;
import com.wgdetective.javashellservice.model.Node;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NodeDtoMapper {
    NodeDto map(Node node);
    Node map(NodeDto node);
    CreateNode map(CreateNodeDto node);
}
