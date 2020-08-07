package net.minecraft.data.client;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.Models;
import net.minecraft.data.client.model.Texture;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemModelGenerator {
	private final BiConsumer<Identifier, Supplier<JsonElement>> writer;

	public ItemModelGenerator(BiConsumer<Identifier, Supplier<JsonElement>> writer) {
		this.writer = writer;
	}

	private void register(Item item, Model model) {
		model.upload(ModelIds.getItemModelId(item), Texture.layer0(item), this.writer);
	}

	private void register(Item item, String suffix, Model model) {
		model.upload(ModelIds.getItemSubModelId(item, suffix), Texture.layer0(Texture.getSubId(item, suffix)), this.writer);
	}

	private void register(Item item, Item texture, Model model) {
		model.upload(ModelIds.getItemModelId(item), Texture.layer0(texture), this.writer);
	}

	public void register() {
		this.register(Items.field_8094, Models.field_22938);
		this.register(Items.field_8279, Models.field_22938);
		this.register(Items.field_8694, Models.field_22938);
		this.register(Items.field_8107, Models.field_22938);
		this.register(Items.field_8512, Models.field_22938);
		this.register(Items.BAMBOO, Models.field_22939);
		this.register(Items.field_8046, Models.field_22938);
		this.register(Items.field_8186, Models.field_22938);
		this.register(Items.field_8515, Models.field_22938);
		this.register(Items.field_8442, Models.field_22938);
		this.register(Items.field_8226, Models.field_22938);
		this.register(Items.field_8183, Models.field_22938);
		this.register(Items.field_8894, Models.field_22939);
		this.register(Items.field_8345, Models.field_22938);
		this.register(Items.field_8324, Models.field_22938);
		this.register(Items.field_8529, Models.field_22938);
		this.register(Items.field_8428, Models.field_22938);
		this.register(Items.field_8229, Models.field_22938);
		this.register(Items.field_8621, Models.field_22938);
		this.register(Items.field_8099, Models.field_22938);
		this.register(Items.field_8550, Models.field_22938);
		this.register(Items.field_8184, Models.field_22940);
		this.register(Items.field_23254, Models.field_22940);
		this.register(Items.field_8313, Models.field_22938);
		this.register(Items.field_8873, Models.field_22938);
		this.register(Items.field_8283, Models.field_22938);
		this.register(Items.field_8218, Models.field_22938);
		this.register(Items.field_8665, Models.field_22938);
		this.register(Items.field_8388, Models.field_22938);
		this.register(Items.field_8726, Models.field_22938);
		this.register(Items.field_8233, Models.field_22938);
		this.register(Items.field_8696, Models.field_22938);

		for (int i = 1; i < 64; i++) {
			this.register(Items.field_8557, String.format("_%02d", i), Models.field_22938);
		}

		this.register(Items.field_8713, Models.field_22938);
		this.register(Items.field_8666, Models.field_22938);
		this.register(Items.field_8220, Models.field_22938);

		for (int i = 0; i < 32; i++) {
			if (i != 16) {
				this.register(Items.field_8251, String.format("_%02d", i), Models.field_22938);
			}
		}

		this.register(Items.field_8176, Models.field_22938);
		this.register(Items.field_8544, Models.field_22938);
		this.register(Items.field_8373, Models.field_22938);
		this.register(Items.field_8347, Models.field_22938);
		this.register(Items.field_8261, Models.field_22938);
		this.register(Items.field_8752, Models.field_22938);
		this.register(Items.field_8509, Models.field_22938);
		this.register(Items.field_8423, Models.field_22938);
		this.register(Items.field_8573, Models.field_22938);
		this.register(Items.field_8632, Models.field_22938);
		this.register(Items.field_8138, Models.field_22938);
		this.register(Items.field_8477, Models.field_22938);
		this.register(Items.field_8556, Models.field_22939);
		this.register(Items.field_8285, Models.field_22938);
		this.register(Items.field_8058, Models.field_22938);
		this.register(Items.field_8805, Models.field_22938);
		this.register(Items.field_8527, Models.field_22939);
		this.register(Items.field_8807, Models.field_22938);
		this.register(Items.field_8348, Models.field_22938);
		this.register(Items.field_8377, Models.field_22939);
		this.register(Items.field_8250, Models.field_22939);
		this.register(Items.field_8802, Models.field_22939);
		this.register(Items.field_8613, Models.field_22938);
		this.register(Items.field_8551, Models.field_22938);
		this.register(Items.field_8803, Models.field_22938);
		this.register(Items.field_8687, Models.field_22938);
		this.register(Items.field_8598, Models.field_22938);
		this.register(Items.field_8449, Models.field_22938);
		this.register(Items.field_8634, Models.field_22938);
		this.register(Items.field_8301, Models.field_22938);
		this.register(Items.field_8287, Models.field_22938);
		this.register(Items.field_8711, Models.field_22938);
		this.register(Items.field_8639, Models.field_22938);
		this.register(Items.field_8814, Models.field_22938);
		this.register(Items.field_8145, Models.field_22938);
		this.register(Items.field_8884, Models.field_22938);
		this.register(Items.field_8498, Models.field_22938);
		this.register(Items.field_8063, Models.field_22938);
		this.register(Items.field_8070, Models.field_22938);
		this.register(Items.field_8469, Models.field_22938);
		this.register(Items.field_8597, Models.field_22938);
		this.register(Items.field_18674, Models.field_22938);
		this.register(Items.field_8601, Models.field_22938);
		this.register(Items.field_8463, Models.field_22938);
		this.register(Items.field_8825, Models.field_22939);
		this.register(Items.field_8753, Models.field_22938);
		this.register(Items.field_8071, Models.field_22938);
		this.register(Items.field_8678, Models.field_22938);
		this.register(Items.field_8862, Models.field_22938);
		this.register(Items.field_8303, Models.field_22939);
		this.register(Items.field_8560, Models.field_22938);
		this.register(Items.field_8416, Models.field_22938);
		this.register(Items.field_8335, Models.field_22939);
		this.register(Items.field_8322, Models.field_22939);
		this.register(Items.field_8845, Models.field_22939);
		this.register(Items.field_8695, Models.field_22938);
		this.register(Items.field_8397, Models.field_22938);
		this.register(Items.field_8298, Models.field_22938);
		this.register(Items.field_8408, Models.field_22938);
		this.register(Items.field_8054, Models.field_22938);
		this.register(Items.field_8207, Models.field_22938);
		this.register(Items.field_20414, Models.field_22938);
		this.register(Items.field_20417, Models.field_22938);
		this.register(Items.field_8836, Models.field_22938);
		this.register(Items.field_8794, Models.field_22938);
		this.register(Items.field_8475, Models.field_22939);
		this.register(Items.field_8660, Models.field_22938);
		this.register(Items.field_8523, Models.field_22938);
		this.register(Items.field_8743, Models.field_22938);
		this.register(Items.field_8609, Models.field_22939);
		this.register(Items.field_8578, Models.field_22938);
		this.register(Items.field_8620, Models.field_22938);
		this.register(Items.field_8396, Models.field_22938);
		this.register(Items.field_8675, Models.field_22938);
		this.register(Items.field_8403, Models.field_22939);
		this.register(Items.field_8699, Models.field_22939);
		this.register(Items.field_8371, Models.field_22939);
		this.register(Items.field_8143, Models.field_22938);
		this.register(Items.field_8730, Models.field_22938);
		this.register(Items.field_8361, Models.field_22938);
		this.register(Items.field_8759, Models.field_22938);
		this.register(Items.field_8187, Models.field_22938);
		this.register(Items.field_8745, Models.field_22938);
		this.register(Items.field_18138, Models.field_22938);
		this.register(Items.field_8273, Models.field_22938);
		this.register(Items.field_8851, Models.field_22938);
		this.register(Items.field_8131, Models.field_22938);
		this.register(Items.field_8669, Models.field_22938);
		this.register(Items.field_8135, Models.field_22938);
		this.register(Items.field_8895, Models.field_22938);
		this.register(Items.field_8497, Models.field_22938);
		this.register(Items.field_8103, Models.field_22938);
		this.register(Items.field_8045, Models.field_22938);
		this.register(Items.field_8159, Models.field_22938);
		this.register(Items.field_8208, Models.field_22938);
		this.register(Items.field_8731, Models.field_22938);
		this.register(Items.field_8144, Models.field_22938);
		this.register(Items.field_8425, Models.field_22938);
		this.register(Items.field_8075, Models.field_22938);
		this.register(Items.field_8623, Models.field_22938);
		this.register(Items.field_8502, Models.field_22938);
		this.register(Items.field_8534, Models.field_22938);
		this.register(Items.field_8344, Models.field_22938);
		this.register(Items.field_23984, Models.field_22938);
		this.register(Items.field_8834, Models.field_22938);
		this.register(Items.field_8065, Models.field_22938);
		this.register(Items.field_8806, Models.field_22938);
		this.register(Items.field_8355, Models.field_22938);
		this.register(Items.field_8748, Models.field_22938);
		this.register(Items.field_8448, Models.field_22938);
		this.register(Items.field_8864, Models.field_22938);
		this.register(Items.field_22025, Models.field_22939);
		this.register(Items.field_22030, Models.field_22938);
		this.register(Items.field_22028, Models.field_22938);
		this.register(Items.field_22027, Models.field_22938);
		this.register(Items.field_22026, Models.field_22939);
		this.register(Items.field_22020, Models.field_22938);
		this.register(Items.field_22029, Models.field_22938);
		this.register(Items.field_22024, Models.field_22939);
		this.register(Items.field_22021, Models.field_22938);
		this.register(Items.field_22023, Models.field_22939);
		this.register(Items.field_22022, Models.field_22939);
		this.register(Items.field_8729, Models.field_22938);
		this.register(Items.field_8137, Models.field_22938);
		this.register(Items.field_8533, Models.field_22938);
		this.register(Items.field_8492, Models.field_22938);
		this.register(Items.field_8892, Models.field_22938);
		this.register(Items.field_8407, Models.field_22938);
		this.register(Items.field_8614, Models.field_22938);
		this.register(Items.field_23831, Models.field_22938);
		this.register(Items.field_8330, Models.field_22938);
		this.register(Items.field_8635, Models.field_22938);
		this.register(Items.field_8882, Models.field_22938);
		this.register(Items.field_8389, Models.field_22938);
		this.register(Items.field_8434, Models.field_22938);
		this.register(Items.field_8662, Models.field_22938);
		this.register(Items.field_8323, Models.field_22938);
		this.register(Items.field_8108, Models.field_22938);
		this.register(Items.field_8741, Models.field_22938);
		this.register(Items.field_8296, Models.field_22938);
		this.register(Items.field_8155, Models.field_22938);
		this.register(Items.field_8504, Models.field_22938);
		this.register(Items.field_8073, Models.field_22938);
		this.register(Items.field_8245, Models.field_22938);
		this.register(Items.field_8308, Models.field_22938);
		this.register(Items.field_8264, Models.field_22938);
		this.register(Items.field_8511, Models.field_22938);
		this.register(Items.field_8175, Models.field_22938);
		this.register(Items.field_8209, Models.field_22938);
		this.register(Items.field_8714, Models.field_22938);
		this.register(Items.field_8161, Models.field_22938);
		this.register(Items.field_8868, Models.field_22938);
		this.register(Items.field_8815, Models.field_22938);
		this.register(Items.field_8891, Models.field_22938);
		this.register(Items.field_8777, Models.field_22938);
		this.register(Items.field_8543, Models.field_22938);
		this.register(Items.field_8236, Models.field_22938);
		this.register(Items.field_8680, Models.field_22938);
		this.register(Items.field_8486, Models.field_22938);
		this.register(Items.field_8600, Models.field_22939);
		this.register(Items.field_8062, Models.field_22939);
		this.register(Items.field_8431, Models.field_22939);
		this.register(Items.field_8387, Models.field_22939);
		this.register(Items.field_8776, Models.field_22939);
		this.register(Items.field_8528, Models.field_22939);
		this.register(Items.field_8479, Models.field_22938);
		this.register(Items.field_8766, Models.field_22938);
		this.register(Items.field_8069, Models.field_22938);
		this.register(Items.field_8288, Models.field_22938);
		this.register(Items.field_8547, Models.field_22938);
		this.register(Items.field_8846, Models.field_22938);
		this.register(Items.field_8478, Models.field_22938);
		this.register(Items.field_8090, Models.field_22938);
		this.register(Items.field_8705, Models.field_22938);
		this.register(Items.field_8861, Models.field_22938);
		this.register(Items.field_8446, Models.field_22938);
		this.register(Items.field_8406, Models.field_22939);
		this.register(Items.field_8167, Models.field_22939);
		this.register(Items.field_8647, Models.field_22939);
		this.register(Items.field_8876, Models.field_22939);
		this.register(Items.field_8091, Models.field_22939);
		this.register(Items.field_8674, Models.field_22938);
		this.register(Items.field_8360, Models.field_22938);
		this.register(Items.field_8192, Models.field_22938);
		this.register(Items.field_8688, Items.field_8600, Models.field_22939);
		this.register(Items.field_8367, Items.field_8463, Models.field_22938);
	}
}
