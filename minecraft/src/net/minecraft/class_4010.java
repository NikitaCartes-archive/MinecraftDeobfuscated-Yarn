package net.minecraft;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadStatus;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4010 extends class_4014 {
	private static final Logger field_17918 = LogManager.getLogger();
	private final Stopwatch field_17919 = Stopwatch.createUnstarted();
	private final ProfilerSystem[] field_17920;
	private final ProfileResult[] field_17921;
	private final ProfileResult[] field_17922;

	public class_4010(ResourceManager resourceManager, List<ResourceReloadListener<?>> list, @Nullable CompletableFuture<Void> completableFuture) {
		super(resourceManager, list, completableFuture);
		this.field_17920 = new ProfilerSystem[list.size()];
		this.field_17921 = new ProfileResult[list.size()];
		this.field_17922 = new ProfileResult[list.size()];
	}

	@Override
	protected void method_18236() {
		this.field_17919.start();

		for (int i = 0; i < this.field_17929.size(); i++) {
			this.field_17920[i] = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
			this.field_17928[i] = ((ResourceReloadListener)this.field_17929.get(i)).prepare(this.field_17927, this.field_17920[i]);
		}
	}

	@Override
	protected void method_18238() {
		this.field_17919.stop();
		int i = 0;
		field_17918.info("Resource reload finished after " + this.field_17919.elapsed(TimeUnit.MILLISECONDS) + " ms");

		for (int j = 0; j < this.field_17929.size(); j++) {
			ResourceReloadListener<?> resourceReloadListener = (ResourceReloadListener<?>)this.field_17929.get(j);
			ProfileResult profileResult = this.field_17921[j];
			ProfileResult profileResult2 = this.field_17922[j];
			int k = (int)((float)profileResult.getTimeSpan() / 1000000.0F);
			int l = (int)((float)profileResult2.getTimeSpan() / 1000000.0F);
			int m = k + l;
			String string = resourceReloadListener.getClass().getSimpleName();
			field_17918.info(string + " took approximately " + m + " ms (" + k + " ms preparing, " + l + " ms applying)");
			String string2 = profileResult.method_18052();
			if (string2.length() > 0) {
				field_17918.debug(string + " preparations:\n" + string2);
			}

			String string3 = profileResult2.method_18052();
			if (string3.length() > 0) {
				field_17918.debug(string + " reload:\n" + string3);
			}

			field_17918.info("----------");
			i += l;
		}

		field_17918.info("Total blocking time: " + i + " ms");
	}

	@Override
	protected boolean method_18239() {
		boolean bl = false;

		for (int i = 0; i < this.field_17928.length; i++) {
			CompletableFuture<?> completableFuture = this.field_17928[i];
			if (completableFuture.isDone()) {
				if (this.field_17921[i] == null) {
					this.field_17921[i] = this.field_17920[i].getResults();
				}
			} else {
				bl = true;
			}
		}

		if (bl) {
			this.status = ResourceReloadStatus.field_17923;
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void method_18237(ResourceReloadListener<?> resourceReloadListener, CompletableFuture<?> completableFuture, int i) {
		ProfilerSystem profilerSystem = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
		((ResourceReloadListener<Object>)resourceReloadListener).apply(this.field_17927, completableFuture.join(), profilerSystem);
		this.field_17922[i] = profilerSystem.getResults();
	}
}
