package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_898 {
	private final Map<Class<? extends class_1297>, class_897<? extends class_1297>> field_4696 = Maps.<Class<? extends class_1297>, class_897<? extends class_1297>>newHashMap();
	private final Map<String, class_1007> field_4687 = Maps.<String, class_1007>newHashMap();
	private final class_1007 field_4683;
	private class_327 field_4689;
	private double field_4691;
	private double field_4690;
	private double field_4688;
	public final class_1060 field_4685;
	public class_1937 field_4684;
	public class_1297 field_4686;
	public class_1297 field_4678;
	public float field_4679;
	public float field_4677;
	public class_315 field_4692;
	public double field_4695;
	public double field_4694;
	public double field_4693;
	private boolean field_4682;
	private boolean field_4681 = true;
	private boolean field_4680;

	private <T extends class_1297> void method_17145(Class<T> class_, class_897<? super T> arg) {
		this.field_4696.put(class_, arg);
	}

	public class_898(class_1060 arg, class_918 arg2, class_3296 arg3) {
		this.field_4685 = arg;
		this.method_17145(class_1549.class, new class_880(this));
		this.method_17145(class_1628.class, new class_949(this));
		this.method_17145(class_1452.class, new class_932(this));
		this.method_17145(class_1472.class, new class_941(this));
		this.method_17145(class_1430.class, new class_884(this));
		this.method_17145(class_1438.class, new class_926(this));
		this.method_17145(class_1493.class, new class_969(this));
		this.method_17145(class_1428.class, new class_882(this));
		this.method_17145(class_3701.class, new class_3683(this));
		this.method_17145(class_1463.class, new class_939(this));
		this.method_17145(class_1453.class, new class_930(this));
		this.method_17145(class_1481.class, new class_958(this));
		this.method_17145(class_1614.class, new class_942(this));
		this.method_17145(class_1559.class, new class_896(this));
		this.method_17145(class_1548.class, new class_887(this));
		this.method_17145(class_1560.class, new class_894(this));
		this.method_17145(class_1473.class, new class_948(this));
		this.method_17145(class_1613.class, new class_946(this));
		this.method_17145(class_1639.class, new class_967(this));
		this.method_17145(class_1627.class, new class_950(this));
		this.method_17145(class_1640.class, new class_965(this));
		this.method_17145(class_1545.class, new class_878(this));
		this.method_17145(class_1590.class, new class_935(this));
		this.method_17145(class_1642.class, new class_3886(this));
		this.method_17145(class_1641.class, new class_971(this, arg3));
		this.method_17145(class_1576.class, new class_912(this));
		this.method_17145(class_1551.class, new class_890(this));
		this.method_17145(class_1621.class, new class_945(this));
		this.method_17145(class_1589.class, new class_917(this));
		this.method_17145(class_1570.class, new class_908(this, 6.0F));
		this.method_17145(class_1571.class, new class_905(this));
		this.method_17145(class_1477.class, new class_951(this));
		this.method_17145(class_1646.class, new class_963(this, arg3));
		this.method_17145(class_1439.class, new class_913(this));
		this.method_17145(class_1420.class, new class_879(this));
		this.method_17145(class_1577.class, new class_907(this));
		this.method_17145(class_1550.class, new class_893(this));
		this.method_17145(class_1606.class, new class_943(this));
		this.method_17145(class_1456.class, new class_937(this));
		this.method_17145(class_1564.class, new class_899(this));
		this.method_17145(class_1632.class, new class_962(this));
		this.method_17145(class_1604.class, new class_934(this));
		this.method_17145(class_1584.class, new class_911(this));
		this.method_17145(class_1634.class, new class_960(this));
		this.method_17145(class_1581.class, new class_914(this));
		this.method_17145(class_1593.class, new class_933(this));
		this.method_17145(class_1454.class, new class_936(this));
		this.method_17145(class_1462.class, new class_938(this));
		this.method_17145(class_1431.class, new class_885(this));
		this.method_17145(class_1474.class, new class_959(this));
		this.method_17145(class_1433.class, new class_888(this));
		this.method_17145(class_1440.class, new class_931(this));
		this.method_17145(class_1451.class, new class_929(this));
		this.method_17145(class_1510.class, new class_895(this));
		this.method_17145(class_1511.class, new class_892(this));
		this.method_17145(class_1528.class, new class_964(this));
		this.method_17145(class_1297.class, new class_886(this));
		this.method_17145(class_1534.class, new class_928(this));
		this.method_17145(class_1533.class, new class_915(this, arg2));
		this.method_17145(class_1532.class, new class_920(this));
		this.method_17145(class_1667.class, new class_954(this));
		this.method_17145(class_1679.class, new class_947(this));
		this.method_17145(class_1685.class, new class_955(this));
		this.method_17145(class_1680.class, new class_953(this, arg2));
		this.method_17145(class_1684.class, new class_953(this, arg2));
		this.method_17145(class_1672.class, new class_953(this, arg2));
		this.method_17145(class_1681.class, new class_953(this, arg2));
		this.method_17145(class_1686.class, new class_953(this, arg2));
		this.method_17145(class_1683.class, new class_953(this, arg2));
		this.method_17145(class_1671.class, new class_903(this, arg2));
		this.method_17145(class_1674.class, new class_953(this, arg2, 3.0F));
		this.method_17145(class_1677.class, new class_953(this, arg2, 0.75F));
		this.method_17145(class_1670.class, new class_891(this));
		this.method_17145(class_1687.class, new class_966(this));
		this.method_17145(class_1678.class, new class_940(this));
		this.method_17145(class_1542.class, new class_916(this, arg2));
		this.method_17145(class_1303.class, new class_902(this));
		this.method_17145(class_1541.class, new class_956(this));
		this.method_17145(class_1540.class, new class_901(this));
		this.method_17145(class_1531.class, new class_877(this));
		this.method_17145(class_1669.class, new class_900(this));
		this.method_17145(class_1701.class, new class_957(this));
		this.method_17145(class_1699.class, new class_925(this));
		this.method_17145(class_1688.class, new class_925(this));
		this.method_17145(class_1690.class, new class_881(this));
		this.method_17145(class_1536.class, new class_906(this));
		this.method_17145(class_1295.class, new class_874(this));
		this.method_17145(class_1498.class, new class_910(this));
		this.method_17145(class_1506.class, new class_961(this));
		this.method_17145(class_1507.class, new class_961(this));
		this.method_17145(class_1500.class, new class_883(this, 0.92F));
		this.method_17145(class_1495.class, new class_883(this, 0.87F));
		this.method_17145(class_1501.class, new class_921(this));
		this.method_17145(class_1673.class, new class_923(this));
		this.method_17145(class_1538.class, new class_919(this));
		this.field_4683 = new class_1007(this);
		this.field_4687.put("default", this.field_4683);
		this.field_4687.put("slim", new class_1007(this, true));
	}

	public void method_3952(double d, double e, double f) {
		this.field_4691 = d;
		this.field_4690 = e;
		this.field_4688 = f;
	}

	public <T extends class_1297, U extends class_897<T>> U method_3953(Class<? extends class_1297> class_) {
		class_897<? extends class_1297> lv = (class_897<? extends class_1297>)this.field_4696.get(class_);
		if (lv == null && class_ != class_1297.class) {
			lv = this.method_3953(class_.getSuperclass());
			this.field_4696.put(class_, lv);
		}

		return (U)lv;
	}

	@Nullable
	public <T extends class_1297, U extends class_897<T>> U method_3957(T arg) {
		if (arg instanceof class_742) {
			String string = ((class_742)arg).method_3121();
			class_1007 lv = (class_1007)this.field_4687.get(string);
			return (U)(lv != null ? lv : this.field_4683);
		} else {
			return this.method_3953(arg.getClass());
		}
	}

	public void method_3941(class_1937 arg, class_327 arg2, class_1297 arg3, class_1297 arg4, class_315 arg5, float f) {
		this.field_4684 = arg;
		this.field_4692 = arg5;
		this.field_4686 = arg3;
		this.field_4678 = arg4;
		this.field_4689 = arg2;
		if (arg3 instanceof class_1309 && ((class_1309)arg3).method_6113()) {
			class_2680 lv = arg.method_8320(new class_2338(arg3));
			class_2248 lv2 = lv.method_11614();
			if (lv2 instanceof class_2244) {
				int i = ((class_2350)lv.method_11654(class_2244.field_11177)).method_10161();
				this.field_4679 = (float)(i * 90 + 180);
				this.field_4677 = 0.0F;
			}
		} else {
			this.field_4679 = class_3532.method_16439(f, arg3.field_5982, arg3.field_6031);
			this.field_4677 = class_3532.method_16439(f, arg3.field_6004, arg3.field_5965);
		}

		if (arg5.field_1850 == 2) {
			this.field_4679 += 180.0F;
		}

		this.field_4695 = class_3532.method_16436((double)f, arg3.field_6038, arg3.field_5987);
		this.field_4694 = class_3532.method_16436((double)f, arg3.field_5971, arg3.field_6010);
		this.field_4693 = class_3532.method_16436((double)f, arg3.field_5989, arg3.field_6035);
	}

	public void method_3945(float f) {
		this.field_4679 = f;
	}

	public boolean method_3951() {
		return this.field_4681;
	}

	public void method_3948(boolean bl) {
		this.field_4681 = bl;
	}

	public void method_3955(boolean bl) {
		this.field_4680 = bl;
	}

	public boolean method_3958() {
		return this.field_4680;
	}

	public boolean method_3942(class_1297 arg) {
		return this.method_3957(arg).method_16894();
	}

	public boolean method_3950(class_1297 arg, class_856 arg2, double d, double e, double f) {
		class_897<class_1297> lv = this.method_3957(arg);
		return lv != null && lv.method_3933(arg, arg2, d, e, f);
	}

	public void method_3946(class_1297 arg, float f, boolean bl) {
		if (arg.field_6012 == 0) {
			arg.field_6038 = arg.field_5987;
			arg.field_5971 = arg.field_6010;
			arg.field_5989 = arg.field_6035;
		}

		double d = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		double e = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
		double g = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		float h = class_3532.method_16439(f, arg.field_5982, arg.field_6031);
		int i = arg.method_5635();
		if (arg.method_5809()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.method_3954(arg, d - this.field_4691, e - this.field_4690, g - this.field_4688, h, f, bl);
	}

	public void method_3954(class_1297 arg, double d, double e, double f, float g, float h, boolean bl) {
		class_897<class_1297> lv = null;

		try {
			lv = this.method_3957(arg);
			if (lv != null && this.field_4685 != null) {
				try {
					lv.method_3927(this.field_4682);
					lv.method_3936(arg, d, e, f, g, h);
				} catch (Throwable var17) {
					throw new class_148(class_128.method_560(var17, "Rendering entity in world"));
				}

				try {
					if (!this.field_4682) {
						lv.method_3939(arg, d, e, f, g, h);
					}
				} catch (Throwable var18) {
					throw new class_148(class_128.method_560(var18, "Post-rendering entity in world"));
				}

				if (this.field_4680 && !arg.method_5767() && !bl && !class_310.method_1551().method_1555()) {
					try {
						this.method_3956(arg, d, e, f, g, h);
					} catch (Throwable var16) {
						throw new class_148(class_128.method_560(var16, "Rendering entity hitbox in world"));
					}
				}
			}
		} catch (Throwable var19) {
			class_128 lv2 = class_128.method_560(var19, "Rendering entity in world");
			class_129 lv3 = lv2.method_562("Entity being rendered");
			arg.method_5819(lv3);
			class_129 lv4 = lv2.method_562("Renderer details");
			lv4.method_578("Assigned renderer", lv);
			lv4.method_578("Location", class_129.method_583(d, e, f));
			lv4.method_578("Rotation", g);
			lv4.method_578("Delta", h);
			throw new class_148(lv2);
		}
	}

	public void method_3947(class_1297 arg, float f) {
		if (arg.field_6012 == 0) {
			arg.field_6038 = arg.field_5987;
			arg.field_5971 = arg.field_6010;
			arg.field_5989 = arg.field_6035;
		}

		double d = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		double e = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
		double g = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		float h = class_3532.method_16439(f, arg.field_5982, arg.field_6031);
		int i = arg.method_5635();
		if (arg.method_5809()) {
			i = 15728880;
		}

		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		class_897<class_1297> lv = this.method_3957(arg);
		if (lv != null && this.field_4685 != null) {
			lv.method_3937(arg, d - this.field_4691, e - this.field_4690, g - this.field_4688, h, f);
		}
	}

	private void method_3956(class_1297 arg, double d, double e, double f, float g, float h) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		float i = arg.field_5998 / 2.0F;
		class_238 lv = arg.method_5829();
		class_761.method_3262(
			lv.field_1323 - arg.field_5987 + d,
			lv.field_1322 - arg.field_6010 + e,
			lv.field_1321 - arg.field_6035 + f,
			lv.field_1320 - arg.field_5987 + d,
			lv.field_1325 - arg.field_6010 + e,
			lv.field_1324 - arg.field_6035 + f,
			1.0F,
			1.0F,
			1.0F,
			1.0F
		);
		class_1297[] lvs = arg.method_5690();
		if (lvs != null) {
			for (class_1297 lv2 : lvs) {
				double j = (lv2.field_5987 - lv2.field_6014) * (double)h;
				double k = (lv2.field_6010 - lv2.field_6036) * (double)h;
				double l = (lv2.field_6035 - lv2.field_5969) * (double)h;
				class_238 lv3 = lv2.method_5829();
				class_761.method_3262(
					lv3.field_1323 - this.field_4691 + j,
					lv3.field_1322 - this.field_4690 + k,
					lv3.field_1321 - this.field_4688 + l,
					lv3.field_1320 - this.field_4691 + j,
					lv3.field_1325 - this.field_4690 + k,
					lv3.field_1324 - this.field_4688 + l,
					0.25F,
					1.0F,
					0.0F,
					1.0F
				);
			}
		}

		if (arg instanceof class_1309) {
			float m = 0.01F;
			class_761.method_3262(
				d - (double)i,
				e + (double)arg.method_5751() - 0.01F,
				f - (double)i,
				d + (double)i,
				e + (double)arg.method_5751() + 0.01F,
				f + (double)i,
				1.0F,
				0.0F,
				0.0F,
				1.0F
			);
		}

		class_289 lv4 = class_289.method_1348();
		class_287 lv5 = lv4.method_1349();
		class_243 lv6 = arg.method_5828(h);
		lv5.method_1328(3, class_290.field_1576);
		lv5.method_1315(d, e + (double)arg.method_5751(), f).method_1323(0, 0, 255, 255).method_1344();
		lv5.method_1315(d + lv6.field_1352 * 2.0, e + (double)arg.method_5751() + lv6.field_1351 * 2.0, f + lv6.field_1350 * 2.0)
			.method_1323(0, 0, 255, 255)
			.method_1344();
		lv4.method_1350();
		GlStateManager.enableTexture();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	public void method_3944(@Nullable class_1937 arg) {
		this.field_4684 = arg;
		if (arg == null) {
			this.field_4686 = null;
		}
	}

	public double method_3959(double d, double e, double f) {
		double g = d - this.field_4695;
		double h = e - this.field_4694;
		double i = f - this.field_4693;
		return g * g + h * h + i * i;
	}

	public class_327 method_3949() {
		return this.field_4689;
	}

	public void method_3943(boolean bl) {
		this.field_4682 = bl;
	}
}
