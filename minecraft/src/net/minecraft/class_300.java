package net.minecraft;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class class_300 {
	private final ClientPlayNetworkHandler field_1640;
	private int field_1641 = -1;
	@Nullable
	private Consumer<CompoundTag> field_1642;

	public class_300(ClientPlayNetworkHandler clientPlayNetworkHandler) {
		this.field_1640 = clientPlayNetworkHandler;
	}

	public boolean method_1404(int i, @Nullable CompoundTag compoundTag) {
		if (this.field_1641 == i && this.field_1642 != null) {
			this.field_1642.accept(compoundTag);
			this.field_1642 = null;
			return true;
		} else {
			return false;
		}
	}

	private int method_1402(Consumer<CompoundTag> consumer) {
		this.field_1642 = consumer;
		return ++this.field_1641;
	}

	public void method_1405(int i, Consumer<CompoundTag> consumer) {
		int j = this.method_1402(consumer);
		this.field_1640.method_2883(new QueryEntityNbtC2SPacket(j, i));
	}

	public void method_1403(BlockPos blockPos, Consumer<CompoundTag> consumer) {
		int i = this.method_1402(consumer);
		this.field_1640.method_2883(new QueryBlockNbtC2SPacket(i, blockPos));
	}
}
