package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;

public class class_3002 extends ServerBossBar {
	private final Identifier field_13441;
	private final Set<UUID> field_13440 = Sets.<UUID>newHashSet();
	private int field_13443;
	private int field_13442 = 100;

	public class_3002(Identifier identifier, TextComponent textComponent) {
		super(textComponent, BossBar.Color.field_5786, BossBar.Overlay.field_5795);
		this.field_13441 = identifier;
		this.setPercent(0.0F);
	}

	public Identifier method_12959() {
		return this.field_13441;
	}

	@Override
	public void method_14088(ServerPlayerEntity serverPlayerEntity) {
		super.method_14088(serverPlayerEntity);
		this.field_13440.add(serverPlayerEntity.getUuid());
	}

	public void method_12964(UUID uUID) {
		this.field_13440.add(uUID);
	}

	@Override
	public void method_14089(ServerPlayerEntity serverPlayerEntity) {
		super.method_14089(serverPlayerEntity);
		this.field_13440.remove(serverPlayerEntity.getUuid());
	}

	@Override
	public void method_14094() {
		super.method_14094();
		this.field_13440.clear();
	}

	public int method_12955() {
		return this.field_13443;
	}

	public int method_12960() {
		return this.field_13442;
	}

	public void method_12954(int i) {
		this.field_13443 = i;
		this.setPercent(MathHelper.clamp((float)i / (float)this.field_13442, 0.0F, 1.0F));
	}

	public void method_12956(int i) {
		this.field_13442 = i;
		this.setPercent(MathHelper.clamp((float)this.field_13443 / (float)i, 0.0F, 1.0F));
	}

	public final TextComponent method_12965() {
		return TextFormatter.bracketed(this.getName())
			.modifyStyle(
				style -> style.setColor(this.getColor().getTextFormat())
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.method_12959().toString())))
						.setInsertion(this.method_12959().toString())
			);
	}

	public boolean method_12962(Collection<ServerPlayerEntity> collection) {
		Set<UUID> set = Sets.<UUID>newHashSet();
		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet();

		for (UUID uUID : this.field_13440) {
			boolean bl = false;

			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (serverPlayerEntity.getUuid().equals(uUID)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set.add(uUID);
			}
		}

		for (ServerPlayerEntity serverPlayerEntity2 : collection) {
			boolean bl = false;

			for (UUID uUID2 : this.field_13440) {
				if (serverPlayerEntity2.getUuid().equals(uUID2)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set2.add(serverPlayerEntity2);
			}
		}

		for (UUID uUID : set) {
			for (ServerPlayerEntity serverPlayerEntity3 : this.method_14092()) {
				if (serverPlayerEntity3.getUuid().equals(uUID)) {
					this.method_14089(serverPlayerEntity3);
					break;
				}
			}

			this.field_13440.remove(uUID);
		}

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.method_14088(serverPlayerEntity2);
		}

		return !set.isEmpty() || !set2.isEmpty();
	}

	public CompoundTag method_12963() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", TextComponent.Serializer.toJsonString(this.name));
		compoundTag.putBoolean("Visible", this.method_14093());
		compoundTag.putInt("Value", this.field_13443);
		compoundTag.putInt("Max", this.field_13442);
		compoundTag.putString("Color", this.getColor().getName());
		compoundTag.putString("Overlay", this.getOverlay().getName());
		compoundTag.putBoolean("DarkenScreen", this.getDarkenSky());
		compoundTag.putBoolean("PlayBossMusic", this.hasDragonMusic());
		compoundTag.putBoolean("CreateWorldFog", this.getThickenFog());
		ListTag listTag = new ListTag();

		for (UUID uUID : this.field_13440) {
			listTag.add((Tag)TagHelper.serializeUuid(uUID));
		}

		compoundTag.put("Players", listTag);
		return compoundTag;
	}

	public static class_3002 method_12966(CompoundTag compoundTag, Identifier identifier) {
		class_3002 lv = new class_3002(identifier, TextComponent.Serializer.fromJsonString(compoundTag.getString("Name")));
		lv.setVisible(compoundTag.getBoolean("Visible"));
		lv.method_12954(compoundTag.getInt("Value"));
		lv.method_12956(compoundTag.getInt("Max"));
		lv.setColor(BossBar.Color.byName(compoundTag.getString("Color")));
		lv.setOverlay(BossBar.Overlay.byName(compoundTag.getString("Overlay")));
		lv.setDarkenSky(compoundTag.getBoolean("DarkenScreen"));
		lv.setDragonMusic(compoundTag.getBoolean("PlayBossMusic"));
		lv.setThickenFog(compoundTag.getBoolean("CreateWorldFog"));
		ListTag listTag = compoundTag.getList("Players", 10);

		for (int i = 0; i < listTag.size(); i++) {
			lv.method_12964(TagHelper.deserializeUuid(listTag.getCompoundTag(i)));
		}

		return lv;
	}

	public void method_12957(ServerPlayerEntity serverPlayerEntity) {
		if (this.field_13440.contains(serverPlayerEntity.getUuid())) {
			this.method_14088(serverPlayerEntity);
		}
	}

	public void method_12961(ServerPlayerEntity serverPlayerEntity) {
		super.method_14089(serverPlayerEntity);
	}
}
