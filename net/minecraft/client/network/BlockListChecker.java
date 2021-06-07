/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public interface BlockListChecker {
    public boolean isAllowed(Address var1);

    public boolean isAllowed(ServerAddress var1);

    public static BlockListChecker create() {
        final ImmutableList immutableList = Streams.stream(ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
        return new BlockListChecker(){

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

