package net.minecraft.server.function;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

/**
 * A lazy reference to another command function that may or may not exist.
 * 
 * <p>Notice that such an instance does not refresh upon reloads and may become
 * invalid.
 */
public class LazyContainer {
	public static final LazyContainer EMPTY = new LazyContainer(null);
	@Nullable
	private final Identifier id;
	private boolean initialized;
	private Optional<CommandFunction<ServerCommandSource>> function = Optional.empty();

	public LazyContainer(@Nullable Identifier id) {
		this.id = id;
	}

	public Optional<CommandFunction<ServerCommandSource>> get(CommandFunctionManager manager) {
		if (!this.initialized) {
			if (this.id != null) {
				this.function = manager.getFunction(this.id);
			}

			this.initialized = true;
		}

		return this.function;
	}

	@Nullable
	public Identifier getId() {
		return (Identifier)this.function.map(CommandFunction::id).orElse(this.id);
	}
}
