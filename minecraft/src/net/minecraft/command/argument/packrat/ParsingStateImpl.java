package net.minecraft.command.argument.packrat;

import com.mojang.brigadier.StringReader;

public class ParsingStateImpl extends ParsingState<StringReader> {
	private final StringReader reader;

	public ParsingStateImpl(ParsingRules<StringReader> rules, ParseErrorList<StringReader> errors, StringReader reader) {
		super(rules, errors);
		this.reader = reader;
	}

	public StringReader getReader() {
		return this.reader;
	}

	@Override
	public int getCursor() {
		return this.reader.getCursor();
	}

	@Override
	public void setCursor(int cursor) {
		this.reader.setCursor(cursor);
	}
}
