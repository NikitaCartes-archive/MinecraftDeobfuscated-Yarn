package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class class_922<T extends class_1309, M extends class_583<T>> extends class_897<T> implements class_3883<T, M> {
	private static final Logger field_4741 = LogManager.getLogger();
	private static final class_1043 field_4742 = class_156.method_654(new class_1043(16, 16, false), arg -> {
		arg.method_4525().method_4302();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				arg.method_4525().method_4305(j, i, -1);
			}
		}

		arg.method_4524();
	});
	protected M field_4737;
	protected final FloatBuffer field_4740 = class_311.method_1597(4);
	protected final List<class_3887<T, M>> field_4738 = Lists.<class_3887<T, M>>newArrayList();
	protected boolean field_4739;

	public class_922(class_898 arg, M arg2, float f) {
		super(arg);
		this.field_4737 = arg2;
		this.field_4673 = f;
	}

	protected final boolean method_4046(class_3887<T, M> arg) {
		return this.field_4738.add(arg);
	}

	@Override
	public M method_4038() {
		return this.field_4737;
	}

	public void method_4054(T arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.field_4737.field_3447 = this.method_4044(arg, h);
		this.field_4737.field_3449 = arg.method_5765();
		this.field_4737.field_3448 = arg.method_6109();

		try {
			float i = class_3532.method_17821(h, arg.field_6220, arg.field_6283);
			float j = class_3532.method_17821(h, arg.field_6259, arg.field_6241);
			float k = j - i;
			if (arg.method_5765() && arg.method_5854() instanceof class_1309) {
				class_1309 lv = (class_1309)arg.method_5854();
				i = class_3532.method_17821(h, lv.field_6220, lv.field_6283);
				k = j - i;
				float l = class_3532.method_15393(k);
				if (l < -85.0F) {
					l = -85.0F;
				}

				if (l >= 85.0F) {
					l = 85.0F;
				}

				i = j - l;
				if (l * l > 2500.0F) {
					i += l * 0.2F;
				}

				k = j - i;
			}

			float m = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
			this.method_4048(arg, d, e, f);
			float lx = this.method_4045(arg, h);
			this.method_4058(arg, lx, i, h);
			float n = this.method_4060(arg, h);
			float o = 0.0F;
			float p = 0.0F;
			if (!arg.method_5765() && arg.method_5805()) {
				o = class_3532.method_16439(h, arg.field_6211, arg.field_6225);
				p = arg.field_6249 - arg.field_6225 * (1.0F - h);
				if (arg.method_6109()) {
					p *= 3.0F;
				}

				if (o > 1.0F) {
					o = 1.0F;
				}
			}

			GlStateManager.enableAlphaTest();
			this.field_4737.method_2816(arg, p, o, h);
			this.field_4737.method_17080(arg, p, o, lx, k, m, n);
			if (this.field_4674) {
				boolean bl = this.method_4057(arg);
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
				if (!this.field_4739) {
					this.method_4052(arg, p, o, lx, k, m, n);
				}

				if (!arg.method_7325()) {
					this.method_4051(arg, p, o, h, lx, k, m, n);
				}

				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
				if (bl) {
					this.method_4050();
				}
			} else {
				boolean blx = this.method_4059(arg, h);
				this.method_4052(arg, p, o, lx, k, m, n);
				if (blx) {
					this.method_4040();
				}

				GlStateManager.depthMask(true);
				if (!arg.method_7325()) {
					this.method_4051(arg, p, o, h, lx, k, m, n);
				}
			}

			GlStateManager.disableRescaleNormal();
		} catch (Exception var19) {
			field_4741.error("Couldn't render entity", (Throwable)var19);
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	public float method_4060(T arg, float f) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.method_4042(arg, f);
		float g = 0.0625F;
		GlStateManager.translatef(0.0F, -1.501F, 0.0F);
		return 0.0625F;
	}

	protected boolean method_4057(T arg) {
		GlStateManager.disableLighting();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		return true;
	}

	protected void method_4050() {
		GlStateManager.enableLighting();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	protected void method_4052(T arg, float f, float g, float h, float i, float j, float k) {
		boolean bl = this.method_4056(arg);
		boolean bl2 = !bl && !arg.method_5756(class_310.method_1551().field_1724);
		if (bl || bl2) {
			if (!this.method_3925(arg)) {
				return;
			}

			if (bl2) {
				GlStateManager.setProfile(GlStateManager.class_1032.field_5125);
			}

			this.field_4737.method_2819(arg, f, g, h, i, j, k);
			if (bl2) {
				GlStateManager.unsetProfile(GlStateManager.class_1032.field_5125);
			}
		}
	}

	protected boolean method_4056(T arg) {
		return !arg.method_5767() || this.field_4674;
	}

	protected boolean method_4059(T arg, float f) {
		return this.method_4047(arg, f, true);
	}

	protected boolean method_4047(T arg, float f, boolean bl) {
		float g = arg.method_5718();
		int i = this.method_4053(arg, g, f);
		boolean bl2 = (i >> 24 & 0xFF) > 0;
		boolean bl3 = arg.field_6235 > 0 || arg.field_6213 > 0;
		if (!bl2 && !bl3) {
			return false;
		} else if (!bl2 && !bl) {
			return false;
		} else {
			GlStateManager.activeTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableTexture();
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			GlStateManager.activeTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableTexture();
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			this.field_4740.position(0);
			if (bl3) {
				this.field_4740.put(1.0F);
				this.field_4740.put(0.0F);
				this.field_4740.put(0.0F);
				this.field_4740.put(0.3F);
			} else {
				float h = (float)(i >> 24 & 0xFF) / 255.0F;
				float j = (float)(i >> 16 & 0xFF) / 255.0F;
				float k = (float)(i >> 8 & 0xFF) / 255.0F;
				float l = (float)(i & 0xFF) / 255.0F;
				this.field_4740.put(j);
				this.field_4740.put(k);
				this.field_4740.put(l);
				this.field_4740.put(1.0F - h);
			}

			this.field_4740.flip();
			GlStateManager.texEnv(8960, 8705, this.field_4740);
			GlStateManager.activeTexture(GLX.GL_TEXTURE2);
			GlStateManager.enableTexture();
			GlStateManager.bindTexture(field_4742.method_4624());
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			GlStateManager.activeTexture(GLX.GL_TEXTURE0);
			return true;
		}
	}

	protected void method_4040() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.enableTexture();
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.activeTexture(GLX.GL_TEXTURE2);
		GlStateManager.disableTexture();
		GlStateManager.bindTexture(0);
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	protected void method_4048(T arg, double d, double e, double f) {
		if (arg.method_18376() == class_4050.field_18078) {
			class_2350 lv = arg.method_18401();
			if (lv != null) {
				float g = arg.method_18381(class_4050.field_18076) - 0.1F;
				GlStateManager.translatef((float)d - (float)lv.method_10148() * g, (float)e, (float)f - (float)lv.method_10165() * g);
				return;
			}
		}

		GlStateManager.translatef((float)d, (float)e, (float)f);
	}

	private static float method_18656(class_2350 arg) {
		switch (arg) {
			case field_11035:
				return 90.0F;
			case field_11039:
				return 0.0F;
			case field_11043:
				return 270.0F;
			case field_11034:
				return 180.0F;
			default:
				return 0.0F;
		}
	}

	protected void method_4058(T arg, float f, float g, float h) {
		class_4050 lv = arg.method_18376();
		if (lv != class_4050.field_18078) {
			GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		}

		if (arg.field_6213 > 0) {
			float i = ((float)arg.field_6213 + h - 1.0F) / 20.0F * 1.6F;
			i = class_3532.method_15355(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			GlStateManager.rotatef(i * this.method_4039(arg), 0.0F, 0.0F, 1.0F);
		} else if (arg.method_6123()) {
			GlStateManager.rotatef(-90.0F - arg.field_5965, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(((float)arg.field_6012 + h) * -75.0F, 0.0F, 1.0F, 0.0F);
		} else if (lv == class_4050.field_18078) {
			class_2350 lv2 = arg.method_18401();
			GlStateManager.rotatef(lv2 != null ? method_18656(lv2) : g, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.method_4039(arg), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (arg.method_16914() || arg instanceof class_1657) {
			String string = class_124.method_539(arg.method_5477().getString());
			if (string != null
				&& ("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(arg instanceof class_1657) || ((class_1657)arg).method_7348(class_1664.field_7559))) {
				GlStateManager.translatef(0.0F, arg.method_17682() + 0.1F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	protected float method_4044(T arg, float f) {
		return arg.method_6055(f);
	}

	protected float method_4045(T arg, float f) {
		return (float)arg.field_6012 + f;
	}

	protected void method_4051(T arg, float f, float g, float h, float i, float j, float k, float l) {
		for (class_3887<T, M> lv : this.field_4738) {
			boolean bl = this.method_4047(arg, h, lv.method_4200());
			lv.method_4199(arg, f, g, h, i, j, k, l);
			if (bl) {
				this.method_4040();
			}
		}
	}

	protected float method_4039(T arg) {
		return 90.0F;
	}

	protected int method_4053(T arg, float f, float g) {
		return 0;
	}

	protected void method_4042(T arg, float f) {
	}

	public void method_4041(T arg, double d, double e, double f) {
		if (this.method_4055(arg)) {
			double g = arg.method_5707(this.field_4676.field_4686.method_19326());
			float h = arg.method_20231() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = arg.method_5476().method_10863();
				GlStateManager.alphaFunc(516, 0.1F);
				this.method_3930(arg, d, e, f, string, g);
			}
		}
	}

	protected boolean method_4055(T arg) {
		class_746 lv = class_310.method_1551().field_1724;
		boolean bl = !arg.method_5756(lv);
		if (arg != lv) {
			class_270 lv2 = arg.method_5781();
			class_270 lv3 = lv.method_5781();
			if (lv2 != null) {
				class_270.class_272 lv4 = lv2.method_1201();
				switch (lv4) {
					case field_1442:
						return bl;
					case field_1443:
						return false;
					case field_1444:
						return lv3 == null ? bl : lv2.method_1206(lv3) && (lv2.method_1199() || bl);
					case field_1446:
						return lv3 == null ? bl : !lv2.method_1206(lv3) && bl;
					default:
						return true;
				}
			}
		}

		return class_310.method_1498() && arg != this.field_4676.field_4686.method_19331() && bl && !arg.method_5782();
	}
}
