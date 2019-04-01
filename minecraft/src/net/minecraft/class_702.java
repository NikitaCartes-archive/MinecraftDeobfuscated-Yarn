package net.minecraft;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_702 implements class_3302 {
	private static final List<class_3999> field_17820 = ImmutableList.of(
		class_3999.field_17827, class_3999.field_17828, class_3999.field_17830, class_3999.field_17829, class_3999.field_17831
	);
	protected class_1937 field_3834;
	private final Map<class_3999, Queue<class_703>> field_3830 = Maps.<class_3999, Queue<class_703>>newIdentityHashMap();
	private final Queue<class_733> field_3837 = Queues.<class_733>newArrayDeque();
	private final class_1060 field_3831;
	private final Random field_3832 = new Random();
	private final Int2ObjectMap<class_707<?>> field_3835 = new Int2ObjectOpenHashMap<>();
	private final Queue<class_703> field_3836 = Queues.<class_703>newArrayDeque();
	private final Map<class_2960, class_702.class_4090> field_18300 = Maps.<class_2960, class_702.class_4090>newHashMap();
	private final class_1059 field_18301 = new class_1059("textures/particle");

	public class_702(class_1937 arg, class_1060 arg2) {
		arg2.method_4620(class_1059.field_17898, this.field_18301);
		this.field_3834 = arg;
		this.field_3831 = arg2;
		this.method_3062();
	}

	private void method_3062() {
		this.method_18834(class_2398.field_11225, class_711.class_712::new);
		this.method_18834(class_2398.field_11231, class_684.class_685::new);
		this.method_3043(class_2398.field_11235, new class_651.class_652());
		this.method_3043(class_2398.field_11217, new class_727.class_728());
		this.method_18834(class_2398.field_11247, class_653.class_654::new);
		this.method_18834(class_2398.field_11238, class_655.class_656::new);
		this.method_18834(class_2398.field_11241, class_661.class_662::new);
		this.method_18834(class_2398.field_17430, class_3937.class_3938::new);
		this.method_18834(class_2398.field_17431, class_3937.class_3995::new);
		this.method_18834(class_2398.field_11204, class_704.class_705::new);
		this.method_18834(class_2398.field_17741, class_729.class_3991::new);
		this.method_18834(class_2398.field_11205, class_657.class_3939::new);
		this.method_18834(class_2398.field_11243, class_736.class_737::new);
		this.method_18834(class_2398.field_11209, class_657.class_658::new);
		this.method_18834(class_2398.field_11216, class_666.class_667::new);
		this.method_18834(class_2398.field_11222, class_729.class_730::new);
		this.method_18834(class_2398.field_11223, class_663.class_664::new);
		this.method_18834(class_2398.field_18304, class_663.class_4086::new);
		this.method_18834(class_2398.field_18305, class_663.class_4087::new);
		this.method_18834(class_2398.field_11232, class_663.class_4088::new);
		this.method_18834(class_2398.field_18306, class_663.class_665::new);
		this.method_18834(class_2398.field_11212, class_671.class_672::new);
		this.method_18834(class_2398.field_11245, class_711.class_713::new);
		this.method_3043(class_2398.field_11250, new class_700.class_701());
		this.method_18834(class_2398.field_11208, class_657.class_659::new);
		this.method_18834(class_2398.field_11215, class_668.class_670::new);
		this.method_18834(class_2398.field_11207, class_675.class_676::new);
		this.method_18834(class_2398.field_11226, class_711.class_714::new);
		this.method_3043(class_2398.field_11221, new class_689.class_690());
		this.method_18834(class_2398.field_11236, class_691.class_692::new);
		this.method_18834(class_2398.field_11206, class_682.class_683::new);
		this.method_18834(class_2398.field_11248, class_677.class_679::new);
		this.method_18834(class_2398.field_11244, class_738.class_739::new);
		this.method_18834(class_2398.field_11240, class_687.class_688::new);
		this.method_18834(class_2398.field_17909, class_677.class_3997::new);
		this.method_18834(class_2398.field_11211, class_729.class_731::new);
		this.method_18834(class_2398.field_11201, class_684.class_686::new);
		this.method_18834(class_2398.field_11213, class_711.class_715::new);
		this.method_3043(class_2398.field_11218, new class_647.class_648());
		this.method_3043(class_2398.field_11246, new class_647.class_649());
		this.method_3043(class_2398.field_11230, new class_647.class_650());
		this.method_18834(class_2398.field_11237, class_696.class_697::new);
		this.method_18834(class_2398.field_11239, class_694.class_695::new);
		this.method_18834(class_2398.field_11219, class_729.class_732::new);
		this.method_18834(class_2398.field_11229, class_668.class_669::new);
		this.method_18834(class_2398.field_11224, class_698.class_699::new);
		this.method_18834(class_2398.field_11203, class_673.class_674::new);
		this.method_18834(class_2398.field_11214, class_709.class_710::new);
		this.method_18834(class_2398.field_11242, class_740.class_741::new);
		this.method_18834(class_2398.field_11251, class_717.class_718::new);
		this.method_18834(class_2398.field_11234, class_704.class_706::new);
		this.method_18834(class_2398.field_11228, class_721.class_722::new);
		this.method_18834(class_2398.field_11227, class_645.class_646::new);
		this.method_18834(class_2398.field_11220, class_734.class_735::new);
		this.method_18834(class_2398.field_11233, class_725.class_726::new);
		this.method_18834(class_2398.field_11210, class_723.class_724::new);
		this.method_18834(class_2398.field_11202, class_719.class_720::new);
		this.method_18834(class_2398.field_11249, class_711.class_716::new);
	}

	private <T extends class_2394> void method_3043(class_2396<T> arg, class_707<T> arg2) {
		this.field_3835.put(class_2378.field_11141.method_10249(arg), arg2);
	}

	private <T extends class_2394> void method_18834(class_2396<T> arg, class_702.class_4091<T> arg2) {
		class_702.class_4090 lv = new class_702.class_4090();
		this.field_18300.put(class_2378.field_11141.method_10221(arg), lv);
		this.field_3835.put(class_2378.field_11141.method_10249(arg), arg2.create(lv));
	}

	@Override
	public CompletableFuture<Void> reload(class_3302.class_4045 arg, class_3300 arg2, class_3695 arg3, class_3695 arg4, Executor executor, Executor executor2) {
		Map<class_2960, List<class_2960>> map = Maps.<class_2960, List<class_2960>>newConcurrentMap();
		CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])class_2378.field_11141
			.method_10235()
			.stream()
			.map(arg2x -> CompletableFuture.runAsync(() -> this.method_18836(arg2, arg2x, map), executor))
			.toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(completableFutures)
			.thenApplyAsync(void_ -> {
				arg3.method_16065();
				arg3.method_15396("stitching");
				Set<class_2960> set = (Set<class_2960>)map.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
				class_1059.class_4007 lv = this.field_18301.method_18163(arg2, set, arg3);
				arg3.method_15407();
				arg3.method_16066();
				return lv;
			}, executor)
			.thenCompose(arg::method_18352)
			.thenAcceptAsync(
				arg2x -> {
					arg4.method_16065();
					arg4.method_15396("upload");
					this.field_18301.method_18159(arg2x);
					arg4.method_15405("bindSpriteSets");
					class_1058 lv = this.field_18301.method_4608(class_1047.method_4539());
					map.forEach(
						(arg2xx, list) -> {
							ImmutableList<class_1058> immutableList = list.isEmpty()
								? ImmutableList.of(lv)
								: (ImmutableList)list.stream().map(this.field_18301::method_4608).collect(ImmutableList.toImmutableList());
							((class_702.class_4090)this.field_18300.get(arg2xx)).method_18838(immutableList);
						}
					);
					arg4.method_15407();
					arg4.method_16066();
				},
				executor2
			);
	}

	public void method_18829() {
		this.field_18301.method_4601();
	}

	private void method_18836(class_3300 arg, class_2960 arg2, Map<class_2960, List<class_2960>> map) {
		class_2960 lv = new class_2960(arg2.method_12836(), "particles/" + arg2.method_12832() + ".json");

		try {
			class_3298 lv2 = arg.method_14486(lv);
			Throwable var6 = null;

			try {
				Reader reader = new InputStreamReader(lv2.method_14482(), Charsets.UTF_8);
				Throwable var8 = null;

				try {
					class_4089 lv3 = class_4089.method_18828(class_3518.method_15255(reader));
					List<class_2960> list = lv3.method_18826();
					boolean bl = this.field_18300.containsKey(arg2);
					if (list == null) {
						if (bl) {
							throw new IllegalStateException("Missing texture list for particle " + arg2);
						}
					} else {
						if (!bl) {
							throw new IllegalStateException("Redundant texture list for particle " + arg2);
						}

						map.put(arg2, list);
					}
				} catch (Throwable var35) {
					var8 = var35;
					throw var35;
				} finally {
					if (reader != null) {
						if (var8 != null) {
							try {
								reader.close();
							} catch (Throwable var34) {
								var8.addSuppressed(var34);
							}
						} else {
							reader.close();
						}
					}
				}
			} catch (Throwable var37) {
				var6 = var37;
				throw var37;
			} finally {
				if (lv2 != null) {
					if (var6 != null) {
						try {
							lv2.close();
						} catch (Throwable var33) {
							var6.addSuppressed(var33);
						}
					} else {
						lv2.close();
					}
				}
			}
		} catch (IOException var39) {
			throw new IllegalStateException("Failed to load description for particle " + arg2, var39);
		}
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
		this.field_3830.forEach((arg, queue) -> {
			this.field_3834.method_16107().method_15396(arg.toString());
			this.method_3048(queue);
			this.field_3834.method_16107().method_15407();
		});
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

		class_703 lv2;
		if (!this.field_3836.isEmpty()) {
			while ((lv2 = (class_703)this.field_3836.poll()) != null) {
				((Queue)this.field_3830.computeIfAbsent(lv2.method_18122(), arg -> EvictingQueue.create(16384))).add(lv2);
			}
		}
	}

	private void method_3048(Collection<class_703> collection) {
		if (!collection.isEmpty()) {
			Iterator<class_703> iterator = collection.iterator();

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
		} catch (Throwable var5) {
			class_128 lv = class_128.method_560(var5, "Ticking Particle");
			class_129 lv2 = lv.method_562("Particle being ticked");
			lv2.method_577("Particle", arg::toString);
			lv2.method_577("Particle Type", arg.method_18122()::toString);
			throw new class_148(lv);
		}
	}

	public void method_3049(class_4184 arg, float f) {
		float g = class_3532.method_15362(arg.method_19330() * (float) (Math.PI / 180.0));
		float h = class_3532.method_15374(arg.method_19330() * (float) (Math.PI / 180.0));
		float i = -h * class_3532.method_15374(arg.method_19329() * (float) (Math.PI / 180.0));
		float j = g * class_3532.method_15374(arg.method_19329() * (float) (Math.PI / 180.0));
		float k = class_3532.method_15362(arg.method_19329() * (float) (Math.PI / 180.0));
		class_703.field_3873 = arg.method_19326().field_1352;
		class_703.field_3853 = arg.method_19326().field_1351;
		class_703.field_3870 = arg.method_19326().field_1350;

		for (class_3999 lv : field_17820) {
			Iterable<class_703> iterable = (Iterable<class_703>)this.field_3830.get(lv);
			if (iterable != null) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				class_289 lv2 = class_289.method_1348();
				class_287 lv3 = lv2.method_1349();
				lv.method_18130(lv3, this.field_3831);

				for (class_703 lv4 : iterable) {
					try {
						lv4.method_3074(lv3, arg, f, g, k, h, i, j);
					} catch (Throwable var18) {
						class_128 lv5 = class_128.method_560(var18, "Rendering Particle");
						class_129 lv6 = lv5.method_562("Particle being rendered");
						lv6.method_577("Particle", lv4::toString);
						lv6.method_577("Particle Type", lv::toString);
						throw new class_148(lv5);
					}
				}

				lv.method_18131(lv2);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(516, 0.1F);
	}

	public void method_3045(@Nullable class_1937 arg) {
		this.field_3834 = arg;
		this.field_3830.clear();
		this.field_3837.clear();
	}

	public void method_3046(class_2338 arg, class_2680 arg2) {
		if (!arg2.method_11588()) {
			class_265 lv = arg2.method_17770(this.field_3834, arg);
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
			class_238 lv2 = lv.method_17770(this.field_3834, arg).method_1107();
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
		return String.valueOf(this.field_3830.values().stream().mapToInt(Collection::size).sum());
	}

	@Environment(EnvType.CLIENT)
	class class_4090 implements class_4002 {
		private List<class_1058> field_18303;

		private class_4090() {
		}

		@Override
		public class_1058 method_18138(int i, int j) {
			return (class_1058)this.field_18303.get(i * (this.field_18303.size() - 1) / j);
		}

		@Override
		public class_1058 method_18139(Random random) {
			return (class_1058)this.field_18303.get(random.nextInt(this.field_18303.size()));
		}

		public void method_18838(List<class_1058> list) {
			this.field_18303 = ImmutableList.copyOf(list);
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	interface class_4091<T extends class_2394> {
		class_707<T> create(class_4002 arg);
	}
}
