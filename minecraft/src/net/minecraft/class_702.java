package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_702 {
	private static final class_2960 field_3833 = new class_2960("textures/particle/particles.png");
	protected class_1937 field_3834;
	private final ArrayDeque<class_703>[][] field_3830 = new ArrayDeque[4][];
	private final Queue<class_733> field_3837 = Queues.<class_733>newArrayDeque();
	private final class_1060 field_3831;
	private final Random field_3832 = new Random();
	private final Int2ObjectMap<class_707<?>> field_3835 = new Int2ObjectOpenHashMap<>();
	private final Queue<class_703> field_3836 = Queues.<class_703>newArrayDeque();

	public class_702(class_1937 arg, class_1060 arg2) {
		this.field_3834 = arg;
		this.field_3831 = arg2;

		for (int i = 0; i < 4; i++) {
			this.field_3830[i] = new ArrayDeque[2];

			for (int j = 0; j < 2; j++) {
				this.field_3830[i][j] = Queues.newArrayDeque();
			}
		}

		this.method_3062();
	}

	private void method_3062() {
		this.method_3043(class_2398.field_11225, new class_711.class_712());
		this.method_3043(class_2398.field_11231, new class_684.class_685());
		this.method_3043(class_2398.field_11235, new class_651.class_652());
		this.method_3043(class_2398.field_11217, new class_727.class_728());
		this.method_3043(class_2398.field_11247, new class_653.class_654());
		this.method_3043(class_2398.field_11238, new class_655.class_656());
		this.method_3043(class_2398.field_11241, new class_661.class_662());
		this.method_3043(class_2398.field_17430, new class_3937.class_3938());
		this.method_3043(class_2398.field_17431, new class_3937.class_3939());
		this.method_3043(class_2398.field_11204, new class_704.class_705());
		this.method_3043(class_2398.field_11205, new class_657.class_660());
		this.method_3043(class_2398.field_11243, new class_736.class_737());
		this.method_3043(class_2398.field_11209, new class_657.class_658());
		this.method_3043(class_2398.field_11216, new class_666.class_667());
		this.method_3043(class_2398.field_11222, new class_729.class_730());
		this.method_3043(class_2398.field_11223, new class_663.class_664());
		this.method_3043(class_2398.field_11232, new class_663.class_665());
		this.method_3043(class_2398.field_11212, new class_671.class_672());
		this.method_3043(class_2398.field_11245, new class_711.class_715());
		this.method_3043(class_2398.field_11250, new class_700.class_701());
		this.method_3043(class_2398.field_11208, new class_657.class_659());
		this.method_3043(class_2398.field_11215, new class_668.class_670());
		this.method_3043(class_2398.field_11207, new class_675.class_676());
		this.method_3043(class_2398.field_11226, new class_711.class_714());
		this.method_3043(class_2398.field_11221, new class_689.class_690());
		this.method_3043(class_2398.field_11236, new class_691.class_692());
		this.method_3043(class_2398.field_11206, new class_682.class_683());
		this.method_3043(class_2398.field_11248, new class_677.class_679());
		this.method_3043(class_2398.field_11244, new class_738.class_739());
		this.method_3043(class_2398.field_11240, new class_687.class_688());
		this.method_3043(class_2398.field_11211, new class_729.class_731());
		this.method_3043(class_2398.field_11201, new class_684.class_686());
		this.method_3043(class_2398.field_11213, new class_711.class_713());
		this.method_3043(class_2398.field_11218, new class_647.class_648());
		this.method_3043(class_2398.field_11246, new class_647.class_649());
		this.method_3043(class_2398.field_11230, new class_647.class_650());
		this.method_3043(class_2398.field_11237, new class_696.class_697());
		this.method_3043(class_2398.field_11239, new class_694.class_695());
		this.method_3043(class_2398.field_11219, new class_729.class_732());
		this.method_3043(class_2398.field_11229, new class_668.class_669());
		this.method_3043(class_2398.field_11224, new class_698.class_699());
		this.method_3043(class_2398.field_11203, new class_673.class_674());
		this.method_3043(class_2398.field_11214, new class_709.class_710());
		this.method_3043(class_2398.field_11242, new class_740.class_741());
		this.method_3043(class_2398.field_11251, new class_717.class_718());
		this.method_3043(class_2398.field_11234, new class_704.class_706());
		this.method_3043(class_2398.field_11228, new class_721.class_722());
		this.method_3043(class_2398.field_11227, new class_645.class_646());
		this.method_3043(class_2398.field_11220, new class_734.class_735());
		this.method_3043(class_2398.field_11233, new class_725.class_726());
		this.method_3043(class_2398.field_11210, new class_723.class_724());
		this.method_3043(class_2398.field_11202, new class_719.class_720());
		this.method_3043(class_2398.field_11249, new class_711.class_716());
	}

	public <T extends class_2394> void method_3043(class_2396<T> arg, class_707<T> arg2) {
		this.field_3835.put(class_2378.field_11141.method_10249(arg), arg2);
	}

	public void method_3061(class_1297 arg, class_2394 arg2) {
		this.field_3837.add(new class_733(this.field_3834, arg, arg2));
	}

	public void method_3051(class_1297 arg, class_2394 arg2, int i) {
		this.field_3837.add(new class_733(this.field_3834, arg, arg2, i));
	}

	@Nullable
	public class_703 method_3056(class_2394 arg, double d, double e, double f, double g, double h, double i) {
		class_703 lv = this.method_3055(arg, d, e, f, g, h, i);
		if (lv != null) {
			this.method_3058(lv);
			return lv;
		} else {
			return null;
		}
	}

	@Nullable
	private <T extends class_2394> class_703 method_3055(T arg, double d, double e, double f, double g, double h, double i) {
		class_707<T> lv = (class_707<T>)this.field_3835.get(class_2378.field_11141.method_10249((class_2396<? extends class_2394>)arg.method_10295()));
		return lv == null ? null : lv.method_3090(arg, this.field_3834, d, e, f, g, h, i);
	}

	public void method_3058(class_703 arg) {
		this.field_3836.add(arg);
	}

	public void method_3057() {
		for (int i = 0; i < 4; i++) {
			this.method_3044(i);
		}

		if (!this.field_3837.isEmpty()) {
			List<class_733> list = Lists.<class_733>newArrayList();

			for (class_733 lv : this.field_3837) {
				lv.method_3070();
				if (!lv.method_3086()) {
					list.add(lv);
				}
			}

			this.field_3837.removeAll(list);
		}

		if (!this.field_3836.isEmpty()) {
			for (class_703 lv2 = (class_703)this.field_3836.poll(); lv2 != null; lv2 = (class_703)this.field_3836.poll()) {
				int j = lv2.method_3079();
				int k = lv2.method_3071() ? 0 : 1;
				if (this.field_3830[j][k].size() >= 16384) {
					this.field_3830[j][k].removeFirst();
				}

				this.field_3830[j][k].add(lv2);
			}
		}
	}

	private void method_3044(int i) {
		this.field_3834.method_16107().method_15396(String.valueOf(i));

		for (int j = 0; j < 2; j++) {
			this.field_3834.method_16107().method_15396(String.valueOf(j));
			this.method_3048(this.field_3830[i][j]);
			this.field_3834.method_16107().method_15407();
		}

		this.field_3834.method_16107().method_15407();
	}

	private void method_3048(Queue<class_703> queue) {
		if (!queue.isEmpty()) {
			Iterator<class_703> iterator = queue.iterator();

			while (iterator.hasNext()) {
				class_703 lv = (class_703)iterator.next();
				this.method_3059(lv);
				if (!lv.method_3086()) {
					iterator.remove();
				}
			}
		}
	}

	private void method_3059(class_703 arg) {
		try {
			arg.method_3070();
		} catch (Throwable var6) {
			class_128 lv = class_128.method_560(var6, "Ticking Particle");
			class_129 lv2 = lv.method_562("Particle being ticked");
			int i = arg.method_3079();
			lv2.method_577("Particle", arg::toString);
			lv2.method_577("Particle Type", () -> {
				if (i == 0) {
					return "MISC_TEXTURE";
				} else if (i == 1) {
					return "TERRAIN_TEXTURE";
				} else {
					return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
				}
			});
			throw new class_148(lv);
		}
	}

	public void method_3049(class_1297 arg, float f) {
		float g = class_295.method_1375();
		float h = class_295.method_1380();
		float i = class_295.method_1381();
		float j = class_295.method_1378();
		float k = class_295.method_1377();
		class_703.field_3873 = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		class_703.field_3853 = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
		class_703.field_3870 = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		class_703.field_3864 = arg.method_5828(f);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088);
		GlStateManager.alphaFunc(516, 0.003921569F);

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 2; m++) {
				if (!this.field_3830[l][m].isEmpty()) {
					switch (m) {
						case 0:
							GlStateManager.depthMask(false);
							break;
						case 1:
							GlStateManager.depthMask(true);
					}

					switch (l) {
						case 0:
						default:
							this.field_3831.method_4618(field_3833);
							break;
						case 1:
							this.field_3831.method_4618(class_1059.field_5275);
					}

					GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					class_289 lv = class_289.method_1348();
					class_287 lv2 = lv.method_1349();
					lv2.method_1328(7, class_290.field_1584);

					for (class_703 lv3 : this.field_3830[l][m]) {
						try {
							lv3.method_3074(lv2, arg, f, g, k, h, i, j);
						} catch (Throwable var18) {
							class_128 lv4 = class_128.method_560(var18, "Rendering Particle");
							class_129 lv5 = lv4.method_562("Particle being rendered");
							int n = l;
							lv5.method_577("Particle", lv3::toString);
							lv5.method_577("Particle Type", () -> {
								if (n == 0) {
									return "MISC_TEXTURE";
								} else if (n == 1) {
									return "TERRAIN_TEXTURE";
								} else {
									return n == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + n;
								}
							});
							throw new class_148(lv4);
						}
					}

					lv.method_1350();
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void method_3060(class_1297 arg, float f) {
		float g = class_295.method_1375();
		float h = class_295.method_1380();
		float i = class_295.method_1381();
		float j = class_295.method_1378();
		float k = class_295.method_1377();
		class_703.field_3873 = class_3532.method_16436((double)f, arg.field_6038, arg.field_5987);
		class_703.field_3853 = class_3532.method_16436((double)f, arg.field_5971, arg.field_6010);
		class_703.field_3870 = class_3532.method_16436((double)f, arg.field_5989, arg.field_6035);
		class_703.field_3864 = arg.method_5828(f);

		for (int l = 0; l < 2; l++) {
			Queue<class_703> queue = this.field_3830[3][l];
			if (!queue.isEmpty()) {
				class_289 lv = class_289.method_1348();
				class_287 lv2 = lv.method_1349();

				for (class_703 lv3 : queue) {
					lv3.method_3074(lv2, arg, f, g, k, h, i, j);
				}
			}
		}
	}

	public void method_3045(@Nullable class_1937 arg) {
		this.field_3834 = arg;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				this.field_3830[i][j].clear();
			}
		}

		this.field_3837.clear();
	}

	public void method_3046(class_2338 arg, class_2680 arg2) {
		if (!arg2.method_11588()) {
			class_265 lv = arg2.method_11606(this.field_3834, arg);
			double d = 0.25;
			lv.method_1089(
				(dx, e, f, g, h, i) -> {
					double j = Math.min(1.0, g - dx);
					double k = Math.min(1.0, h - e);
					double l = Math.min(1.0, i - f);
					int m = Math.max(2, class_3532.method_15384(j / 0.25));
					int n = Math.max(2, class_3532.method_15384(k / 0.25));
					int o = Math.max(2, class_3532.method_15384(l / 0.25));

					for (int p = 0; p < m; p++) {
						for (int q = 0; q < n; q++) {
							for (int r = 0; r < o; r++) {
								double s = ((double)p + 0.5) / (double)m;
								double t = ((double)q + 0.5) / (double)n;
								double u = ((double)r + 0.5) / (double)o;
								double v = s * j + dx;
								double w = t * k + e;
								double x = u * l + f;
								this.method_3058(
									new class_727(
											this.field_3834, (double)arg.method_10263() + v, (double)arg.method_10264() + w, (double)arg.method_10260() + x, s - 0.5, t - 0.5, u - 0.5, arg2
										)
										.method_3108(arg)
								);
							}
						}
					}
				}
			);
		}
	}

	public void method_3054(class_2338 arg, class_2350 arg2) {
		class_2680 lv = this.field_3834.method_8320(arg);
		if (lv.method_11610() != class_2464.field_11455) {
			int i = arg.method_10263();
			int j = arg.method_10264();
			int k = arg.method_10260();
			float f = 0.1F;
			class_238 lv2 = lv.method_11606(this.field_3834, arg).method_1107();
			double d = (double)i + this.field_3832.nextDouble() * (lv2.field_1320 - lv2.field_1323 - 0.2F) + 0.1F + lv2.field_1323;
			double e = (double)j + this.field_3832.nextDouble() * (lv2.field_1325 - lv2.field_1322 - 0.2F) + 0.1F + lv2.field_1322;
			double g = (double)k + this.field_3832.nextDouble() * (lv2.field_1324 - lv2.field_1321 - 0.2F) + 0.1F + lv2.field_1321;
			if (arg2 == class_2350.field_11033) {
				e = (double)j + lv2.field_1322 - 0.1F;
			}

			if (arg2 == class_2350.field_11036) {
				e = (double)j + lv2.field_1325 + 0.1F;
			}

			if (arg2 == class_2350.field_11043) {
				g = (double)k + lv2.field_1321 - 0.1F;
			}

			if (arg2 == class_2350.field_11035) {
				g = (double)k + lv2.field_1324 + 0.1F;
			}

			if (arg2 == class_2350.field_11039) {
				d = (double)i + lv2.field_1323 - 0.1F;
			}

			if (arg2 == class_2350.field_11034) {
				d = (double)i + lv2.field_1320 + 0.1F;
			}

			this.method_3058(new class_727(this.field_3834, d, e, g, 0.0, 0.0, 0.0, lv).method_3108(arg).method_3075(0.2F).method_3087(0.6F));
		}
	}

	public String method_3052() {
		int i = 0;

		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 2; k++) {
				i += this.field_3830[j][k].size();
			}
		}

		return String.valueOf(i);
	}
}
