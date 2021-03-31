package com.mojang.blaze3d.systems;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderCallStorage {
	private final List<ConcurrentLinkedQueue<RenderCall>> recordingQueues = ImmutableList.of(
		new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue(), new ConcurrentLinkedQueue()
	);
	private volatile boolean field_31899;
	private volatile int field_20454;
	private volatile boolean field_31900;
	private volatile int field_20455;
	private volatile int field_20456;

	public RenderCallStorage() {
		this.field_20454 = this.field_20455 = this.field_20456 + 1;
	}

	public boolean method_35599() {
		return !this.field_31899 && this.field_20454 == this.field_20455;
	}

	public boolean method_35601() {
		if (this.field_31899) {
			throw new RuntimeException("ALREADY RECORDING !!!");
		} else if (this.method_35599()) {
			this.field_20454 = (this.field_20455 + 1) % this.recordingQueues.size();
			this.field_31899 = true;
			return true;
		} else {
			return false;
		}
	}

	public void method_35600(RenderCall renderCall) {
		if (!this.field_31899) {
			throw new RuntimeException("NOT RECORDING !!!");
		} else {
			ConcurrentLinkedQueue<RenderCall> concurrentLinkedQueue = this.method_35608();
			concurrentLinkedQueue.add(renderCall);
		}
	}

	public void method_35602() {
		if (this.field_31899) {
			this.field_31899 = false;
		} else {
			throw new RuntimeException("NOT RECORDING !!!");
		}
	}

	public boolean method_35603() {
		return !this.field_31900 && this.field_20454 != this.field_20455;
	}

	public boolean method_35604() {
		if (this.field_31900) {
			throw new RuntimeException("ALREADY PROCESSING !!!");
		} else if (this.method_35603()) {
			this.field_31900 = true;
			return true;
		} else {
			return false;
		}
	}

	public void method_35605() {
		if (!this.field_31900) {
			throw new RuntimeException("NOT PROCESSING !!!");
		}
	}

	public void method_35606() {
		if (this.field_31900) {
			this.field_31900 = false;
			this.field_20456 = this.field_20455;
			this.field_20455 = this.field_20454;
		} else {
			throw new RuntimeException("NOT PROCESSING !!!");
		}
	}

	public ConcurrentLinkedQueue<RenderCall> method_35607() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.field_20456);
	}

	public ConcurrentLinkedQueue<RenderCall> method_35608() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.field_20454);
	}

	public ConcurrentLinkedQueue<RenderCall> method_35609() {
		return (ConcurrentLinkedQueue<RenderCall>)this.recordingQueues.get(this.field_20455);
	}
}
