package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.MacroInvocation;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class Macro<T extends AbstractServerCommandSource<T>> implements CommandFunction<T> {
	private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#"), format -> {
		format.setMaximumFractionDigits(15);
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
	});
	private static final int CACHE_SIZE = 8;
	private final List<String> varNames;
	private final Object2ObjectLinkedOpenHashMap<List<String>, Procedure<T>> cache = new Object2ObjectLinkedOpenHashMap<>(8, 0.25F);
	private final Identifier id;
	private final List<Macro.Line<T>> lines;

	public Macro(Identifier id, List<Macro.Line<T>> lines, List<String> varNames) {
		this.id = id;
		this.lines = lines;
		this.varNames = varNames;
	}

	@Override
	public Identifier id() {
		return this.id;
	}

	@Override
	public Procedure<T> withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher<T> dispatcher) throws MacroException {
		if (arguments == null) {
			throw new MacroException(Text.translatable("commands.function.error.missing_arguments", Text.of(this.id())));
		} else {
			List<String> list = new ArrayList(this.varNames.size());

			for (String string : this.varNames) {
				NbtElement nbtElement = arguments.get(string);
				if (nbtElement == null) {
					throw new MacroException(Text.translatable("commands.function.error.missing_argument", Text.of(this.id()), string));
				}

				list.add(toString(nbtElement));
			}

			Procedure<T> procedure = this.cache.getAndMoveToLast(list);
			if (procedure != null) {
				return procedure;
			} else {
				if (this.cache.size() >= 8) {
					this.cache.removeFirst();
				}

				Procedure<T> procedure2 = this.withMacroReplaced(this.varNames, list, dispatcher);
				this.cache.put(list, procedure2);
				return procedure2;
			}
		}
	}

	private static String toString(NbtElement nbt) {
		if (nbt instanceof NbtFloat nbtFloat) {
			return DECIMAL_FORMAT.format((double)nbtFloat.floatValue());
		} else if (nbt instanceof NbtDouble nbtDouble) {
			return DECIMAL_FORMAT.format(nbtDouble.doubleValue());
		} else if (nbt instanceof NbtByte nbtByte) {
			return String.valueOf(nbtByte.byteValue());
		} else if (nbt instanceof NbtShort nbtShort) {
			return String.valueOf(nbtShort.shortValue());
		} else {
			return nbt instanceof NbtLong nbtLong ? String.valueOf(nbtLong.longValue()) : nbt.asString();
		}
	}

	private static void addArgumentsByIndices(List<String> arguments, IntList indices, List<String> out) {
		out.clear();
		indices.forEach(index -> out.add((String)arguments.get(index)));
	}

	private Procedure<T> withMacroReplaced(List<String> varNames, List<String> arguments, CommandDispatcher<T> dispatcher) throws MacroException {
		List<SourcedCommandAction<T>> list = new ArrayList(this.lines.size());
		List<String> list2 = new ArrayList(arguments.size());

		for (Macro.Line<T> line : this.lines) {
			addArgumentsByIndices(arguments, line.getDependentVariables(), list2);
			list.add(line.instantiate(list2, dispatcher, this.id));
		}

		return new ExpandedMacro<>(this.id().withPath((UnaryOperator<String>)(path -> path + "/" + varNames.hashCode())), list);
	}

	static class FixedLine<T> implements Macro.Line<T> {
		private final SourcedCommandAction<T> action;

		public FixedLine(SourcedCommandAction<T> action) {
			this.action = action;
		}

		@Override
		public IntList getDependentVariables() {
			return IntLists.emptyList();
		}

		@Override
		public SourcedCommandAction<T> instantiate(List<String> args, CommandDispatcher<T> dispatcher, Identifier identifier) {
			return this.action;
		}
	}

	interface Line<T> {
		IntList getDependentVariables();

		SourcedCommandAction<T> instantiate(List<String> args, CommandDispatcher<T> dispatcher, Identifier identifier) throws MacroException;
	}

	static class VariableLine<T extends AbstractServerCommandSource<T>> implements Macro.Line<T> {
		private final MacroInvocation invocation;
		private final IntList variableIndices;
		private final T field_47891;

		public VariableLine(MacroInvocation invocation, IntList variableIndices, T abstractServerCommandSource) {
			this.invocation = invocation;
			this.variableIndices = variableIndices;
			this.field_47891 = abstractServerCommandSource;
		}

		@Override
		public IntList getDependentVariables() {
			return this.variableIndices;
		}

		@Override
		public SourcedCommandAction<T> instantiate(List<String> args, CommandDispatcher<T> dispatcher, Identifier identifier) throws MacroException {
			String string = this.invocation.apply(args);

			try {
				return CommandFunction.parse(dispatcher, this.field_47891, new StringReader(string));
			} catch (CommandSyntaxException var6) {
				throw new MacroException(Text.translatable("commands.function.error.parse", Text.of(identifier), string, var6.getMessage()));
			}
		}
	}
}
