package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_343 extends class_332 implements class_364 {
	protected int field_2114;
	protected int field_2112;
	public final int field_2111;
	public int field_2110;
	private final List<String> field_2121;
	private boolean field_2116;
	public boolean field_2117;
	private boolean field_2115;
	private final int field_2123;
	private int field_2122;
	private int field_2120;
	private int field_2119;
	private final class_327 field_2113;
	private int field_2118;

	public void method_1892(int i, int j, float f) {
		if (this.field_2117) {
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
			);
			this.method_1891(i, j, f);
			int k = this.field_2110 + this.field_2112 / 2 + this.field_2118 / 2;
			int l = k - this.field_2121.size() * 10 / 2;

			for (int m = 0; m < this.field_2121.size(); m++) {
				if (this.field_2116) {
					this.method_1789(this.field_2113, (String)this.field_2121.get(m), this.field_2111 + this.field_2114 / 2, l + m * 10, this.field_2123);
				} else {
					this.method_1780(this.field_2113, (String)this.field_2121.get(m), this.field_2111, l + m * 10, this.field_2123);
				}
			}
		}
	}

	protected void method_1891(int i, int j, float f) {
		if (this.field_2115) {
			int k = this.field_2114 + this.field_2118 * 2;
			int l = this.field_2112 + this.field_2118 * 2;
			int m = this.field_2111 - this.field_2118;
			int n = this.field_2110 - this.field_2118;
			method_1785(m, n, m + k, n + l, this.field_2122);
			this.method_1783(m, m + k, n, this.field_2120);
			this.method_1783(m, m + k, n + l, this.field_2119);
			this.method_1787(m, n, n + l, this.field_2120);
			this.method_1787(m + k, n, n + l, this.field_2119);
		}
	}
}
