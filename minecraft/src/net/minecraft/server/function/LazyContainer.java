package net.minecraft.server.function;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

/**
 * A lazy reference to another command function that may or may not exist.
 * 
 * <p>Notice that such an instance does not refresh upon reloads and may become
 * invalid.
 */
public class LazyContainer {
	public static final Codec<LazyContainer> CODEC = Identifier.CODEC.xmap(LazyContainer::new, LazyContainer::getId);
	private final Identifier id;
	private boolean initialized;
	private Optional<CommandFunction<ServerCommandSource>> function = Optional.empty();

	public LazyContainer(Identifier id) {
		this.id = id;
	}

	public Optional<CommandFunction<ServerCommandSource>> get(CommandFunctionManager commandFunctionManager) {
		if (!this.initialized) {
			this.function = commandFunctionManager.getFunction(this.id);
			this.initialized = true;
		}

		return this.function;
	}

	public Identifier getId() {
		return this.id;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else {
			if (o instanceof LazyContainer lazyContainer && this.getId().equals(lazyContainer.getId())) {
				return true;
			}

			return false;
		}
	}
}
