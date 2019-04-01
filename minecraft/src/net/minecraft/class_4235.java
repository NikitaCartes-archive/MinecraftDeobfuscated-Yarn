package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4235 {
	private final Set<class_4235.class_4236> field_18937 = Sets.newIdentityHashSet();
	private final class_4225 field_18938;
	private final Executor field_18939;

	public class_4235(class_4225 arg, Executor executor) {
		this.field_18938 = arg;
		this.field_18939 = executor;
	}

	public class_4235.class_4236 method_19723(class_4225.class_4105 arg) {
		class_4235.class_4236 lv = new class_4235.class_4236();
		this.field_18939.execute(() -> {
			class_4224 lvx = this.field_18938.method_19663(arg);
			if (lvx != null) {
				lv.field_18941 = lvx;
				this.field_18937.add(lv);
			}
		});
		return lv;
	}

	public void method_19727(Consumer<Stream<class_4224>> consumer) {
		this.field_18939.execute(() -> consumer.accept(this.field_18937.stream().map(arg -> arg.field_18941).filter(Objects::nonNull)));
	}

	public void method_19722() {
		this.field_18939.execute(() -> {
			Iterator<class_4235.class_4236> iterator = this.field_18937.iterator();

			while (iterator.hasNext()) {
				class_4235.class_4236 lv = (class_4235.class_4236)iterator.next();
				lv.field_18941.method_19658();
				if (lv.field_18941.method_19656()) {
					lv.method_19736();
					iterator.remove();
				}
			}
		});
	}

	public void method_19728() {
		this.field_18937.forEach(class_4235.class_4236::method_19736);
		this.field_18937.clear();
	}

	@Environment(EnvType.CLIENT)
	public class class_4236 {
		private class_4224 field_18941;
		private boolean field_18942;

		public boolean method_19732() {
			return this.field_18942;
		}

		public void method_19735(Consumer<class_4224> consumer) {
			class_4235.this.field_18939.execute(() -> {
				if (this.field_18941 != null) {
					consumer.accept(this.field_18941);
				}
			});
		}

		public void method_19736() {
			this.field_18942 = true;
			class_4235.this.field_18938.method_19662(this.field_18941);
			this.field_18941 = null;
		}
	}
}
