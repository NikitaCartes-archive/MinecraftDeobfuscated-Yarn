package net.minecraft.client.toast;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ToastManager extends Drawable {
	private final MinecraftClient client;
	private final ToastManager.Entry<?>[] visibleEntries = new ToastManager.Entry[5];
	private final Deque<Toast> toastQueue = Queues.<Toast>newArrayDeque();

	public ToastManager(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void draw() {
		if (!this.client.field_1690.field_1842) {
			GuiLighting.disable();

			for (int i = 0; i < this.visibleEntries.length; i++) {
				ToastManager.Entry<?> entry = this.visibleEntries[i];
				if (entry != null && entry.draw(this.client.window.getScaledWidth(), i)) {
					this.visibleEntries[i] = null;
				}

				if (this.visibleEntries[i] == null && !this.toastQueue.isEmpty()) {
					this.visibleEntries[i] = new ToastManager.Entry((Toast)this.toastQueue.removeFirst());
				}
			}
		}
	}

	@Nullable
	public <T extends Toast> T method_1997(Class<? extends T> class_, Object object) {
		for (ToastManager.Entry<?> entry : this.visibleEntries) {
			if (entry != null && class_.isAssignableFrom(entry.getInstance().getClass()) && entry.getInstance().method_1987().equals(object)) {
				return (T)entry.getInstance();
			}
		}

		for (Toast toast : this.toastQueue) {
			if (class_.isAssignableFrom(toast.getClass()) && toast.method_1987().equals(object)) {
				return (T)toast;
			}
		}

		return null;
	}

	public void clear() {
		Arrays.fill(this.visibleEntries, null);
		this.toastQueue.clear();
	}

	public void add(Toast toast) {
		this.toastQueue.add(toast);
	}

	public MinecraftClient getGame() {
		return this.client;
	}

	@Environment(EnvType.CLIENT)
	class Entry<T extends Toast> {
		private final T instance;
		private long field_2243 = -1L;
		private long field_2242 = -1L;
		private Toast.class_369 field_2244 = Toast.class_369.field_2210;

		private Entry(T toast) {
			this.instance = toast;
		}

		public T getInstance() {
			return this.instance;
		}

		private float method_2003(long l) {
			float f = MathHelper.clamp((float)(l - this.field_2243) / 600.0F, 0.0F, 1.0F);
			f *= f;
			return this.field_2244 == Toast.class_369.field_2209 ? 1.0F - f : f;
		}

		public boolean draw(int i, int j) {
			long l = SystemUtil.getMeasuringTimeMs();
			if (this.field_2243 == -1L) {
				this.field_2243 = l;
				this.field_2244.play(ToastManager.this.client.getSoundLoader());
			}

			if (this.field_2244 == Toast.class_369.field_2210 && l - this.field_2243 <= 600L) {
				this.field_2242 = l;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translatef((float)i - 160.0F * this.method_2003(l), (float)(j * 32), (float)(500 + j));
			Toast.class_369 lv = this.instance.draw(ToastManager.this, l - this.field_2242);
			GlStateManager.popMatrix();
			if (lv != this.field_2244) {
				this.field_2243 = l - (long)((int)((1.0F - this.method_2003(l)) * 600.0F));
				this.field_2244 = lv;
				this.field_2244.play(ToastManager.this.client.getSoundLoader());
			}

			return this.field_2244 == Toast.class_369.field_2209 && l - this.field_2243 > 600L;
		}
	}
}
