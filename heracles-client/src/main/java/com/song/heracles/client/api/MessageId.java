package com.song.heracles.client.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author song
 */
@Setter
@Getter
@ToString
public class MessageId {

	private final long logSegmentSequenceNo;
	private final long entryId;
	private final long slotId;

	public MessageId(long logSegmentSequenceNo, long entryId, long slotId) {
		this.logSegmentSequenceNo = logSegmentSequenceNo;
		this.entryId = entryId;
		this.slotId = slotId;
	}

}
