package net.minecraft;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.compress.utils.Lists;

@Environment(EnvType.CLIENT)
public record class_7345(float lengthInSeconds, boolean looping, Map<String, List<class_7340>> boneAnimations) {
	@Environment(EnvType.CLIENT)
	public static class class_7346 {
		private final float field_38624;
		private final Map<String, List<class_7340>> field_38625 = Maps.<String, List<class_7340>>newHashMap();
		private boolean field_38626;

		public static class_7345.class_7346 method_42967(float f) {
			return new class_7345.class_7346(f);
		}

		private class_7346(float f) {
			this.field_38624 = f;
		}

		public class_7345.class_7346 method_42966() {
			this.field_38626 = true;
			return this;
		}

		public class_7345.class_7346 method_42969(String string, class_7340 arg) {
			((List)this.field_38625.computeIfAbsent(string, stringx -> Lists.newArrayList())).add(arg);
			return this;
		}

		public class_7345 method_42970() {
			return new class_7345(this.field_38624, this.field_38626, this.field_38625);
		}
	}
}
