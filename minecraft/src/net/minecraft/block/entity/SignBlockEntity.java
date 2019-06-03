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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity extends BlockEntity {
	public final Text[] field_12050 = new Text[]{new LiteralText(""), new LiteralText(""), new LiteralText(""), new LiteralText("")};
	@Environment(EnvType.CLIENT)
	private boolean caretVisible;
	private int currentRow = -1;
	private int selectionStart = -1;
	private int selectionEnd = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final String[] textBeingEdited = new String[4];
	private DyeColor textColor = DyeColor.field_7963;

	public SignBlockEntity() {
		super(BlockEntityType.field_11911);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);

		for (int i = 0; i < 4; i++) {
			String string = Text.Serializer.toJson(this.field_12050[i]);
			compoundTag.putString("Text" + (i + 1), string);
		}

		compoundTag.putString("Color", this.textColor.getName());
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.editable = false;
		super.fromTag(compoundTag);
		this.textColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.field_7963);

		for (int i = 0; i < 4; i++) {
			String string = compoundTag.getString("Text" + (i + 1));
			Text text = Text.Serializer.fromJson(string.isEmpty() ? "\"\"" : string);
			if (this.world instanceof ServerWorld) {
				try {
					this.field_12050[i] = Texts.parse(this.getCommandSource(null), text, null, 0);
				} catch (CommandSyntaxException var6) {
					this.field_12050[i] = text;
				}
			} else {
				this.field_12050[i] = text;
			}

			this.textBeingEdited[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public Text method_11302(int i) {
		return this.field_12050[i];
	}

	public void method_11299(int i, Text text) {
		this.field_12050[i] = text;
		this.textBeingEdited[i] = null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getTextBeingEditedOnRow(int i, Function<Text, String> function) {
		if (this.textBeingEdited[i] == null && this.field_12050[i] != null) {
			this.textBeingEdited[i] = (String)function.apply(this.field_12050[i]);
		}

		return this.textBeingEdited[i];
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
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
		for (Text text : this.field_12050) {
			Style style = text == null ? null : text.method_10866();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.field_11750) {
					playerEntity.getServer().getCommandManager().execute(this.getCommandSource((ServerPlayerEntity)playerEntity), clickEvent.getValue());
				}
			}
		}

		return true;
	}

	public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity serverPlayerEntity) {
		String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.method_5477().getString();
		Text text = (Text)(serverPlayerEntity == null ? new LiteralText("Sign") : serverPlayerEntity.method_5476());
		return new ServerCommandSource(
			CommandOutput.DUMMY,
			new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5),
			Vec2f.ZERO,
			(ServerWorld)this.world,
			2,
			string,
			text,
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
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
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
