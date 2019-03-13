package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
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

public class SignBlockEntity extends BlockEntity {
	public final TextComponent[] field_12050 = new TextComponent[]{
		new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")
	};
	@Environment(EnvType.CLIENT)
	private boolean caretVisible;
	private int currentRow = -1;
	private int selectionStart = -1;
	private int selectionEnd = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final String[] textBeingEdited = new String[4];
	private DyeColor textColor = DyeColor.BLACK;

	public SignBlockEntity() {
		super(BlockEntityType.SIGN);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);

		for (int i = 0; i < 4; i++) {
			String string = TextComponent.Serializer.toJsonString(this.field_12050[i]);
			compoundTag.putString("Text" + (i + 1), string);
		}

		compoundTag.putString("Color", this.textColor.getName());
		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		this.editable = false;
		super.method_11014(compoundTag);
		this.textColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; i++) {
			String string = compoundTag.getString("Text" + (i + 1));
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
			if (this.world instanceof ServerWorld) {
				try {
					this.field_12050[i] = TextFormatter.method_10881(this.method_11304(null), textComponent, null);
				} catch (CommandSyntaxException var6) {
					this.field_12050[i] = textComponent;
				}
			} else {
				this.field_12050[i] = textComponent;
			}

			this.textBeingEdited[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_11302(int i) {
		return this.field_12050[i];
	}

	public void method_11299(int i, TextComponent textComponent) {
		this.field_12050[i] = textComponent;
		this.textBeingEdited[i] = null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getTextBeingEditedOnRow(int i, Function<TextComponent, String> function) {
		if (this.textBeingEdited[i] == null && this.field_12050[i] != null) {
			this.textBeingEdited[i] = (String)function.apply(this.field_12050[i]);
		}

		return this.textBeingEdited[i];
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 9, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	@Override
	public boolean shouldNotCopyTagFromItem() {
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
		for (TextComponent textComponent : this.field_12050) {
			Style style = textComponent == null ? null : textComponent.method_10866();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerEntity.getServer().getCommandManager().execute(this.method_11304((ServerPlayerEntity)playerEntity), clickEvent.getValue());
				}
			}
		}

		return true;
	}

	public ServerCommandSource method_11304(@Nullable ServerPlayerEntity serverPlayerEntity) {
		String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.method_5477().getString();
		TextComponent textComponent = (TextComponent)(serverPlayerEntity == null ? new StringTextComponent("Sign") : serverPlayerEntity.method_5476());
		return new ServerCommandSource(
			CommandOutput.field_17395,
			new Vec3d((double)this.field_11867.getX() + 0.5, (double)this.field_11867.getY() + 0.5, (double)this.field_11867.getZ() + 0.5),
			Vec2f.ZERO,
			(ServerWorld)this.world,
			2,
			string,
			textComponent,
			this.world.getServer(),
			serverPlayerEntity
		);
	}

	public DyeColor getTextColor() {
		return this.textColor;
	}

	public boolean setTextColor(DyeColor dyeColor) {
		if (dyeColor != this.getTextColor()) {
			this.textColor = dyeColor;
			this.markDirty();
			this.world.method_8413(this.method_11016(), this.method_11010(), this.method_11010(), 3);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void setSelectionState(int i, int j, int k, boolean bl) {
		this.currentRow = i;
		this.selectionStart = j;
		this.selectionEnd = k;
		this.caretVisible = bl;
	}

	@Environment(EnvType.CLIENT)
	public void resetSelectionState() {
		this.currentRow = -1;
		this.selectionStart = -1;
		this.selectionEnd = -1;
		this.caretVisible = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCaretVisible() {
		return this.caretVisible;
	}

	@Environment(EnvType.CLIENT)
	public int getCurrentRow() {
		return this.currentRow;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionStart() {
		return this.selectionStart;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionEnd() {
		return this.selectionEnd;
	}
}
