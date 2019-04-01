package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4207 implements class_863.class_864 {
	private static final Logger field_18920 = LogManager.getLogger();
	private final class_310 field_18786;
	private final Map<class_2338, class_4207.class_4233> field_18787 = Maps.<class_2338, class_4207.class_4233>newHashMap();
	private final Set<class_4076> field_18788 = Sets.<class_4076>newHashSet();
	private final Map<UUID, class_4207.class_4232> field_18921 = Maps.<UUID, class_4207.class_4232>newHashMap();
	private UUID field_18922;

	public class_4207(class_310 arg) {
		this.field_18786 = arg;
	}

	public void method_19701(class_4207.class_4233 arg) {
		this.field_18787.put(arg.field_18931, arg);
	}

	public void method_19434(class_2338 arg) {
		this.field_18787.remove(arg);
	}

	public void method_19702(class_2338 arg, int i) {
		class_4207.class_4233 lv = (class_4207.class_4233)this.field_18787.get(arg);
		if (lv == null) {
			field_18920.warn("Strange, setFreeTicketCount was called for an unknown POI: " + arg);
		} else {
			lv.field_18933 = i;
		}
	}

	public void method_19433(class_4076 arg) {
		this.field_18788.add(arg);
	}

	public void method_19435(class_4076 arg) {
		this.field_18788.remove(arg);
	}

	public void method_19432(class_4207.class_4232 arg) {
		this.field_18921.put(arg.field_18923, arg);
	}

	@Override
	public void method_3715(long l) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(
			GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
		);
		GlStateManager.disableTexture();
		this.method_19699();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		this.method_19710();
	}

	private void method_19699() {
		class_2338 lv = this.method_19706().method_19328();

		for (class_2338 lv2 : this.field_18787.keySet()) {
			if (lv.method_19771(lv2, 160.0)) {
				method_19709(lv2);
			}
		}

		for (class_4076 lv3 : this.field_18788) {
			if (lv.method_19771(lv3.method_19768(), 160.0)) {
				method_19714(lv3);
			}
		}

		for (class_4207.class_4232 lv4 : this.field_18921.values()) {
			if (this.method_19715(lv4)) {
				this.method_19707(lv4);
			}
		}

		for (class_4207.class_4233 lv5 : this.field_18787.values()) {
			if (lv.method_19771(lv5.field_18931, 160.0)) {
				this.method_19708(lv5);
			}
		}
	}

	private static void method_19714(class_4076 arg) {
		float f = 1.0F;
		class_2338 lv = arg.method_19768();
		class_2338 lv2 = lv.method_10080(-1.0, -1.0, -1.0);
		class_2338 lv3 = lv.method_10080(1.0, 1.0, 1.0);
		class_863.method_19697(lv2, lv3, 0.2F, 1.0F, 0.2F, 0.15F);
	}

	private static void method_19709(class_2338 arg) {
		float f = 0.05F;
		class_863.method_19696(arg, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
	}

	private void method_19708(class_4207.class_4233 arg) {
		int i = 0;
		method_19705("Ticket holders: " + this.method_19712(arg), arg, i, -256);
		method_19705("Free tickets: " + arg.field_18933, arg, ++i, -256);
		method_19705(arg.field_18932, arg, ++i, -1);
	}

	private void method_19707(class_4207.class_4232 arg) {
		int i = 0;
		method_19704(arg.field_18926, i, arg.field_18925, -1, 0.03F);
		i++;

		for (String string : arg.field_18928) {
			method_19704(arg.field_18926, i, string, -16711681, 0.02F);
			i++;
		}

		for (String string : arg.field_18927) {
			method_19704(arg.field_18926, i, string, -16711936, 0.02F);
			i++;
		}

		if (this.method_19711(arg)) {
			for (String string : Lists.reverse(arg.field_18929)) {
				method_19704(arg.field_18926, i, string, -3355444, 0.02F);
				i++;
			}
		}
	}

	private static void method_19705(String string, class_4207.class_4233 arg, int i, int j) {
		double d = 1.3;
		double e = 0.2;
		double f = (double)arg.field_18931.method_10263() + 0.5;
		double g = (double)arg.field_18931.method_10264() + 1.3 + (double)i * 0.2;
		double h = (double)arg.field_18931.method_10260() + 0.5;
		class_863.method_3712(string, f, g, h, j, 0.02F, true, 0.0F, true);
	}

	private static void method_19704(class_2374 arg, int i, String string, int j, float f) {
		double d = 2.4;
		double e = 0.25;
		class_2338 lv = new class_2338(arg);
		double g = (double)lv.method_10263() + 0.5;
		double h = arg.method_10214() + 2.4 + (double)i * 0.25;
		double k = (double)lv.method_10260() + 0.5;
		float l = 0.5F;
		class_863.method_3712(string, g, h, k, j, f, false, 0.5F, true);
	}

	private class_4184 method_19706() {
		return this.field_18786.field_1773.method_19418();
	}

	private Set<String> method_19712(class_4207.class_4233 arg) {
		return (Set<String>)this.method_19713(arg.field_18931).stream().map(class_4240::method_19780).collect(Collectors.toSet());
	}

	private boolean method_19711(class_4207.class_4232 arg) {
		return Objects.equals(this.field_18922, arg.field_18923);
	}

	private boolean method_19715(class_4207.class_4232 arg) {
		class_1657 lv = this.field_18786.field_1724;
		class_2338 lv2 = new class_2338(lv.field_5987, 0.0, lv.field_6035);
		class_2338 lv3 = new class_2338(arg.field_18926);
		return lv2.method_19771(lv3, 160.0);
	}

	private Collection<UUID> method_19713(class_2338 arg) {
		return (Collection<UUID>)this.field_18921
			.values()
			.stream()
			.filter(arg2 -> arg2.method_19718(arg))
			.map(class_4207.class_4232::method_19716)
			.collect(Collectors.toSet());
	}

	private void method_19710() {
		class_863.method_19694(this.field_18786.method_1560(), 16).ifPresent(arg -> this.field_18922 = arg.method_5667());
	}

	@Environment(EnvType.CLIENT)
	public static class class_4232 {
		public final UUID field_18923;
		public final int field_18924;
		public final String field_18925;
		public final class_2374 field_18926;
		public final List<String> field_18927 = Lists.<String>newArrayList();
		public final List<String> field_18928 = Lists.<String>newArrayList();
		public final List<String> field_18929 = Lists.<String>newArrayList();
		public final Set<class_2338> field_18930 = Sets.<class_2338>newHashSet();

		public class_4232(UUID uUID, int i, String string, class_2374 arg) {
			this.field_18923 = uUID;
			this.field_18924 = i;
			this.field_18925 = string;
			this.field_18926 = arg;
		}

		private boolean method_19718(class_2338 arg) {
			return this.field_18930.stream().anyMatch(arg::equals);
		}

		public UUID method_19716() {
			return this.field_18923;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4233 {
		public final class_2338 field_18931;
		public String field_18932;
		public int field_18933;

		public class_4233(class_2338 arg, String string, int i) {
			this.field_18931 = arg;
			this.field_18932 = string;
			this.field_18933 = i;
		}
	}
}
