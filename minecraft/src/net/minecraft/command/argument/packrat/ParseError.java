package net.minecraft.command.argument.packrat;

public record ParseError<S>(int cursor, Suggestable<S> suggestions, Object reason) {
}
