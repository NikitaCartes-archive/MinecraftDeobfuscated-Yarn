package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AllowedAddressResolver {
	public static final AllowedAddressResolver DEFAULT = new AllowedAddressResolver(
		AddressResolver.DEFAULT, RedirectResolver.createSrv(), BlockListChecker.create()
	);
	private final AddressResolver addressResolver;
	private final RedirectResolver redirectResolver;
	private final BlockListChecker blockListChecker;

	@VisibleForTesting
	AllowedAddressResolver(AddressResolver addressResolver, RedirectResolver redirectResolver, BlockListChecker blockListChecker) {
		this.addressResolver = addressResolver;
		this.redirectResolver = redirectResolver;
		this.blockListChecker = blockListChecker;
	}

	public Optional<Address> resolve(ServerAddress address) {
		Optional<Address> optional = this.addressResolver.resolve(address);
		if ((!optional.isPresent() || this.blockListChecker.isAllowed((Address)optional.get())) && this.blockListChecker.isAllowed(address)) {
			Optional<ServerAddress> optional2 = this.redirectResolver.lookupRedirect(address);
			if (optional2.isPresent()) {
				optional = this.addressResolver.resolve((ServerAddress)optional2.get()).filter(this.blockListChecker::isAllowed);
			}

			return optional;
		} else {
			return Optional.empty();
		}
	}
}
