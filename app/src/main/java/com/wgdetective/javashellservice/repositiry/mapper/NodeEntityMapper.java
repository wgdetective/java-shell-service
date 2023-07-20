package com.wgdetective.javashellservice.repositiry.mapper;

import com.wgdetective.javashellservice.model.Node;
import com.wgdetective.javashellservice.repositiry.entity.NodeEntity;
import org.mapstruct.Mapper;

/**
 * Mapper.
 */
@Mapper(componentModel = "spring")
public interface NodeEntityMapper {

    Node map(final NodeEntity nodeEntity);

    NodeEntity map(final Node nodeEntity);

}
