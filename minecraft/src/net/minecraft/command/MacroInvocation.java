package net.minecraft.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import net.minecraft.server.function.CommandFunction;

public record MacroInvocation(List<String> segments, List<String> variables) {
	public static MacroInvocation parse(String command, int lineNumber) {
		Builder<String> builder = ImmutableList.builder();
		Builder<String> builder2 = ImmutableList.builder();
		int i = command.length();
		int j = 0;
		int k = command.indexOf(36);

		while (k != -1) {
			if (k != i - 1 && command.charAt(k + 1) == '(') {
				builder.add(command.substring(j, k));
				int l = command.indexOf(41, k + 1);
				if (l == -1) {
					throw new IllegalArgumentException("Unterminated macro variable in macro '" + command + "' on line " + lineNumber);
				}

				String string = command.substring(k + 2, l);
				if (!isValidMacroName(string)) {
					throw new IllegalArgumentException("Invalid macro variable name '" + string + "' on line " + lineNumber);
				}

				builder2.add(string);
				j = l + 1;
				k = command.indexOf(36, j);
			} else {
				k = command.indexOf(36, k + 1);
			}
		}

		if (j == 0) {
			throw new IllegalArgumentException("Macro without variables on line " + lineNumber);
		} else {
			if (j != i) {
				builder.add(command.substring(j));
			}

			return new MacroInvocation(builder.build(), builder2.build());
		}
	}

	private static boolean isValidMacroName(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_') {
				return false;
			}
		}

		return true;
	}

	public String apply(List<String> arguments) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.variables.size(); i++) {
			stringBuilder.append((String)this.segments.get(i)).append((String)arguments.get(i));
			CommandFunction.validateCommandLength(stringBuilder);
		}

		if (this.segments.size() > this.variables.size()) {
			stringBuilder.append((String)this.segments.get(this.segments.size() - 1));
		}

		CommandFunction.validateCommandLength(stringBuilder);
		return stringBuilder.toString();
	}
}
