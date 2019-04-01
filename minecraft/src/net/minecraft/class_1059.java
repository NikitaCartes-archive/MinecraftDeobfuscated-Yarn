package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1059 extends class_1044 implements class_1063 {
	private static final Logger field_5278 = LogManager.getLogger();
	public static final class_2960 field_5275 = new class_2960("textures/atlas/blocks.png");
	public static final class_2960 field_17898 = new class_2960("textures/atlas/particles.png");
	public static final class_2960 field_18031 = new class_2960("textures/atlas/paintings.png");
	public static final class_2960 field_18229 = new class_2960("textures/atlas/mob_effects.png");
	private final List<class_1058> field_5276 = Lists.<class_1058>newArrayList();
	private final Set<class_2960> field_5277 = Sets.<class_2960>newHashSet();
	private final Map<class_2960, class_1058> field_5280 = Maps.<class_2960, class_1058>newHashMap();
	private final String field_5279;
	private final int field_17899;
	private int field_5281;
	private final class_1058 field_5282 = class_1047.method_4541();

	public class_1059(String string) {
		this.field_5279 = string;
		this.field_17899 = class_310.method_1580();
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
	}

	public void method_18159(class_1059.class_4007 arg) {
		this.field_5277.clear();
		this.field_5277.addAll(arg.field_17900);
		field_5278.info("Created: {}x{} {}-atlas", arg.field_17901, arg.field_17902, this.field_5279);
		TextureUtil.prepareImage(this.method_4624(), this.field_5281, arg.field_17901, arg.field_17902);
		this.method_4601();

		for (class_1058 lv : arg.field_17903) {
			this.field_5280.put(lv.method_4598(), lv);

			try {
				lv.method_4584();
			} catch (Throwable var7) {
				class_128 lv2 = class_128.method_560(var7, "Stitching texture atlas");
				class_129 lv3 = lv2.method_562("Texture being stitched together");
				lv3.method_578("Atlas path", this.field_5279);
				lv3.method_578("Sprite", lv);
				throw new class_148(lv2);
			}

			if (lv.method_4599()) {
				this.field_5276.add(lv);
			}
		}
	}

	public class_1059.class_4007 method_18163(class_3300 arg, Iterable<class_2960> iterable, class_3695 arg2) {
		Set<class_2960> set = Sets.<class_2960>newHashSet();
		arg2.method_15396("preparing");
		iterable.forEach(argx -> {
			if (argx == null) {
				throw new IllegalArgumentException("Location cannot be null!");
			} else {
				set.add(argx);
			}
		});
		int i = this.field_17899;
		class_1055 lv = new class_1055(i, i, this.field_5281);
		int j = Integer.MAX_VALUE;
		int k = 1 << this.field_5281;
		arg2.method_15405("extracting_frames");

		for (class_1058 lv2 : this.method_18164(arg, set)) {
			j = Math.min(j, Math.min(lv2.method_4578(), lv2.method_4595()));
			int l = Math.min(Integer.lowestOneBit(lv2.method_4578()), Integer.lowestOneBit(lv2.method_4595()));
			if (l < k) {
				field_5278.warn(
					"Texture {} with size {}x{} limits mip level from {} to {}",
					lv2.method_4598(),
					lv2.method_4578(),
					lv2.method_4595(),
					class_3532.method_15351(k),
					class_3532.method_15351(l)
				);
				k = l;
			}

			lv.method_4553(lv2);
		}

		int m = Math.min(j, k);
		int n = class_3532.method_15351(m);
		if (n < this.field_5281) {
			field_5278.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.field_5279, this.field_5281, n, m);
			this.field_5281 = n;
		}

		arg2.method_15405("mipmapping");
		this.field_5282.method_4590(this.field_5281);
		arg2.method_15405("register");
		lv.method_4553(this.field_5282);
		arg2.method_15405("stitching");

		try {
			lv.method_4557();
		} catch (class_1054 var12) {
			throw var12;
		}

		arg2.method_15405("loading");
		List<class_1058> list = this.method_18161(arg, lv);
		arg2.method_15407();
		return new class_1059.class_4007(set, lv.method_4554(), lv.method_4555(), list);
	}

	private Collection<class_1058> method_18164(class_3300 arg, Set<class_2960> set) {
		List<CompletableFuture<?>> list = new ArrayList();
		ConcurrentLinkedQueue<class_1058> concurrentLinkedQueue = new ConcurrentLinkedQueue();

		for (class_2960 lv : set) {
			if (!this.field_5282.method_4598().equals(lv)) {
				list.add(CompletableFuture.runAsync(() -> {
					class_2960 lvx = this.method_4603(lv);

					class_1058 lv5;
					try {
						class_3298 lv2 = arg.method_14486(lvx);
						Throwable var7 = null;

						try {
							class_1050 lv3 = new class_1050(lv2.toString(), lv2.method_14482());
							class_1079 lv4 = lv2.method_14481(class_1079.field_5337);
							lv5 = new class_1058(lv, lv3, lv4);
						} catch (Throwable var19) {
							var7 = var19;
							throw var19;
						} finally {
							if (lv2 != null) {
								if (var7 != null) {
									try {
										lv2.close();
									} catch (Throwable var18) {
										var7.addSuppressed(var18);
									}
								} else {
									lv2.close();
								}
							}
						}
					} catch (RuntimeException var21) {
						field_5278.error("Unable to parse metadata from {} : {}", lvx, var21);
						return;
					} catch (IOException var22) {
						field_5278.error("Using missing texture, unable to load {} : {}", lvx, var22);
						return;
					}

					concurrentLinkedQueue.add(lv5);
				}, class_156.method_18349()));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return concurrentLinkedQueue;
	}

	private List<class_1058> method_18161(class_3300 arg, class_1055 arg2) {
		ConcurrentLinkedQueue<class_1058> concurrentLinkedQueue = new ConcurrentLinkedQueue();
		List<CompletableFuture<?>> list = new ArrayList();

		for (class_1058 lv : arg2.method_4549()) {
			if (lv == this.field_5282) {
				concurrentLinkedQueue.add(lv);
			} else {
				list.add(CompletableFuture.runAsync(() -> {
					if (this.method_4604(arg, lv)) {
						concurrentLinkedQueue.add(lv);
					}
				}, class_156.method_18349()));
			}
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		return new ArrayList(concurrentLinkedQueue);
	}

	private boolean method_4604(class_3300 arg, class_1058 arg2) {
		class_2960 lv = this.method_4603(arg2.method_4598());
		class_3298 lv2 = null;

		label45: {
			boolean lv3;
			try {
				lv2 = arg.method_14486(lv);
				arg2.method_4576(lv2, this.field_5281 + 1);
				break label45;
			} catch (RuntimeException var13) {
				field_5278.error("Unable to parse metadata from {}", lv, var13);
				return false;
			} catch (IOException var14) {
				field_5278.error("Using missing texture, unable to load {}", lv, var14);
				lv3 = false;
			} finally {
				IOUtils.closeQuietly(lv2);
			}

			return lv3;
		}

		try {
			arg2.method_4590(this.field_5281);
			return true;
		} catch (Throwable var12) {
			class_128 lv3 = class_128.method_560(var12, "Applying mipmap");
			class_129 lv4 = lv3.method_562("Sprite being mipmapped");
			lv4.method_577("Sprite name", () -> arg2.method_4598().toString());
			lv4.method_577("Sprite size", () -> arg2.method_4578() + " x " + arg2.method_4595());
			lv4.method_577("Sprite frames", () -> arg2.method_4592() + " frames");
			lv4.method_578("Mipmap levels", this.field_5281);
			throw new class_148(lv3);
		}
	}

	private class_2960 method_4603(class_2960 arg) {
		return new class_2960(arg.method_12836(), String.format("%s/%s%s", this.field_5279, arg.method_12832(), ".png"));
	}

	public class_1058 method_4607(String string) {
		return this.method_4608(new class_2960(string));
	}

	public void method_4612() {
		this.method_4623();

		for (class_1058 lv : this.field_5276) {
			lv.method_4597();
		}
	}

	@Override
	public void method_4622() {
		this.method_4612();
	}

	public void method_4605(int i) {
		this.field_5281 = i;
	}

	public class_1058 method_4608(class_2960 arg) {
		class_1058 lv = (class_1058)this.field_5280.get(arg);
		return lv == null ? this.field_5282 : lv;
	}

	public void method_4601() {
		for (class_1058 lv : this.field_5280.values()) {
			lv.method_4588();
		}

		this.field_5280.clear();
		this.field_5276.clear();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4007 {
		final Set<class_2960> field_17900;
		final int field_17901;
		final int field_17902;
		final List<class_1058> field_17903;

		public class_4007(Set<class_2960> set, int i, int j, List<class_1058> list) {
			this.field_17900 = set;
			this.field_17901 = i;
			this.field_17902 = j;
			this.field_17903 = list;
		}
	}
}
