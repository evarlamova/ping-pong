package ru.varlamova.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PingRequest {
    private String id;
    private int countPing;
}
