package net.minecraft;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_869 implements class_863.class_864 {
	private final class_310 field_4622;
	private final Map<Long, Map<class_2338, Integer>> field_4623 = Maps.newTreeMap(Ordering.natural().reverse());

	class_869(class_310 arg) {
		this.field_4622 = arg;
	}

	public void method_3870(long l, class_2338 arg) {
		Map<class_2338, Integer> map = (Map<class_2338, Integer>)this.field_4623.get(l);
		if (map == null) {
			map = Maps.<class_2338, Integer>newHashMap();
			this.field_4623.put(l, map);
		}

		Integer integer = (Integer)map.get(arg);
		if (integer == null) {
			integer = 0;
		}

		map.put(arg, integer + 1);
	}

	@Override
	public void method_3715(float f, long l) {
		long m = this.field_4622.field_1687.method_8510();
		class_1657 lv = this.field_4622.field_1724;
		double d = class_3532.method_16436((double)f, lv.field_6038, lv.field_5987);
		double e = class_3532.method_16436((double)f, lv.field_5971, lv.field_6010);
		double g = class_3532.method_16436((double)f, lv.field_5989, lv.field_6035);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.class_1033.field_5138, GlStateManager.class_1027.field_5088, GlStateManager.class_1033.field_5140, GlStateManager.class_1027.field_5084
		);
		GlStateManager.lineWidth(2.0F);
		GlStateManager.disableTexture();
		GlStateManager.depthMask(false);
		int i = 200;
		double h = 0.0025;
		Set<class_2338> set = Sets.<class_2338>newHashSet();
		Map<class_2338, Integer> map = Maps.<class_2338, Integer>newHashMap();
		Iterator<Entry<Long, Map<class_2338, Integer>>> iterator = this.field_4623.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Long, Map<class_2338, Integer>> entry = (Entry<Long, Map<class_2338, Integer>>)iterator.next();
			Long long_ = (Long)entry.getKey();
			Map<class_2338, Integer> map2 = (Map<class_2338, Integer>)entry.getValue();
			long n = m - long_;
			if (n > 200L) {
				iterator.remove();
			} else {
				for (Entry<class_2338, Integer> entry2 : map2.entrySet()) {
					class_2338 lv2 = (class_2338)entry2.getKey();
					Integer integer = (Integer)entry2.getValue();
					if (set.add(lv2)) {
						class_761.method_3260(
							new class_238(class_2338.field_10980)
								.method_1014(0.002)
								.method_1011(0.0025 * (double)n)
								.method_989((double)lv2.method_10263(), (double)lv2.method_10264(), (double)lv2.method_10260())
								.method_989(-d, -e, -g),
							1.0F,
							1.0F,
							1.0F,
							1.0F
						);
						map.put(lv2, integer);
					}
				}
			}
		}

		for (Entry<class_2338, Integer> entry : map.entrySet()) {
			class_2338 lv3 = (class_2338)entry.getKey();
			Integer integer2 = (Integer)entry.getValue();
			class_863.method_3711(String.valueOf(integer2), lv3.method_10263(), lv3.method_10264(), lv3.method_10260(), f, -1);
		}

		GlStateManager.depthMask(true);
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
