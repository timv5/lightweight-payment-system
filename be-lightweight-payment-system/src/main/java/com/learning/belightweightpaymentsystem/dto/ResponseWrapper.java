package com.learning.belightweightpaymentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseWrapper<T> {

    private T data;
    private boolean success;

    public ResponseWrapper(boolean success) {
        this.success = success;
    }
}
