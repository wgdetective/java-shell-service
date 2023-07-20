package com.wgdetective.javashellservice.model;

/**
 * Create node request model.
 *
 * @param parentId parentId
 *
 * @param value value
 */
public record CreateNode(Long parentId, String value) {

}
