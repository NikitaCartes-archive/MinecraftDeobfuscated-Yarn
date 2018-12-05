package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

public class class_3004 {
	private final MinecraftServer field_13448;
	private final Map<Identifier, class_3002> field_13447 = Maps.<Identifier, class_3002>newHashMap();

	public class_3004(MinecraftServer minecraftServer) {
		this.field_13448 = minecraftServer;
	}

	@Nullable
	public class_3002 method_12971(Identifier identifier) {
		return (class_3002)this.field_13447.get(identifier);
	}

	public class_3002 method_12970(Identifier identifier, TextComponent textComponent) {
		class_3002 lv = new class_3002(identifier, textComponent);
		this.field_13447.put(identifier, lv);
		return lv;
	}

	public void method_12973(class_3002 arg) {
		this.field_13447.remove(arg.method_12959());
	}

	public Collection<Identifier> method_12968() {
		return this.field_13447.keySet();
	}

	public Collection<class_3002> method_12969() {
		return this.field_13447.values();
	}

	public CompoundTag method_12974() {
		CompoundTag compoundTag = new CompoundTag();

		for (class_3002 lv : this.field_13447.values()) {
			compoundTag.put(lv.method_12959().toString(), lv.method_12963());
		}

		return compoundTag;
	}

	public void method_12972(CompoundTag compoundTag) {
		for (String string : compoundTag.getKeys()) {
			Identifier identifier = new Identifier(string);
			this.field_13447.put(identifier, class_3002.method_12966(compoundTag.getCompound(string), identifier));
		}
	}

	public void method_12975(ServerPlayerEntity serverPlayerEntity) {
		for (class_3002 lv : this.field_13447.values()) {
			lv.method_12957(serverPlayerEntity);
		}
	}

	public void method_12976(ServerPlayerEntity serverPlayerEntity) {
		for (class_3002 lv : this.field_13447.values()) {
			lv.method_12961(serverPlayerEntity);
		}
	}
}
