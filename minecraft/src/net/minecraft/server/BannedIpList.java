package net.minecraft.server;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BannedIpList extends ServerConfigList<String, BannedIpEntry> {
	public BannedIpList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<String> fromJson(JsonObject jsonObject) {
		return new BannedIpEntry(jsonObject);
	}

	public boolean isBanned(SocketAddress socketAddress) {
		String string = this.stringifyAddress(socketAddress);
		return this.contains(string);
	}

	public boolean isBanned(String string) {
		return this.contains(string);
	}

	public BannedIpEntry get(SocketAddress socketAddress) {
		String string = this.stringifyAddress(socketAddress);
		return this.get(string);
	}

	private String stringifyAddress(SocketAddress socketAddress) {
		String string = socketAddress.toString();
		if (string.contains("/")) {
			string = string.substring(string.indexOf(47) + 1);
		}

		if (string.contains(":")) {
			string = string.substring(0, string.indexOf(58));
		}

		return string;
	}
}
