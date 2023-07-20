package com.wgdetective.javashellservice.controller.dto;

/**
 * Node dto.
 *
 * @param id id
 *
 * @param parentId parentId
 *
 * @param value value
 */
public record NodeDto(Long id, Long parentId, String value) {

}
