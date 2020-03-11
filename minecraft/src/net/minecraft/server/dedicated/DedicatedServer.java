package net.minecraft.server.dedicated;

public interface DedicatedServer {
	ServerPropertiesHandler getProperties();

	String getHostname();

	int getPort();

	String getMotd();

	String getVersion();

	int getCurrentPlayerCount();

	int getMaxPlayerCount();

	String[] getPlayerNames();

	String getLevelName();

	String getPlugins();

	String executeRconCommand(String string);

	boolean isDebuggingEnabled();

	void info(String message);

	void warn(String message);

	void logError(String message);

	void log(String message);
}
