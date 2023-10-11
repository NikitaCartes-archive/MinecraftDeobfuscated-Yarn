package net.minecraft.server.function;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.MacroInvocation;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.util.Identifier;

class FunctionBuilder<T extends AbstractServerCommandSource<T>> {
	@Nullable
	private List<SourcedCommandAction<T>> actions = new ArrayList();
	@Nullable
	private List<Macro.Line<T>> macroLines;
	private final List<String> usedVariables = new ArrayList();

	public void addAction(SourcedCommandAction<T> action) {
		if (this.macroLines != null) {
			this.macroLines.add(new Macro.FixedLine<>(action));
		} else {
			this.actions.add(action);
		}
	}

	private int indexOfVariable(String variable) {
		int i = this.usedVariables.indexOf(variable);
		if (i == -1) {
			i = this.usedVariables.size();
			this.usedVariables.add(variable);
		}

		return i;
	}

	private IntList indicesOfVariables(List<String> variables) {
		IntArrayList intArrayList = new IntArrayList(variables.size());

		for (String string : variables) {
			intArrayList.add(this.indexOfVariable(string));
		}

		return intArrayList;
	}

	public void addMacroCommand(String command, int lineNum) {
		MacroInvocation macroInvocation = MacroInvocation.parse(command, lineNum);
		if (this.actions != null) {
			this.macroLines = new ArrayList(this.actions.size() + 1);

			for (SourcedCommandAction<T> sourcedCommandAction : this.actions) {
				this.macroLines.add(new Macro.FixedLine<>(sourcedCommandAction));
			}

			this.actions = null;
		}

		this.macroLines.add(new Macro.VariableLine(macroInvocation, this.indicesOfVariables(macroInvocation.variables())));
	}

	public CommandFunction<T> toCommandFunction(Identifier id) {
		return (CommandFunction<T>)(this.macroLines != null ? new Macro<>(id, this.macroLines, this.usedVariables) : new ExpandedMacro<>(id, this.actions));
	}
}
