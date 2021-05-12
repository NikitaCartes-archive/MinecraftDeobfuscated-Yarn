package net.minecraft.client.network;

import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Address {
	String getHostName();

	String getHostAddress();

	int getPort();

	InetSocketAddress getInetSocketAddress();

	static Address create(InetSocketAddress address) {
		return new Address() {
			@Override
			public String getHostName() {
				return address.getAddress().getHostName();
			}

			@Override
			public String getHostAddress() {
				return address.getAddress().getHostAddress();
			}

			@Override
			public int getPort() {
				return address.getPort();
			}

			@Override
			public InetSocketAddress getInetSocketAddress() {
				return address;
			}
		};
	}
}
