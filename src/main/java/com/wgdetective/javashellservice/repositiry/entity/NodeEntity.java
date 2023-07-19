package com.wgdetective.javashellservice.repositiry.entity;

import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Getter
@Setter
@Table("Node")
public final class NodeEntity {

    @Id
    private Long id;

    private Long parentId;

    @Column("node_value")
    private String value;
}
