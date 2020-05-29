package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.util.Unit;

public interface class_5348 {
	Optional<Unit> field_25309 = Optional.of(Unit.INSTANCE);
	class_5348 field_25310 = new class_5348() {
		@Override
		public <T> Optional<T> visit(class_5348.Visitor<T> visitor) {
			return Optional.empty();
		}

		@Environment(EnvType.CLIENT)
		@Override
		public <T> Optional<T> visit(class_5348.StyledVisitor<T> styledVisitor, Style style) {
			return Optional.empty();
		}
	};

	<T> Optional<T> visit(class_5348.Visitor<T> visitor);

	@Environment(EnvType.CLIENT)
	<T> Optional<T> visit(class_5348.StyledVisitor<T> styledVisitor, Style style);

	static class_5348 method_29430(String string) {
		return new class_5348() {
			@Override
			public <T> Optional<T> visit(class_5348.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public <T> Optional<T> visit(class_5348.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style, string);
			}
		};
	}

	@Environment(EnvType.CLIENT)
	static class_5348 method_29431(String string, Style style) {
		return new class_5348() {
			@Override
			public <T> Optional<T> visit(class_5348.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Override
			public <T> Optional<T> visit(class_5348.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style.withParent(style), string);
			}
		};
	}

	@Environment(EnvType.CLIENT)
	static class_5348 method_29433(class_5348... args) {
		return method_29432(ImmutableList.copyOf(args));
	}

	@Environment(EnvType.CLIENT)
	static class_5348 method_29432(List<class_5348> list) {
		return new class_5348() {
			@Override
			public <T> Optional<T> visit(class_5348.Visitor<T> visitor) {
				for (class_5348 lv : list) {
					Optional<T> optional = lv.visit(visitor);
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}

			@Override
			public <T> Optional<T> visit(class_5348.StyledVisitor<T> styledVisitor, Style style) {
				for (class_5348 lv : list) {
					Optional<T> optional = lv.visit(styledVisitor, style);
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}
		};
	}

	/**
	 * A visitor for text content and a contextual {@link Style}.
	 */
	@Environment(EnvType.CLIENT)
	public interface StyledVisitor<T> {
		/**
		 * Visits a text's contextual style and {@link Text#asString() asString
		 * result}.
		 * 
		 * <p>A contextual style is obtained by calling {@link Style#withParent(Style)}
		 * on the current's text style, passing the previous contextual style or
		 * the starting style if it is the beginning of a visit.</p>
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * Text#TERMINATE_VISIT} for convenience.</p>
		 */
		Optional<T> accept(Style style, String asString);
	}

	/**
	 * A visitor for text content.
	 */
	public interface Visitor<T> {
		/**
		 * Visits a text's {@link Text#asString() asString result}.
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * Text#TERMINATE_VISIT} for convenience.</p>
		 */
		Optional<T> accept(String asString);
	}
}
