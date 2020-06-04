package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_5369<T extends ResourcePackProfile> {
	private final List<T> field_25455;
	private final List<T> field_25456;
	private final BiConsumer<T, TextureManager> field_25457;
	private final Runnable field_25458;
	private final class_5369.class_5370<T> field_25459;

	public class_5369(
		Runnable runnable, BiConsumer<T, TextureManager> biConsumer, Collection<T> collection, Collection<T> collection2, class_5369.class_5370<T> arg
	) {
		this.field_25458 = runnable;
		this.field_25457 = biConsumer;
		this.field_25455 = Lists.<T>newArrayList(collection);
		this.field_25456 = Lists.<T>newArrayList(collection2);
		this.field_25459 = arg;
	}

	public Stream<class_5369.class_5371> method_29639() {
		return this.field_25456.stream().map(resourcePackProfile -> new class_5369.class_5374(resourcePackProfile));
	}

	public Stream<class_5369.class_5371> method_29643() {
		return this.field_25455.stream().map(resourcePackProfile -> new class_5369.class_5373(resourcePackProfile));
	}

	public void method_29642(boolean bl) {
		this.field_25459.accept(ImmutableList.copyOf(this.field_25455), ImmutableList.copyOf(this.field_25456), bl);
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface class_5370<T extends ResourcePackProfile> {
		void accept(List<T> list, List<T> list2, boolean bl);
	}

	@Environment(EnvType.CLIENT)
	public interface class_5371 {
		void method_29649(TextureManager textureManager);

		ResourcePackCompatibility method_29648();

		Text method_29650();

		Text method_29651();

		class_5352 method_29652();

		default class_5348 method_29653() {
			return this.method_29652().decorate(this.method_29651());
		}

		boolean method_29654();

		boolean method_29655();

		void method_29656();

		void method_29657();

		void method_29658();

		void method_29659();

		boolean method_29660();

		default boolean method_29661() {
			return !this.method_29660();
		}

		default boolean method_29662() {
			return this.method_29660() && !this.method_29655();
		}

		boolean method_29663();

		boolean method_29664();
	}

	@Environment(EnvType.CLIENT)
	abstract class class_5372 implements class_5369.class_5371 {
		private final T field_25461;

		public class_5372(T resourcePackProfile) {
			this.field_25461 = resourcePackProfile;
		}

		protected abstract List<T> method_29666();

		protected abstract List<T> method_29667();

		@Override
		public void method_29649(TextureManager textureManager) {
			class_5369.this.field_25457.accept(this.field_25461, textureManager);
		}

		@Override
		public ResourcePackCompatibility method_29648() {
			return this.field_25461.getCompatibility();
		}

		@Override
		public Text method_29650() {
			return this.field_25461.getDisplayName();
		}

		@Override
		public Text method_29651() {
			return this.field_25461.getDescription();
		}

		@Override
		public class_5352 method_29652() {
			return this.field_25461.method_29483();
		}

		@Override
		public boolean method_29654() {
			return this.field_25461.isPinned();
		}

		@Override
		public boolean method_29655() {
			return this.field_25461.isAlwaysEnabled();
		}

		protected void method_29668() {
			this.method_29666().remove(this.field_25461);
			this.field_25461.getInitialPosition().insert(this.method_29667(), this.field_25461, Function.identity(), true);
			class_5369.this.field_25458.run();
		}

		protected void method_29665(int i) {
			List<T> list = this.method_29666();
			int j = list.indexOf(this.field_25461);
			list.remove(j);
			list.add(j + i, this.field_25461);
			class_5369.this.field_25458.run();
		}

		@Override
		public boolean method_29663() {
			List<T> list = this.method_29666();
			int i = list.indexOf(this.field_25461);
			return i > 0 && !((ResourcePackProfile)list.get(i - 1)).isPinned();
		}

		@Override
		public void method_29658() {
			this.method_29665(-1);
		}

		@Override
		public boolean method_29664() {
			List<T> list = this.method_29666();
			int i = list.indexOf(this.field_25461);
			return i >= 0 && i < list.size() - 1 && !((ResourcePackProfile)list.get(i + 1)).isPinned();
		}

		@Override
		public void method_29659() {
			this.method_29665(1);
		}
	}

	@Environment(EnvType.CLIENT)
	class class_5373 extends class_5369<T>.class_5372 {
		public class_5373(T resourcePackProfile) {
			super(resourcePackProfile);
		}

		@Override
		protected List<T> method_29666() {
			return class_5369.this.field_25455;
		}

		@Override
		protected List<T> method_29667() {
			return class_5369.this.field_25456;
		}

		@Override
		public boolean method_29660() {
			return true;
		}

		@Override
		public void method_29656() {
		}

		@Override
		public void method_29657() {
			this.method_29668();
		}
	}

	@Environment(EnvType.CLIENT)
	class class_5374 extends class_5369<T>.class_5372 {
		public class_5374(T resourcePackProfile) {
			super(resourcePackProfile);
		}

		@Override
		protected List<T> method_29666() {
			return class_5369.this.field_25456;
		}

		@Override
		protected List<T> method_29667() {
			return class_5369.this.field_25455;
		}

		@Override
		public boolean method_29660() {
			return false;
		}

		@Override
		public void method_29656() {
			this.method_29668();
		}

		@Override
		public void method_29657() {
		}
	}
}
