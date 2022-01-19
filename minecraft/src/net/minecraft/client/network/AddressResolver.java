package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface AddressResolver {
	Logger LOGGER = LogUtils.getLogger();
	AddressResolver DEFAULT = address -> {
		try {
			InetAddress inetAddress = InetAddress.getByName(address.getAddress());
			return Optional.of(Address.create(new InetSocketAddress(inetAddress, address.getPort())));
		} catch (UnknownHostException var2) {
			LOGGER.debug("Couldn't resolve server {} address", address.getAddress(), var2);
			return Optional.empty();
		}
	};

	Optional<Address> resolve(ServerAddress address);
}
