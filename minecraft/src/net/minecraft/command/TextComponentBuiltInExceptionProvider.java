package net.minecraft.command;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.TranslatableTextComponent;

public class TextComponentBuiltInExceptionProvider implements BuiltInExceptionProvider {
	private static final Dynamic2CommandExceptionType field_9799 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.double.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_9788 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.double.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_9802 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.float.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_9795 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.float.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_9784 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.integer.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType field_9793 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("argument.integer.big", object2, object)
	);
	private static final DynamicCommandExceptionType field_9796 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.literal.incorrect", object)
	);
	private static final SimpleCommandExceptionType field_9783 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.quote.expected.start"));
	private static final SimpleCommandExceptionType field_9803 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.quote.expected.end"));
	private static final DynamicCommandExceptionType field_9791 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.quote.escape", object)
	);
	private static final DynamicCommandExceptionType field_9789 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.bool.invalid", object)
	);
	private static final DynamicCommandExceptionType field_9786 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.int.invalid", object)
	);
	private static final SimpleCommandExceptionType field_9801 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.int.expected"));
	private static final DynamicCommandExceptionType field_9800 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.double.invalid", object)
	);
	private static final SimpleCommandExceptionType field_9798 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.double.expected"));
	private static final DynamicCommandExceptionType field_9804 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.float.invalid", object)
	);
	private static final SimpleCommandExceptionType field_9787 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.float.expected"));
	private static final SimpleCommandExceptionType field_9794 = new SimpleCommandExceptionType(new TranslatableTextComponent("parsing.bool.expected"));
	private static final DynamicCommandExceptionType field_9785 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("parsing.expected", object)
	);
	private static final SimpleCommandExceptionType field_9797 = new SimpleCommandExceptionType(new TranslatableTextComponent("command.unknown.command"));
	private static final SimpleCommandExceptionType field_9792 = new SimpleCommandExceptionType(new TranslatableTextComponent("command.unknown.argument"));
	private static final SimpleCommandExceptionType field_9782 = new SimpleCommandExceptionType(new TranslatableTextComponent("command.expected.separator"));
	private static final DynamicCommandExceptionType field_9790 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("command.exception", object)
	);

	@Override
	public Dynamic2CommandExceptionType doubleTooLow() {
		return field_9799;
	}

	@Override
	public Dynamic2CommandExceptionType doubleTooHigh() {
		return field_9788;
	}

	@Override
	public Dynamic2CommandExceptionType floatTooLow() {
		return field_9802;
	}

	@Override
	public Dynamic2CommandExceptionType floatTooHigh() {
		return field_9795;
	}

	@Override
	public Dynamic2CommandExceptionType integerTooLow() {
		return field_9784;
	}

	@Override
	public Dynamic2CommandExceptionType integerTooHigh() {
		return field_9793;
	}

	@Override
	public DynamicCommandExceptionType literalIncorrect() {
		return field_9796;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedStartOfQuote() {
		return field_9783;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedEndOfQuote() {
		return field_9803;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidEscape() {
		return field_9791;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidBool() {
		return field_9789;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidInt() {
		return field_9786;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedInt() {
		return field_9801;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidDouble() {
		return field_9800;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedDouble() {
		return field_9798;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidFloat() {
		return field_9804;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedFloat() {
		return field_9787;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedBool() {
		return field_9794;
	}

	@Override
	public DynamicCommandExceptionType readerExpectedSymbol() {
		return field_9785;
	}

	@Override
	public SimpleCommandExceptionType dispatcherUnknownCommand() {
		return field_9797;
	}

	@Override
	public SimpleCommandExceptionType dispatcherUnknownArgument() {
		return field_9792;
	}

	@Override
	public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
		return field_9782;
	}

	@Override
	public DynamicCommandExceptionType dispatcherParseException() {
		return field_9790;
	}
}
