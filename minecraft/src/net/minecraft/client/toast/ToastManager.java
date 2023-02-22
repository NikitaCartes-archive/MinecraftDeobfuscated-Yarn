package net.minecraft.client.toast;

import com.google.common.collect.Queues;
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
	private static final int SPACES = 5;
	private static final int ALL_OCCUPIED = -1;
	final MinecraftClient client;
	private final List<ToastManager.Entry<?>> visibleEntries = new ArrayList();
	private final BitSet occupiedSpaces = new BitSet(5);
	private final Deque<Toast> toastQueue = Queues.<Toast>newArrayDeque();

	public ToastManager(MinecraftClient client) {
		this.client = client;
	}

	public void draw(MatrixStack matrices) {
		if (!this.client.options.hudHidden) {
			int i = this.client.getWindow().getScaledWidth();
			this.visibleEntries.removeIf(visibleEntry -> {
				if (visibleEntry != null && visibleEntry.draw(i, matrices)) {
					this.occupiedSpaces.clear(visibleEntry.topIndex, visibleEntry.topIndex + visibleEntry.requiredSpaceCount);
					return true;
				} else {
					return false;
				}
			});
			if (!this.toastQueue.isEmpty() && this.getEmptySpaceCount() > 0) {
				this.toastQueue.removeIf(toast -> {
					int ix = toast.getRequiredSpaceCount();
					int j = this.getTopIndex(ix);
					if (j != -1) {
						this.visibleEntries.add(new ToastManager.Entry<>(toast, j, ix));
						this.occupiedSpaces.set(j, j + ix);
						return true;
					} else {
						return false;
					}
				});
			}
		}
	}

	private int getTopIndex(int requiredSpaces) {
		if (this.getEmptySpaceCount() >= requiredSpaces) {
			int i = 0;

			for (int j = 0; j < 5; j++) {
				if (this.occupiedSpaces.get(j)) {
					i = 0;
				} else if (++i == requiredSpaces) {
					return j + 1 - i;
				}
			}
		}

		return -1;
	}

	private int getEmptySpaceCount() {
		return 5 - this.occupiedSpaces.cardinality();
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
		this.occupiedSpaces.clear();
		this.visibleEntries.clear();
		this.toastQueue.clear();
	}

	public void add(Toast toast) {
		this.toastQueue.add(toast);
	}

	public MinecraftClient getClient() {
		return this.client;
	}

	public double getNotificationDisplayTimeMultiplier() {
		return this.client.options.getNotificationDisplayTime().getValue();
	}

	@Environment(EnvType.CLIENT)
	class Entry<T extends Toast> {
		private static final long DISAPPEAR_TIME = 600L;
		private final T instance;
		final int topIndex;
		final int requiredSpaceCount;
		private long startTime = -1L;
		private long showTime = -1L;
		private Toast.Visibility visibility = Toast.Visibility.SHOW;

		Entry(T instance, int topIndex, int requiredSpaceCount) {
			this.instance = instance;
			this.topIndex = topIndex;
			this.requiredSpaceCount = requiredSpaceCount;
		}

		public T getInstance() {
			return this.instance;
		}

		private float getDisappearProgress(long time) {
			float f = MathHelper.clamp((float)(time - this.startTime) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f;
		}

		public boolean draw(int x, MatrixStack matrices) {
			long l = Util.getMeasuringTimeMs();
			if (this.startTime == -1L) {
				this.startTime = l;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			if (this.visibility == Toast.Visibility.SHOW && l - this.startTime <= 600L) {
				this.showTime = l;
			}

			matrices.push();
			matrices.translate((float)x - (float)this.instance.getWidth() * this.getDisappearProgress(l), (float)(this.topIndex * 32), 800.0F);
			Toast.Visibility visibility = this.instance.draw(matrices, ToastManager.this, l - this.showTime);
			matrices.pop();
			if (visibility != this.visibility) {
				this.startTime = l - (long)((int)((1.0F - this.getDisappearProgress(l)) * 600.0F));
				this.visibility = visibility;
				this.visibility.playSound(ToastManager.this.client.getSoundManager());
			}

			return this.visibility == Toast.Visibility.HIDE && l - this.startTime > 600L;
		}
	}
}
