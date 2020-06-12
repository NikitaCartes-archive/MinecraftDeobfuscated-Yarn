package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

/**
 * An object that can supply strings to a visitor, with or without a style
 * context, for rendering the strings.
 */
public interface StringRenderable {
	/**
	 * Convenience object indicating the termination of a string visit.
	 */
	Optional<Unit> TERMINATE_VISIT = Optional.of(Unit.INSTANCE);
	/**
	 * An empty renderable that does not call the visitors.
	 */
	StringRenderable EMPTY = new StringRenderable() {
		@Override
		public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
			return Optional.empty();
		}

		@Environment(EnvType.CLIENT)
		@Override
		public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
			return Optional.empty();
		}
	};

	/**
	 * Supplies this renderable's literal content to the visitor.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * 
	 * @param visitor the visitor
	 */
	<T> Optional<T> visit(StringRenderable.Visitor<T> visitor);

	/**
	 * Supplies this renderable's literal content and contextual style to
	 * the visitor.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * 
	 * @param styledVisitor the visitor
	 * @param style the contextual style
	 */
	@Environment(EnvType.CLIENT)
	<T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style);

	/**
	 * Creates a renderable from a plain string.
	 * 
	 * @param string the plain string
	 */
	static StringRenderable plain(String string) {
		return new StringRenderable() {
			@Override
			public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Environment(EnvType.CLIENT)
			@Override
			public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style, string);
			}
		};
	}

	/**
	 * Creates a visitable from a plain string and a root style.
	 * 
	 * @param string the plain string
	 * @param style the root style
	 */
	@Environment(EnvType.CLIENT)
	static StringRenderable styled(String string, Style style) {
		return new StringRenderable() {
			@Override
			public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Override
			public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style.withParent(style), string);
			}
		};
	}

	/**
	 * Concats multiple string renderables by the order they appear in the array.
	 * 
	 * @param visitables an array or varargs of visitables
	 */
	@Environment(EnvType.CLIENT)
	static StringRenderable concat(StringRenderable... visitables) {
		return concat(ImmutableList.copyOf(visitables));
	}

	/**
	 * Concats multiple string renderables by the order they appear in the list.
	 * 
	 * @param visitables a list of visitables
	 */
	@Environment(EnvType.CLIENT)
	static StringRenderable concat(List<StringRenderable> visitables) {
		return new StringRenderable() {
			@Override
			public <T> Optional<T> visit(StringRenderable.Visitor<T> visitor) {
				for (StringRenderable stringRenderable : visitables) {
					Optional<T> optional = stringRenderable.visit(visitor);
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}

			@Override
			public <T> Optional<T> visit(StringRenderable.StyledVisitor<T> styledVisitor, Style style) {
				for (StringRenderable stringRenderable : visitables) {
					Optional<T> optional = stringRenderable.visit(styledVisitor, style);
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}
		};
	}

	default String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.visit(string -> {
			stringBuilder.append(string);
			return Optional.empty();
		});
		return stringBuilder.toString();
	}

	/**
	 * A visitor for rendered string content and a contextual {@link Style}.
	 */
	@Environment(EnvType.CLIENT)
	public interface StyledVisitor<T> {
		/**
		 * Visits a string's content with a contextual style.
		 * 
		 * <p>A contextual style is obtained by calling {@link Style#withParent(Style)}
		 * on the current's text style, passing the previous contextual style or
		 * the starting style if it is the beginning of a visit.</p>
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * StringRenderable#TERMINATE_VISIT} for convenience.</p>
		 * 
		 * @return {@code Optional.empty()} to continue, a non-empty result to terminate
		 * 
		 * @param style the current style
		 * @param asString the literal string
		 */
		Optional<T> accept(Style style, String asString);
	}

	/**
	 * A visitor for rendered string content.
	 */
	public interface Visitor<T> {
		/**
		 * Visits a literal string.
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * StringRenderable#TERMINATE_VISIT} for convenience.</p>
		 * 
		 * @return {@code Optional.empty()} to continue, a non-empty result to terminate
		 * 
		 * @param asString the literal string
		 */
		Optional<T> accept(String asString);
	}
}
