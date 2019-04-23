package net.minecraft.command;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;

public class TextComponentBuiltInExceptionProvider implements BuiltInExceptionProvider {
	private static final Dynamic2CommandExceptionType DOUBLE_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.double.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType DOUBLE_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.double.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType FLOAT_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.float.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType FLOAT_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.float.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType INTEGER_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.integer.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType INTEGER_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.integer.big", object2, object)
	);
	private static final Dynamic2CommandExceptionType LONG_TOO_LOW_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.long.low", object2, object)
	);
	private static final Dynamic2CommandExceptionType LONG_TOO_HIGH_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableComponent("argument.long.big", object2, object)
	);
	private static final DynamicCommandExceptionType EXPECTED_LITERAL_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("argument.literal.incorrect", object)
	);
	private static final SimpleCommandExceptionType EXPECTED_START_QUOTE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("parsing.quote.expected.start")
	);
	private static final SimpleCommandExceptionType EXPECTED_END_QUOTE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("parsing.quote.expected.end")
	);
	private static final DynamicCommandExceptionType INVALID_ESCAPE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.quote.escape", object)
	);
	private static final DynamicCommandExceptionType INVALID_BOOLEAN_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.bool.invalid", object)
	);
	private static final DynamicCommandExceptionType INVALID_INTEGER_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.int.invalid", object)
	);
	private static final SimpleCommandExceptionType EXPECTED_INTEGER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("parsing.int.expected"));
	private static final DynamicCommandExceptionType INVALID_LONG_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.long.invalid", object)
	);
	private static final SimpleCommandExceptionType EXPECTED_LONG_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("parsing.long.expected"));
	private static final DynamicCommandExceptionType INVALID_DOUBLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.double.invalid", object)
	);
	private static final SimpleCommandExceptionType EXPECTED_DOUBLE_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("parsing.double.expected")
	);
	private static final DynamicCommandExceptionType INVALID_FLOAT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.float.invalid", object)
	);
	private static final SimpleCommandExceptionType EXPECTED_FLOAT_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("parsing.float.expected"));
	private static final SimpleCommandExceptionType EXPECTED_BOOLEAN_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("parsing.bool.expected"));
	private static final DynamicCommandExceptionType EXPECTED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("parsing.expected", object)
	);
	private static final SimpleCommandExceptionType UNKNOWN_COMMAND_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("command.unknown.command")
	);
	private static final SimpleCommandExceptionType UNKNOWN_ARGUMENT_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("command.unknown.argument")
	);
	private static final SimpleCommandExceptionType EXPECTED_SEPARATOR_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableComponent("command.expected.separator")
	);
	private static final DynamicCommandExceptionType COMMAND_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("command.exception", object)
	);

	@Override
	public Dynamic2CommandExceptionType doubleTooLow() {
		return DOUBLE_TOO_LOW_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType doubleTooHigh() {
		return DOUBLE_TOO_HIGH_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType floatTooLow() {
		return FLOAT_TOO_LOW_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType floatTooHigh() {
		return FLOAT_TOO_HIGH_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType integerTooLow() {
		return INTEGER_TOO_LOW_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType integerTooHigh() {
		return INTEGER_TOO_HIGH_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType longTooLow() {
		return LONG_TOO_LOW_EXCEPTION;
	}

	@Override
	public Dynamic2CommandExceptionType longTooHigh() {
		return LONG_TOO_HIGH_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType literalIncorrect() {
		return EXPECTED_LITERAL_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedStartOfQuote() {
		return EXPECTED_START_QUOTE_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedEndOfQuote() {
		return EXPECTED_END_QUOTE_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidEscape() {
		return INVALID_ESCAPE_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidBool() {
		return INVALID_BOOLEAN_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidInt() {
		return INVALID_INTEGER_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedInt() {
		return EXPECTED_INTEGER_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidLong() {
		return INVALID_LONG_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedLong() {
		return EXPECTED_LONG_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidDouble() {
		return INVALID_DOUBLE_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedDouble() {
		return EXPECTED_DOUBLE_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerInvalidFloat() {
		return INVALID_FLOAT_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedFloat() {
		return EXPECTED_FLOAT_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType readerExpectedBool() {
		return EXPECTED_BOOLEAN_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType readerExpectedSymbol() {
		return EXPECTED_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType dispatcherUnknownCommand() {
		return UNKNOWN_COMMAND_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType dispatcherUnknownArgument() {
		return UNKNOWN_ARGUMENT_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
		return EXPECTED_SEPARATOR_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionType dispatcherParseException() {
		return COMMAND_EXCEPTION;
	}
}
