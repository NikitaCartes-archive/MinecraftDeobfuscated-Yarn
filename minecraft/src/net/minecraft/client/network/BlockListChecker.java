package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BlockListChecker {
	boolean isAllowed(Address address);

	boolean isAllowed(ServerAddress address);

	static BlockListChecker create() {
		final ImmutableList<Predicate<String>> immutableList = (ImmutableList<Predicate<String>>)Streams.stream(ServiceLoader.load(BlockListSupplier.class))
			.map(BlockListSupplier::createBlockList)
			.filter(Objects::nonNull)
			.collect(ImmutableList.toImmutableList());
		return new BlockListChecker() {
			@Override
			public boolean isAllowed(Address address) {
				String string = address.getHostName();
				String string2 = address.getHostAddress();
				return immutableList.stream().noneMatch(predicate -> predicate.test(string) || predicate.test(string2));
			}

			@Override
			public boolean isAllowed(ServerAddress address) {
				String string = address.getAddress();
				return immutableList.stream().noneMatch(predicate -> predicate.test(string));
			}
		};
	}
}
