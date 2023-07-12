package com.learning.belightweightpaymentsystem.rmq;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message<T> {

    private T message;
    private Date date;
    private UUID uuid;

}
