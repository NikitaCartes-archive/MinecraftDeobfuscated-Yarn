/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DataQueryHandler {
    private final ClientPlayNetworkHandler networkHandler;
    private int expectedTransactionId = -1;
    @Nullable
    private Consumer<CompoundTag> callback;

    public DataQueryHandler(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.networkHandler = clientPlayNetworkHandler;
    }

    public boolean handleQueryResponse(int i, @Nullable CompoundTag compoundTag) {
        if (this.expectedTransactionId == i && this.callback != null) {
            this.callback.accept(compoundTag);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int nextQuery(Consumer<CompoundTag> consumer) {
        this.callback = consumer;
        return ++this.expectedTransactionId;
    }

    public void queryEntityNbt(int i, Consumer<CompoundTag> consumer) {
        int j = this.nextQuery(consumer);
        this.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(j, i));
    }

    public void queryBlockNbt(BlockPos blockPos, Consumer<CompoundTag> consumer) {
        int i = this.nextQuery(consumer);
        this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(i, blockPos));
    }
}

