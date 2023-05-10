package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

/**
 * An object that can supply strings to a visitor,
 * with or without a style context.
 */
public interface StringVisitable {
	/**
	 * Convenience object indicating the termination of a string visit.
	 */
	Optional<Unit> TERMINATE_VISIT = Optional.of(Unit.INSTANCE);
	/**
	 * An empty visitable that does not call the visitors.
	 */
	StringVisitable EMPTY = new StringVisitable() {
		@Override
		public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
			return Optional.empty();
		}

		@Override
		public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
			return Optional.empty();
		}
	};

	/**
	 * Supplies this visitable's literal content to the visitor.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * 
	 * @param visitor the visitor
	 */
	<T> Optional<T> visit(StringVisitable.Visitor<T> visitor);

	/**
	 * Supplies this visitable's literal content and contextual style to
	 * the visitor.
	 * 
	 * @return {@code Optional.empty()} if the visit finished, or a terminating
	 * result from the {@code visitor}
	 * 
	 * @param style the contextual style
	 * @param styledVisitor the visitor
	 */
	<T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style);

	/**
	 * Creates a visitable from a plain string.
	 * 
	 * @param string the plain string
	 */
	static StringVisitable plain(String string) {
		return new StringVisitable() {
			@Override
			public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Override
			public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style, string);
			}
		};
	}

	/**
	 * Creates a visitable from a plain string and a root style.
	 * 
	 * @param style the root style
	 * @param string the plain string
	 */
	static StringVisitable styled(String string, Style style) {
		return new StringVisitable() {
			@Override
			public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
				return visitor.accept(string);
			}

			@Override
			public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
				return styledVisitor.accept(style.withParent(style), string);
			}
		};
	}

	/**
	 * Concats multiple string visitables by the order they appear in the array.
	 * 
	 * @param visitables an array or varargs of visitables
	 */
	static StringVisitable concat(StringVisitable... visitables) {
		return concat(ImmutableList.copyOf(visitables));
	}

	/**
	 * Concats multiple string visitables by the order they appear in the list.
	 * 
	 * @param visitables a list of visitables
	 */
	static StringVisitable concat(List<? extends StringVisitable> visitables) {
		return new StringVisitable() {
			@Override
			public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
				for (StringVisitable stringVisitable : visitables) {
					Optional<T> optional = stringVisitable.visit(visitor);
					if (optional.isPresent()) {
						return optional;
					}
				}

				return Optional.empty();
			}

			@Override
			public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
				for (StringVisitable stringVisitable : visitables) {
					Optional<T> optional = stringVisitable.visit(styledVisitor, style);
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
	 * A visitor for string content and a contextual {@link Style}.
	 */
	public interface StyledVisitor<T> {
		/**
		 * Visits a string's content with a contextual style.
		 * 
		 * <p>A contextual style is obtained by calling {@link Style#withParent(Style)}
		 * on the current's text style, passing the previous contextual style or
		 * the starting style if it is the beginning of a visit.
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * StringVisitable#TERMINATE_VISIT} for convenience.
		 * 
		 * @return {@code Optional.empty()} to continue, a non-empty result to terminate
		 * 
		 * @param asString the literal string
		 */
		Optional<T> accept(Style style, String asString);
	}

	/**
	 * A visitor for string content.
	 */
	public interface Visitor<T> {
		/**
		 * Visits a literal string.
		 * 
		 * <p>When a {@link Optional#isPresent() present optional} is returned,
		 * the visit is terminated before visiting all text. Can return {@link
		 * StringVisitable#TERMINATE_VISIT} for convenience.
		 * 
		 * @return {@code Optional.empty()} to continue, a non-empty result to terminate
		 * 
		 * @param asString the literal string
		 */
		Optional<T> accept(String asString);
	}
}
