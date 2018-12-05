package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity extends BlockEntity implements CommandOutput {
	public final TextComponent[] text = new TextComponent[]{
		new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")
	};
	private boolean field_16502 = false;
	private int field_16501 = -1;
	private int field_16500 = -1;
	private int field_16499 = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final String[] field_12049 = new String[4];
	private DyeColor field_16419 = DyeColor.BLACK;

	public SignBlockEntity() {
		super(BlockEntityType.SIGN);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);

		for (int i = 0; i < 4; i++) {
			String string = TextComponent.Serializer.toJsonString(this.text[i]);
			compoundTag.putString("Text" + (i + 1), string);
		}

		compoundTag.putString("Color", this.field_16419.getName());
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.editable = false;
		super.fromTag(compoundTag);
		this.field_16419 = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; i++) {
			String string = compoundTag.getString("Text" + (i + 1));
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
			if (this.world instanceof ServerWorld) {
				try {
					this.text[i] = TextFormatter.method_10881(this.method_11304(null), textComponent, null);
				} catch (CommandSyntaxException var6) {
					this.text[i] = textComponent;
				}
			} else {
				this.text[i] = textComponent;
			}

			this.field_12049[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_11302(int i) {
		return this.text[i];
	}

	public void method_11299(int i, TextComponent textComponent) {
		this.text[i] = textComponent;
		this.field_12049[i] = null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11300(int i, Function<TextComponent, String> function) {
		if (this.field_12049[i] == null && this.text[i] != null) {
			this.field_12049[i] = (String)function.apply(this.text[i]);
		}

		return this.field_12049[i];
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 9, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	@Override
	public boolean method_11011() {
		return true;
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Environment(EnvType.CLIENT)
	public void setEditable(boolean bl) {
		this.editable = bl;
		if (!bl) {
			this.editor = null;
		}
	}

	public void setEditor(PlayerEntity playerEntity) {
		this.editor = playerEntity;
	}

	public PlayerEntity getEditor() {
		return this.editor;
	}

	public boolean onActivate(PlayerEntity playerEntity) {
		for (TextComponent textComponent : this.text) {
			Style style = textComponent == null ? null : textComponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerEntity.getServer().getCommandManager().execute(this.method_11304((ServerPlayerEntity)playerEntity), clickEvent.getValue());
				}
			}
		}

		return true;
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
	}

	public ServerCommandSource method_11304(@Nullable ServerPlayerEntity serverPlayerEntity) {
		String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.getName().getString();
		TextComponent textComponent = (TextComponent)(serverPlayerEntity == null ? new StringTextComponent("Sign") : serverPlayerEntity.getDisplayName());
		return new ServerCommandSource(
			this,
			new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5),
			Vec2f.ZERO,
			(ServerWorld)this.world,
			2,
			string,
			textComponent,
			this.world.getServer(),
			serverPlayerEntity
		);
	}

	@Override
	public boolean sendCommandFeedback() {
		return false;
	}

	@Override
	public boolean shouldTrackOutput() {
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return false;
	}

	public DyeColor method_16126() {
		return this.field_16419;
	}

	public boolean method_16127(DyeColor dyeColor) {
		if (dyeColor != this.method_16126()) {
			this.field_16419 = dyeColor;
			this.markDirty();
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_16332(int i, int j, int k, boolean bl) {
		this.field_16501 = i;
		this.field_16500 = j;
		this.field_16499 = k;
		this.field_16502 = bl;
	}

	@Environment(EnvType.CLIENT)
	public void method_16335() {
		this.field_16501 = -1;
		this.field_16500 = -1;
		this.field_16499 = -1;
		this.field_16502 = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_16331() {
		return this.field_16502;
	}

	@Environment(EnvType.CLIENT)
	public int method_16334() {
		return this.field_16501;
	}

	@Environment(EnvType.CLIENT)
	public int method_16336() {
		return this.field_16500;
	}

	@Environment(EnvType.CLIENT)
	public int method_16333() {
		return this.field_16499;
	}
}
