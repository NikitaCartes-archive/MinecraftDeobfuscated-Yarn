/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public class AllowedAddressResolver {
    public static final AllowedAddressResolver DEFAULT = new AllowedAddressResolver(AddressResolver.DEFAULT, RedirectResolver.createSrv(), BlockListChecker.create());
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
        if (optional.isPresent() && !this.blockListChecker.isBlocked(optional.get()) || !this.blockListChecker.isBlocked(address)) {
            return Optional.empty();
        }
        Optional<ServerAddress> optional2 = this.redirectResolver.lookupRedirect(address);
        if (optional2.isPresent()) {
            optional = this.addressResolver.resolve(optional2.get()).filter(this.blockListChecker::isBlocked);
        }
        return optional;
    }
}

