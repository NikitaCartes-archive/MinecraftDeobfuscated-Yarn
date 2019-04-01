package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_630 {
	public float field_3659 = 64.0F;
	public float field_3658 = 32.0F;
	private int field_3672;
	private int field_3670;
	public float field_3657;
	public float field_3656;
	public float field_3655;
	public float field_3654;
	public float field_3675;
	public float field_3674;
	private boolean field_3660;
	private int field_3667;
	public boolean field_3666;
	public boolean field_3665 = true;
	public boolean field_3664;
	public final List<class_628> field_3663 = Lists.<class_628>newArrayList();
	public List<class_630> field_3661;
	public final String field_3662;
	public float field_3673;
	public float field_3671;
	public float field_3669;

	public class_630(class_3879 arg, String string) {
		arg.field_17137.add(this);
		this.field_3662 = string;
		this.method_2853(arg.field_17138, arg.field_17139);
	}

	public class_630(class_3879 arg) {
		this(arg, null);
	}

	public class_630(class_3879 arg, int i, int j) {
		this(arg);
		this.method_2850(i, j);
	}

	public void method_17138(class_630 arg) {
		this.field_3654 = arg.field_3654;
		this.field_3675 = arg.field_3675;
		this.field_3674 = arg.field_3674;
		this.field_3657 = arg.field_3657;
		this.field_3656 = arg.field_3656;
		this.field_3655 = arg.field_3655;
	}

	public void method_2845(class_630 arg) {
		if (this.field_3661 == null) {
			this.field_3661 = Lists.<class_630>newArrayList();
		}

		this.field_3661.add(arg);
	}

	public void method_17578(class_630 arg) {
		if (this.field_3661 != null) {
			this.field_3661.remove(arg);
		}
	}

	public class_630 method_2850(int i, int j) {
		this.field_3672 = i;
		this.field_3670 = j;
		return this;
	}

	public class_630 method_2848(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
		string = this.field_3662 + "." + string;
		this.method_2850(m, n);
		this.field_3663.add(new class_628(this, this.field_3672, this.field_3670, f, g, h, i, j, k, l).method_2842(string));
		return this;
	}

	public class_630 method_2844(float f, float g, float h, int i, int j, int k) {
		this.field_3663.add(new class_628(this, this.field_3672, this.field_3670, f, g, h, i, j, k, 0.0F));
		return this;
	}

	public class_630 method_2854(float f, float g, float h, int i, int j, int k, boolean bl) {
		this.field_3663.add(new class_628(this, this.field_3672, this.field_3670, f, g, h, i, j, k, 0.0F, bl));
		return this;
	}

	public void method_2856(float f, float g, float h, int i, int j, int k, float l) {
		this.field_3663.add(new class_628(this, this.field_3672, this.field_3670, f, g, h, i, j, k, l));
	}

	public void method_2849(float f, float g, float h, int i, int j, int k, float l, boolean bl) {
		this.field_3663.add(new class_628(this, this.field_3672, this.field_3670, f, g, h, i, j, k, l, bl));
	}

	public void method_2851(float f, float g, float h) {
		this.field_3657 = f;
		this.field_3656 = g;
		this.field_3655 = h;
	}

	public void method_2846(float f) {
		if (!this.field_3664) {
			if (this.field_3665) {
				if (!this.field_3660) {
					this.method_2855(f);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.field_3673, this.field_3671, this.field_3669);
				if (this.field_3654 != 0.0F || this.field_3675 != 0.0F || this.field_3674 != 0.0F) {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.field_3657 * f, this.field_3656 * f, this.field_3655 * f);
					if (this.field_3674 != 0.0F) {
						GlStateManager.rotatef(this.field_3674 * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.field_3675 != 0.0F) {
						GlStateManager.rotatef(this.field_3675 * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.field_3654 != 0.0F) {
						GlStateManager.rotatef(this.field_3654 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}

					GlStateManager.callList(this.field_3667);
					if (this.field_3661 != null) {
						for (int i = 0; i < this.field_3661.size(); i++) {
							((class_630)this.field_3661.get(i)).method_2846(f);
						}
					}

					GlStateManager.popMatrix();
				} else if (this.field_3657 == 0.0F && this.field_3656 == 0.0F && this.field_3655 == 0.0F) {
					GlStateManager.callList(this.field_3667);
					if (this.field_3661 != null) {
						for (int i = 0; i < this.field_3661.size(); i++) {
							((class_630)this.field_3661.get(i)).method_2846(f);
						}
					}
				} else {
					GlStateManager.pushMatrix();
					GlStateManager.translatef(this.field_3657 * f, this.field_3656 * f, this.field_3655 * f);
					GlStateManager.callList(this.field_3667);
					if (this.field_3661 != null) {
						for (int i = 0; i < this.field_3661.size(); i++) {
							((class_630)this.field_3661.get(i)).method_2846(f);
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public void method_2852(float f) {
		if (!this.field_3664) {
			if (this.field_3665) {
				if (!this.field_3660) {
					this.method_2855(f);
				}

				GlStateManager.pushMatrix();
				GlStateManager.translatef(this.field_3657 * f, this.field_3656 * f, this.field_3655 * f);
				if (this.field_3675 != 0.0F) {
					GlStateManager.rotatef(this.field_3675 * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
				}

				if (this.field_3654 != 0.0F) {
					GlStateManager.rotatef(this.field_3654 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
				}

				if (this.field_3674 != 0.0F) {
					GlStateManager.rotatef(this.field_3674 * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
				}

				GlStateManager.callList(this.field_3667);
				GlStateManager.popMatrix();
			}
		}
	}

	public void method_2847(float f) {
		if (!this.field_3664) {
			if (this.field_3665) {
				if (!this.field_3660) {
					this.method_2855(f);
				}

				if (this.field_3654 != 0.0F || this.field_3675 != 0.0F || this.field_3674 != 0.0F) {
					GlStateManager.translatef(this.field_3657 * f, this.field_3656 * f, this.field_3655 * f);
					if (this.field_3674 != 0.0F) {
						GlStateManager.rotatef(this.field_3674 * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
					}

					if (this.field_3675 != 0.0F) {
						GlStateManager.rotatef(this.field_3675 * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
					}

					if (this.field_3654 != 0.0F) {
						GlStateManager.rotatef(this.field_3654 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
					}
				} else if (this.field_3657 != 0.0F || this.field_3656 != 0.0F || this.field_3655 != 0.0F) {
					GlStateManager.translatef(this.field_3657 * f, this.field_3656 * f, this.field_3655 * f);
				}
			}
		}
	}

	private void method_2855(float f) {
		this.field_3667 = class_311.method_1593(1);
		GlStateManager.newList(this.field_3667, 4864);
		class_287 lv = class_289.method_1348().method_1349();

		for (int i = 0; i < this.field_3663.size(); i++) {
			((class_628)this.field_3663.get(i)).method_2843(lv, f);
		}

		GlStateManager.endList();
		this.field_3660 = true;
	}

	public class_630 method_2853(int i, int j) {
		this.field_3659 = (float)i;
		this.field_3658 = (float)j;
		return this;
	}
}
