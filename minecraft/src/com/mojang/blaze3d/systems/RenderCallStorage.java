package com.mojang.blaze3d.systems;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A storage of render calls with recording and processing states. It exposes
 * three queues: a recording queue, a processing queue, and a last processed
 * queue. The recording queue is equal to the processing queue during processing
 * and different during recording.
 * 
 * <p>This storage appears to be a work in progress, as its processing currently
 * performs no operation.
 */
@Environment(EnvType.CLIENT)
public class RenderCallStorage {
	private final List<ConcurrentLinkedQueue<RenderCall>> recordingQueues = ImmutableList.of(
		new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()
	);
	private volatile boolean recording;
	private volatile int recordingIndex;
	private volatile boolean processing;
	private volatile int processingIndex;
	private volatile int lastProcessedIndex;

	public RenderCallStorage() {
		this.recordingIndex = this.processingIndex = this.lastProcessedIndex + 1;
	}

	public boolean canRecord() {
		return !this.recording && this.recordingIndex == this.processingIndex;
	}

	public boolean startRecording() {
		if (this.recording) {
			throw new RuntimeException("ALREADY RECORDING !!!");
		} else if (this.canRecord()) {
			this.recordingIndex = (this.processingIndex + 1) % this.recordingQueues.size();
			this.recording = true;
			return true;
		} else {
			return false;
		}
	}

	public void record(RenderCall call) {
		if (!this.recording) {
			throw new RuntimeException("NOT RECORDING !!!");
		} else {
			ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = this.getRecordingQueue();
			concurrentLinkedQueue.add(call);
		}
	}

	public void stopRecording() {
		if (this.recording) {
			this.recording = false;
		} else {
			throw new RuntimeException("NOT RECORDING !!!");
		}
	}

	public boolean canProcess() {
		return !this.processing && this.recordingIndex != this.processingIndex;
	}

	public boolean startProcessing() {
		if (this.processing) {
			throw new RuntimeException("ALREADY PROCESSING !!!");
		} else if (this.canProcess()) {
			this.processing = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * No-op, but it seems like processing by method order and the check in method body.
	 */
	public void process() {
		if (!this.processing) {
			throw new RuntimeException("NOT PROCESSING !!!");
		}
	}

	public void stopProcessing() {
		if (this.processing) {
			this.processing = false;
			this.lastProcessedIndex = this.processingIndex;
			this.processingIndex = this.recordingIndex;
		} else {
			throw new RuntimeException("NOT PROCESSING !!!");
		}
	}

	public ConcurrentLinkedQueue<RenderCall> getLastProcessedQueue() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.lastProcessedIndex);
	}

	public ConcurrentLinkedQueue<RenderCall> getRecordingQueue() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.recordingIndex);
	}

	public ConcurrentLinkedQueue<RenderCall> getProcessingQueue() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.processingIndex);
	}
}
