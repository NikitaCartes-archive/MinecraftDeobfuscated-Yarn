package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ToastManager extends DrawableHelper {
	private static final int field_39929 = 5;
	private static final int field_39930 = -1;
	final MinecraftClient client;
	private final List<ToastManager.Entry<?>> visibleEntries = new ArrayList();
	private final BitSet field_39931 = new BitSet(5);
	private final Deque<Toast> toastQueue = Queues.<Toast>newArrayDeque();

	public ToastManager(MinecraftClient client) {
		this.client = client;
	}

	public void draw(MatrixStack matrices) {
		if (!this.client.options.hudHidden) {
			int i = this.client.getWindow().getScaledWidth();
			this.visibleEntries.removeIf(entry -> {
				if (entry != null && entry.draw(i, matrices)) {
					this.field_39931.clear(entry.field_39932, entry.field_39932 + entry.field_39933);
					return true;
				} else {
					return false;
				}
			});
			if (!this.toastQueue.isEmpty() && this.method_45076() > 0) {
				this.toastQueue.removeIf(toast -> {
					int ix = toast.method_45072();
					int j = this.method_45073(ix);
					if (j != -1) {
						this.visibleEntries.add(new ToastManager.Entry<>(toast, j, ix));
						this.field_39931.set(j, j + ix);
						return true;
					} else {
						return false;
					}
				});
			}
		}
	}

	private int method_45073(int i) {
		if (this.method_45076() >= i) {
			int j = 0;

			for (int k = 0; k < 5; k++) {
				if (this.field_39931.get(k)) {
					j = 0;
				} else if (++j == i) {
					return k + 1 - j;
				}
			}
		}

		return -1;
	}

	private int method_45076() {
		return 5 - this.field_39931.cardinality();
	}

	@Nullable
	public <T extends Toast> T getToast(Class<? extends T> toastClass, Object type) {
		for (ToastManager.Entry<?> entry : this.visibleEntries) {
			if (entry != null && toastClass.isAssignableFrom(entry.getInstance().getClass()) && entry.getInstance().getType().equals(type)) {
				return (T)entry.getInstance();
			}
		}

		for (Toast toast : this.toastQueue) {
			if (toastClass.isAssignableFrom(toast.getClass()) && toast.getType().equals(type)) {
				return (T)toast;
			}
		}

		return null;
	}

	public void clear() {
		this.field_39931.clear();
		this.visibleEntries.clear();
		this.toastQueue.clear();
	}

	public void add(Toast toast) {
		this.toastQueue.add(toast);
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	@Environment(EnvType.CLIENT)
	class Entry<T extends Toast> {
		private static final long field_32221 = 600L;
		private final T instance;
		final int field_39932;
		final int field_39933;
		private long startTime = -1L;
		private long showTime = -1L;
		private Toast.Visibility visibility = Toast.Visibility.SHOW;

		Entry(T instance, int i, int j) {
			this.instance = instance;
			this.field_39932 = i;
			this.field_39933 = j;
		}

		public T getInstance() {
			return this.instance;
		}

		private float getDisappearProgress(long time) {
			float f = MathHelper.clamp((float)(time - this.startTime) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f;
		}

		public boolean draw(int x, MatrixStack matrixStack) {
			long l = Util.getMeasuringTimeMs();
			if (this.startTime == -1L) {
				this.startTime = l;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			if (this.visibility == Toast.Visibility.SHOW && l - this.startTime <= 600L) {
				this.showTime = l;
			}

			MatrixStack matrixStack2 = RenderSystem.getModelViewStack();
			matrixStack2.push();
			matrixStack2.translate((double)((float)x - (float)this.instance.getWidth() * this.getDisappearProgress(l)), (double)(this.field_39932 * 32), 800.0);
			RenderSystem.applyModelViewMatrix();
			Toast.Visibility visibility = this.instance.draw(matrixStack, ToastManager.this, l - this.showTime);
			matrixStack2.pop();
			RenderSystem.applyModelViewMatrix();
			if (visibility != this.visibility) {
				this.startTime = l - (long)((int)((1.0F - this.getDisappearProgress(l)) * 600.0F));
				this.visibility = visibility;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			return this.visibility == Toast.Visibility.HIDE && l - this.startTime > 600L;
		}
	}
}
