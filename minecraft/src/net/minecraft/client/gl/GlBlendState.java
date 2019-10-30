package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlBlendState {
	private static GlBlendState activeBlendState;
	private final int srcRgb;
	private final int srcAlpha;
	private final int dstRgb;
	private final int dstAlpha;
	private final int func;
	private final boolean separateBlend;
	private final boolean blendDisabled;

	private GlBlendState(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
		this.separateBlend = separateBlend;
		this.srcRgb = srcRgb;
		this.dstRgb = dstRgb;
		this.srcAlpha = srcAlpha;
		this.dstAlpha = dstAlpha;
		this.blendDisabled = blendDisabled;
		this.func = func;
	}

	public GlBlendState() {
		this(false, true, 1, 0, 1, 0, 32774);
	}

	public GlBlendState(int srcRgb, int dstRgb, int func) {
		this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, func);
	}

	public GlBlendState(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
		this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, func);
	}

	public void enable() {
		if (!this.equals(activeBlendState)) {
			if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
				activeBlendState = this;
				if (this.blendDisabled) {
					RenderSystem.disableBlend();
					return;
				}

				RenderSystem.enableBlend();
			}

			RenderSystem.blendEquation(this.func);
			if (this.separateBlend) {
				RenderSystem.blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
			} else {
				RenderSystem.blendFunc(this.srcRgb, this.dstRgb);
			}
		}
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof GlBlendState)) {
			return false;
		} else {
			GlBlendState glBlendState = (GlBlendState)o;
			if (this.func != glBlendState.func) {
				return false;
			} else if (this.dstAlpha != glBlendState.dstAlpha) {
				return false;
			} else if (this.dstRgb != glBlendState.dstRgb) {
				return false;
			} else if (this.blendDisabled != glBlendState.blendDisabled) {
				return false;
			} else if (this.separateBlend != glBlendState.separateBlend) {
				return false;
			} else {
				return this.srcAlpha != glBlendState.srcAlpha ? false : this.srcRgb == glBlendState.srcRgb;
			}
		}
	}

	public int hashCode() {
		int i = this.srcRgb;
		i = 31 * i + this.srcAlpha;
		i = 31 * i + this.dstRgb;
		i = 31 * i + this.dstAlpha;
		i = 31 * i + this.func;
		i = 31 * i + (this.separateBlend ? 1 : 0);
		return 31 * i + (this.blendDisabled ? 1 : 0);
	}

	public boolean isBlendDisabled() {
		return this.blendDisabled;
	}

	public static int getFuncFromString(String string) {
		String string2 = string.trim().toLowerCase(Locale.ROOT);
		if ("add".equals(string2)) {
			return 32774;
		} else if ("subtract".equals(string2)) {
			return 32778;
		} else if ("reversesubtract".equals(string2)) {
			return 32779;
		} else if ("reverse_subtract".equals(string2)) {
			return 32779;
		} else if ("min".equals(string2)) {
			return 32775;
		} else {
			return "max".equals(string2) ? 32776 : 32774;
		}
	}

	public static int getComponentFromString(String string) {
		String string2 = string.trim().toLowerCase(Locale.ROOT);
		string2 = string2.replaceAll("_", "");
		string2 = string2.replaceAll("one", "1");
		string2 = string2.replaceAll("zero", "0");
		string2 = string2.replaceAll("minus", "-");
		if ("0".equals(string2)) {
			return 0;
		} else if ("1".equals(string2)) {
			return 1;
		} else if ("srccolor".equals(string2)) {
			return 768;
		} else if ("1-srccolor".equals(string2)) {
			return 769;
		} else if ("dstcolor".equals(string2)) {
			return 774;
		} else if ("1-dstcolor".equals(string2)) {
			return 775;
		} else if ("srcalpha".equals(string2)) {
			return 770;
		} else if ("1-srcalpha".equals(string2)) {
			return 771;
		} else if ("dstalpha".equals(string2)) {
			return 772;
		} else {
			return "1-dstalpha".equals(string2) ? 773 : -1;
		}
	}
}
