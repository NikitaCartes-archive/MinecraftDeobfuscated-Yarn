package net.minecraft;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceLoadProgressProvider;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadStatus;
import net.minecraft.util.profiler.DummyProfiler;

public class class_4014 implements ResourceLoadProgressProvider {
	protected final ResourceManager field_17927;
	protected final CompletableFuture<?>[] field_17928;
	private final CompletableFuture<Void> field_17932;
	protected final List<ResourceReloadListener<?>> field_17929;
	protected ResourceReloadStatus status = ResourceReloadStatus.field_17923;
	protected boolean field_17931 = false;
	private int field_17933;
	private boolean field_17934;

	public class_4014(ResourceManager resourceManager, List<ResourceReloadListener<?>> list, CompletableFuture<Void> completableFuture) {
		this.field_17927 = resourceManager;
		this.field_17929 = list;
		this.field_17928 = new CompletableFuture[list.size()];
		this.field_17932 = completableFuture;
	}

	protected void method_18236() {
		for (int i = 0; i < this.field_17929.size(); i++) {
			this.field_17928[i] = ((ResourceReloadListener)this.field_17929.get(i)).prepare(this.field_17927, DummyProfiler.INSTANCE);
		}
	}

	@Override
	public void method_18226() {
		while (this.getStatus() != ResourceReloadStatus.field_17925) {
			Thread.yield();
		}
	}

	@Override
	public ResourceReloadStatus getStatus() {
		if (!this.field_17934 && this.field_17932 != null) {
			if (this.field_17932.isDone()) {
				this.field_17934 = true;
			}

			this.status = ResourceReloadStatus.field_17923;
			return this.status;
		} else {
			if (!this.field_17931) {
				this.field_17931 = true;
				this.method_18236();
			}

			if (this.method_18239()) {
				return this.status;
			} else if (this.field_17933 < this.field_17929.size()) {
				ResourceReloadListener<?> resourceReloadListener = (ResourceReloadListener<?>)this.field_17929.get(this.field_17933);
				this.method_18237(resourceReloadListener, this.field_17928[this.field_17933], this.field_17933);
				this.status = ResourceReloadStatus.field_17924;
				this.field_17933++;
				return this.status;
			} else {
				this.method_18238();
				this.status = ResourceReloadStatus.field_17925;
				return this.status;
			}
		}
	}

	protected void method_18238() {
	}

	protected boolean method_18239() {
		for (CompletableFuture<?> completableFuture : this.field_17928) {
			if (!completableFuture.isDone()) {
				this.status = ResourceReloadStatus.field_17923;
				return true;
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getTotal() {
		int i = this.field_17932 == null ? 0 : 2;
		return i + this.field_17929.size() * 2 + this.field_17929.size();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getProgress() {
		int i = this.field_17933;
		if (this.field_17932 != null && this.field_17934) {
			i += 2;
		}

		for (int j = 0; j < this.field_17928.length; j++) {
			if (this.field_17928[j] != null && this.field_17928[j].isDone()) {
				i += 2;
			}
		}

		return i;
	}

	protected void method_18237(ResourceReloadListener<?> resourceReloadListener, CompletableFuture<?> completableFuture, int i) {
		((ResourceReloadListener<Object>)resourceReloadListener).apply(this.field_17927, completableFuture.join(), DummyProfiler.INSTANCE);
	}
}
