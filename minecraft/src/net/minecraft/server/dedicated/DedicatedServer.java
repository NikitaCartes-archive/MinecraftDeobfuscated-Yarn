package net.minecraft.server.dedicated;

import net.minecraft.class_3806;

public interface DedicatedServer {
	class_3806 method_16705();

	String getHostname();

	int getPort();

	String getMotd();

	String getVersion();

	int getCurrentPlayerCount();

	int getMaxPlayerCount();

	String[] getPlayerNames();

	String getLevelName();

	String method_12916();

	String method_12934(String string);

	boolean isDebuggingEnabled();

	void info(String string);

	void warn(String string);

	void logError(String string);

	void log(String string);
}
