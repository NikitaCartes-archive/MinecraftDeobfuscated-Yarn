package net.minecraft.command.argument.packrat;

import java.util.ArrayList;
import java.util.List;

public interface ParseErrorList<S> {
	void add(int cursor, Suggestable<S> suggestions, Object reason);

	default void add(int cursor, Object reason) {
		this.add(cursor, Suggestable.empty(), reason);
	}

	void setCursor(int cursor);

	public static class Impl<S> implements ParseErrorList<S> {
		private final List<ParseError<S>> errors = new ArrayList();
		private int cursor = -1;

		private void moveCursor(int cursor) {
			if (cursor > this.cursor) {
				this.cursor = cursor;
				this.errors.clear();
			}
		}

		@Override
		public void setCursor(int cursor) {
			this.moveCursor(cursor);
		}

		@Override
		public void add(int cursor, Suggestable<S> suggestions, Object reason) {
			this.moveCursor(cursor);
			if (cursor == this.cursor) {
				this.errors.add(new ParseError<>(cursor, suggestions, reason));
			}
		}

		public List<ParseError<S>> getErrors() {
			return this.errors;
		}

		public int getCursor() {
			return this.cursor;
		}
	}
}
