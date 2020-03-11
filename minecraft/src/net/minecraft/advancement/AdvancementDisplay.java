package net.minecraft.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
	private final Text title;
	private final Text description;
	private final ItemStack icon;
	private final Identifier background;
	private final AdvancementFrame frame;
	private final boolean showToast;
	private final boolean announceToChat;
	private final boolean hidden;
	private float xPos;
	private float yPos;

	public AdvancementDisplay(
		ItemStack icon,
		Text title,
		Text description,
		@Nullable Identifier background,
		AdvancementFrame frame,
		boolean showToast,
		boolean announceToChat,
		boolean hidden
	) {
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.background = background;
		this.frame = frame;
		this.showToast = showToast;
		this.announceToChat = announceToChat;
		this.hidden = hidden;
	}

	public void setPosition(float xPos, float yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public Text getTitle() {
		return this.title;
	}

	public Text getDescription() {
		return this.description;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getIcon() {
		return this.icon;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Identifier getBackground() {
		return this.background;
	}

	public AdvancementFrame getFrame() {
		return this.frame;
	}

	@Environment(EnvType.CLIENT)
	public float getX() {
		return this.xPos;
	}

	@Environment(EnvType.CLIENT)
	public float getY() {
		return this.yPos;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldShowToast() {
		return this.showToast;
	}

	public boolean shouldAnnounceToChat() {
		return this.announceToChat;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public static AdvancementDisplay fromJson(JsonObject obj, JsonDeserializationContext context) {
		Text text = JsonHelper.deserialize(obj, "title", context, Text.class);
		Text text2 = JsonHelper.deserialize(obj, "description", context, Text.class);
		if (text != null && text2 != null) {
			ItemStack itemStack = iconFromJson(JsonHelper.getObject(obj, "icon"));
			Identifier identifier = obj.has("background") ? new Identifier(JsonHelper.getString(obj, "background")) : null;
			AdvancementFrame advancementFrame = obj.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(obj, "frame")) : AdvancementFrame.TASK;
			boolean bl = JsonHelper.getBoolean(obj, "show_toast", true);
			boolean bl2 = JsonHelper.getBoolean(obj, "announce_to_chat", true);
			boolean bl3 = JsonHelper.getBoolean(obj, "hidden", false);
			return new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static ItemStack iconFromJson(JsonObject jsonObject) {
		if (!jsonObject.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JsonHelper.getItem(jsonObject, "item");
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack itemStack = new ItemStack(item);
				if (jsonObject.has("nbt")) {
					try {
						CompoundTag compoundTag = StringNbtReader.parse(JsonHelper.asString(jsonObject.get("nbt"), "nbt"));
						itemStack.setTag(compoundTag);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return itemStack;
			}
		}
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		packetByteBuf.writeText(this.title);
		packetByteBuf.writeText(this.description);
		packetByteBuf.writeItemStack(this.icon);
		packetByteBuf.writeEnumConstant(this.frame);
		int i = 0;
		if (this.background != null) {
			i |= 1;
		}

		if (this.showToast) {
			i |= 2;
		}

		if (this.hidden) {
			i |= 4;
		}

		packetByteBuf.writeInt(i);
		if (this.background != null) {
			packetByteBuf.writeIdentifier(this.background);
		}

		packetByteBuf.writeFloat(this.xPos);
		packetByteBuf.writeFloat(this.yPos);
	}

	public static AdvancementDisplay fromPacket(PacketByteBuf buf) {
		Text text = buf.readText();
		Text text2 = buf.readText();
		ItemStack itemStack = buf.readItemStack();
		AdvancementFrame advancementFrame = buf.readEnumConstant(AdvancementFrame.class);
		int i = buf.readInt();
		Identifier identifier = (i & 1) != 0 ? buf.readIdentifier() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, false, bl2);
		advancementDisplay.setPosition(buf.readFloat(), buf.readFloat());
		return advancementDisplay;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.iconToJson());
		jsonObject.add("title", Text.Serializer.toJsonTree(this.title));
		jsonObject.add("description", Text.Serializer.toJsonTree(this.description));
		jsonObject.addProperty("frame", this.frame.getId());
		jsonObject.addProperty("show_toast", this.showToast);
		jsonObject.addProperty("announce_to_chat", this.announceToChat);
		jsonObject.addProperty("hidden", this.hidden);
		if (this.background != null) {
			jsonObject.addProperty("background", this.background.toString());
		}

		return jsonObject;
	}

	private JsonObject iconToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.getId(this.icon.getItem()).toString());
		if (this.icon.hasTag()) {
			jsonObject.addProperty("nbt", this.icon.getTag().toString());
		}

		return jsonObject;
	}
}
