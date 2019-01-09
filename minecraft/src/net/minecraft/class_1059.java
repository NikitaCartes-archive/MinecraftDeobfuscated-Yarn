package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1059 extends class_1044 implements class_1063 {
	private static final Logger field_5278 = LogManager.getLogger();
	public static final class_2960 field_5275 = new class_2960("textures/atlas/blocks.png");
	private final List<class_1058> field_5276 = Lists.<class_1058>newArrayList();
	private final Set<class_2960> field_5277 = Sets.<class_2960>newHashSet();
	private final Map<class_2960, class_1058> field_5280 = Maps.<class_2960, class_1058>newHashMap();
	private final String field_5279;
	private int field_5281;
	private final class_1058 field_5282 = class_1047.method_4541();

	public class_1059(String string) {
		this.field_5279 = string;
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
	}

	public void method_4602(class_3300 arg, Iterable<class_2960> iterable) {
		this.field_5277.clear();
		iterable.forEach(arg2 -> this.method_4613(arg, arg2));
		this.method_4610(arg);
	}

	public void method_4610(class_3300 arg) {
		int i = class_310.method_1580();
		class_1055 lv = new class_1055(i, i, 0, this.field_5281);
		this.method_4601();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.field_5281;

		for (class_2960 lv2 : this.field_5277) {
			if (!this.field_5282.method_4598().equals(lv2)) {
				class_2960 lv3 = this.method_4603(lv2);

				class_1058 lv7;
				try {
					class_3298 lv4 = arg.method_14486(lv3);
					Throwable lv9 = null;

					try {
						class_1050 lv5 = new class_1050(lv4.toString(), lv4.method_14482());
						class_1079 lv6 = lv4.method_14481(class_1079.field_5337);
						lv7 = new class_1058(lv2, lv5, lv6);
					} catch (Throwable var27) {
						lv9 = var27;
						throw var27;
					} finally {
						if (lv4 != null) {
							if (lv9 != null) {
								try {
									lv4.close();
								} catch (Throwable var26) {
									lv9.addSuppressed(var26);
								}
							} else {
								lv4.close();
							}
						}
					}
				} catch (RuntimeException var29) {
					field_5278.error("Unable to parse metadata from {} : {}", lv3, var29);
					continue;
				} catch (IOException var30) {
					field_5278.error("Using missing texture, unable to load {} : {}", lv3, var30);
					continue;
				}

				j = Math.min(j, Math.min(lv7.method_4578(), lv7.method_4595()));
				int l = Math.min(Integer.lowestOneBit(lv7.method_4578()), Integer.lowestOneBit(lv7.method_4595()));
				if (l < k) {
					field_5278.warn(
						"Texture {} with size {}x{} limits mip level from {} to {}",
						lv3,
						lv7.method_4578(),
						lv7.method_4595(),
						class_3532.method_15351(k),
						class_3532.method_15351(l)
					);
					k = l;
				}

				lv.method_4553(lv7);
			}
		}

		int m = Math.min(j, k);
		int n = class_3532.method_15351(m);
		if (n < this.field_5281) {
			field_5278.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.field_5279, this.field_5281, n, m);
			this.field_5281 = n;
		}

		this.field_5282.method_4590(this.field_5281);
		lv.method_4553(this.field_5282);

		try {
			lv.method_4557();
		} catch (class_1054 var25) {
			throw var25;
		}

		field_5278.info("Created: {}x{} {}-atlas", lv.method_4554(), lv.method_4555(), this.field_5279);
		TextureUtil.prepareImage(this.method_4624(), this.field_5281, lv.method_4554(), lv.method_4555());

		for (class_1058 lv7x : lv.method_4549()) {
			if (lv7x == this.field_5282 || this.method_4604(arg, lv7x)) {
				this.field_5280.put(lv7x.method_4598(), lv7x);

				try {
					lv7x.method_4584();
				} catch (Throwable var24) {
					class_128 lv9 = class_128.method_560(var24, "Stitching texture atlas");
					class_129 lv10 = lv9.method_562("Texture being stitched together");
					lv10.method_578("Atlas path", this.field_5279);
					lv10.method_578("Sprite", lv7x);
					throw new class_148(lv9);
				}

				if (lv7x.method_4599()) {
					this.field_5276.add(lv7x);
				}
			}
		}
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

	public void method_4613(class_3300 arg, class_2960 arg2) {
		if (arg2 == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			this.field_5277.add(arg2);
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
}
