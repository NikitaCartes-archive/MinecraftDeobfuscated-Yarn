package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AllowedAddressResolver {
	public static final AllowedAddressResolver DEFAULT = new AllowedAddressResolver(AddressResolver.DEFAULT, RedirectResolver.createSrv(), getBlockListPredicate());
	private final AddressResolver addressResolver;
	private final RedirectResolver redirectResolver;
	private final Predicate<Address> isAllowed;

	@VisibleForTesting
	AllowedAddressResolver(AddressResolver addressResolver, RedirectResolver redirectResolver, Predicate<Address> isBlocked) {
		this.addressResolver = addressResolver;
		this.redirectResolver = redirectResolver;
		this.isAllowed = isBlocked.negate();
	}

	private static Predicate<Address> getBlockListPredicate() {
		ImmutableList<Predicate<String>> immutableList = (ImmutableList<Predicate<String>>)Streams.stream(ServiceLoader.load(BlockListSupplier.class))
			.map(BlockListSupplier::createBlockList)
			.filter(Objects::nonNull)
			.collect(ImmutableList.toImmutableList());
		return address -> immutableList.stream().anyMatch(predicate -> predicate.test(address.getHostName()) || predicate.test(address.getHostAddress()));
	}

	public Optional<Address> resolve(ServerAddress address) {
		Optional<Address> optional = this.getAllowedAddress(address);
		if (!optional.isPresent()) {
			return Optional.empty();
		} else {
			Optional<ServerAddress> optional2 = this.redirectResolver.lookupRedirect(address);
			if (optional2.isPresent()) {
				optional = this.getAllowedAddress((ServerAddress)optional2.get());
			}

			return optional;
		}
	}

	private Optional<Address> getAllowedAddress(ServerAddress address) {
		return this.addressResolver.resolve(address).filter(this.isAllowed);
	}
}
