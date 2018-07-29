package com.song.heracles.broker.core.message;

import com.song.heracles.broker.core.Offset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author song
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Offset offset;

    private byte[] payload;
}
