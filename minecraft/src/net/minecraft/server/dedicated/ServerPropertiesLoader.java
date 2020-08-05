package net.minecraft.server.dedicated;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.util.registry.DynamicRegistryManager;

public class ServerPropertiesLoader {
	private final Path path;
	private ServerPropertiesHandler propertiesHandler;

	public ServerPropertiesLoader(DynamicRegistryManager dynamicRegistryManager, Path path) {
		this.path = path;
		this.propertiesHandler = ServerPropertiesHandler.load(dynamicRegistryManager, path);
	}

	public ServerPropertiesHandler getPropertiesHandler() {
		return this.propertiesHandler;
	}

	public void store() {
		this.propertiesHandler.saveProperties(this.path);
	}

	public ServerPropertiesLoader apply(UnaryOperator<ServerPropertiesHandler> unaryOperator) {
		(this.propertiesHandler = (ServerPropertiesHandler)unaryOperator.apply(this.propertiesHandler)).saveProperties(this.path);
		return this;
	}
}
