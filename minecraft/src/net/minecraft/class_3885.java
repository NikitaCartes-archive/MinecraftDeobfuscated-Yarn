package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3885<T extends class_1309 & class_3851, M extends class_583<T> & class_3884> extends class_3887<T, M> implements class_3302 {
	private static final Int2ObjectMap<class_2960> field_17148 = class_156.method_654(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
		int2ObjectOpenHashMap.put(1, new class_2960("stone"));
		int2ObjectOpenHashMap.put(2, new class_2960("iron"));
		int2ObjectOpenHashMap.put(3, new class_2960("gold"));
		int2ObjectOpenHashMap.put(4, new class_2960("emerald"));
		int2ObjectOpenHashMap.put(5, new class_2960("diamond"));
	});
	private final Object2ObjectMap<class_3854, class_3888.class_3889> field_17149 = new Object2ObjectOpenHashMap<>();
	private final Object2ObjectMap<class_3852, class_3888.class_3889> field_17150 = new Object2ObjectOpenHashMap<>();
	private final class_3296 field_17151;
	private final String field_17152;

	public class_3885(class_3883<T, M> arg, class_3296 arg2, String string) {
		super(arg);
		this.field_17151 = arg2;
		this.field_17152 = string;
		arg2.method_14477(this);
	}

	public void method_17151(T arg, float f, float g, float h, float i, float j, float k, float l) {
		class_3850 lv = arg.method_7231();
		class_3854 lv2 = lv.method_16919();
		class_3852 lv3 = lv.method_16924();
		class_3888.class_3889 lv4 = this.method_17153(this.field_17149, "type", class_2378.field_17166, lv2);
		class_3888.class_3889 lv5 = this.method_17153(this.field_17150, "profession", class_2378.field_17167, lv3);
		M lv6 = this.method_17165();
		this.method_17164(this.method_17155("type", class_2378.field_17166.method_10221(lv2)));
		lv6.method_17150(lv5 == class_3888.class_3889.field_17160 || lv5 == class_3888.class_3889.field_17161 && lv4 != class_3888.class_3889.field_17162);
		lv6.method_2819(arg, f, g, i, j, k, l);
		lv6.method_17150(true);
		if (lv3 != class_3852.field_17051 && !arg.method_6109()) {
			this.method_17164(this.method_17155("profession", class_2378.field_17167.method_10221(lv3)));
			lv6.method_2819(arg, f, g, i, j, k, l);
			this.method_17164(this.method_17155("profession_level", field_17148.get(class_3532.method_15340(lv.method_16925(), 1, field_17148.size()))));
			lv6.method_2819(arg, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}

	private class_2960 method_17155(String string, class_2960 arg) {
		return new class_2960(arg.method_12836(), "textures/entity/" + this.field_17152 + "/" + string + "/" + arg.method_12832() + ".png");
	}

	public <K> class_3888.class_3889 method_17153(Object2ObjectMap<K, class_3888.class_3889> object2ObjectMap, String string, class_2348<K> arg, K object) {
		return (class_3888.class_3889)object2ObjectMap.computeIfAbsent(object, object2 -> {
			try {
				class_3298 lv = this.field_17151.method_14486(this.method_17155(string, arg.method_10221(object)));
				Throwable var6 = null;

				class_3888.class_3889 var8;
				try {
					class_3888 lv2 = lv.method_14481(class_3888.field_17158);
					if (lv2 == null) {
						return class_3888.class_3889.field_17160;
					}

					var8 = lv2.method_17167();
				} catch (Throwable var19) {
					var6 = var19;
					throw var19;
				} finally {
					if (lv != null) {
						if (var6 != null) {
							try {
								lv.close();
							} catch (Throwable var18) {
								var6.addSuppressed(var18);
							}
						} else {
							lv.close();
						}
					}
				}

				return var8;
			} catch (IOException var21) {
				return class_3888.class_3889.field_17160;
			}
		});
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_17150.clear();
		this.field_17149.clear();
	}
}
