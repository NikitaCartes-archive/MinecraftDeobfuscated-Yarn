package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VertexFormatElement {
	private static final Logger LOGGER = LogManager.getLogger();
	private final VertexFormatElement.Format format;
	private final VertexFormatElement.Type type;
	private final int index;
	private final int count;
	private final int field_21329;

	public VertexFormatElement(int i, VertexFormatElement.Format format, VertexFormatElement.Type type, int j) {
		if (this.isValidType(i, type)) {
			this.type = type;
		} else {
			LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
			this.type = VertexFormatElement.Type.UV;
		}

		this.format = format;
		this.index = i;
		this.count = j;
		this.field_21329 = format.getSize() * this.count;
	}

	private boolean isValidType(int i, VertexFormatElement.Type type) {
		return i == 0 || type == VertexFormatElement.Type.UV;
	}

	public final VertexFormatElement.Format getFormat() {
		return this.format;
	}

	public final VertexFormatElement.Type getType() {
		return this.type;
	}

	public final int getCount() {
		return this.count;
	}

	public final int getIndex() {
		return this.index;
	}

	public String toString() {
		return this.count + "," + this.type.getName() + "," + this.format.getName();
	}

	public final int getSize() {
		return this.field_21329;
	}

	public final boolean isPosition() {
		return this.type == VertexFormatElement.Type.POSITION;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)object;
			if (this.count != vertexFormatElement.count) {
				return false;
			} else if (this.index != vertexFormatElement.index) {
				return false;
			} else {
				return this.format != vertexFormatElement.format ? false : this.type == vertexFormatElement.type;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.format.hashCode();
		i = 31 * i + this.type.hashCode();
		i = 31 * i + this.index;
		return 31 * i + this.count;
	}

	public void method_22652(long l, int i) {
		this.type.method_22655(this.count, this.format.getGlId(), i, l, this.index);
	}

	public void method_22653() {
		this.type.method_22654(this.index);
	}

	@Environment(EnvType.CLIENT)
	public static enum Format {
		FLOAT(4, "Float", 5126),
		UBYTE(1, "Unsigned Byte", 5121),
		BYTE(1, "Byte", 5120),
		USHORT(2, "Unsigned Short", 5123),
		SHORT(2, "Short", 5122),
		UINT(4, "Unsigned Int", 5125),
		INT(4, "Int", 5124);

		private final int size;
		private final String name;
		private final int glId;

		private Format(int j, String string2, int k) {
			this.size = j;
			this.name = string2;
			this.glId = k;
		}

		public int getSize() {
			return this.size;
		}

		public String getName() {
			return this.name;
		}

		public int getGlId() {
			return this.glId;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		POSITION("Position", (i, j, k, l, m) -> {
			GlStateManager.vertexPointer(i, j, k, l);
			GlStateManager.enableClientState(32884);
		}, i -> GlStateManager.disableClientState(32884)),
		NORMAL("Normal", (i, j, k, l, m) -> {
			GlStateManager.normalPointer(j, k, l);
			GlStateManager.enableClientState(32885);
		}, i -> GlStateManager.disableClientState(32885)),
		COLOR("Vertex Color", (i, j, k, l, m) -> {
			GlStateManager.colorPointer(i, j, k, l);
			GlStateManager.enableClientState(32886);
		}, i -> {
			GlStateManager.disableClientState(32886);
			GlStateManager.clearCurrentColor();
		}),
		UV("UV", (i, j, k, l, m) -> {
			GlStateManager.clientActiveTexture(33984 + m);
			GlStateManager.texCoordPointer(i, j, k, l);
			GlStateManager.enableClientState(32888);
			GlStateManager.clientActiveTexture(33984);
		}, i -> {
			GlStateManager.clientActiveTexture(33984 + i);
			GlStateManager.disableClientState(32888);
			GlStateManager.clientActiveTexture(33984);
		}),
		PADDING("Padding", (i, j, k, l, m) -> {
		}, i -> {
		}),
		GENERIC("Generic", (i, j, k, l, m) -> {
			GlStateManager.method_22606(m);
			GlStateManager.method_22609(m, i, j, false, k, l);
		}, GlStateManager::method_22607);

		private final String name;
		private final VertexFormatElement.Type.class_4575 field_20783;
		private final IntConsumer field_20784;

		private Type(String string2, VertexFormatElement.Type.class_4575 arg, IntConsumer intConsumer) {
			this.name = string2;
			this.field_20783 = arg;
			this.field_20784 = intConsumer;
		}

		private void method_22655(int i, int j, int k, long l, int m) {
			this.field_20783.setupBufferState(i, j, k, l, m);
		}

		public void method_22654(int i) {
			this.field_20784.accept(i);
		}

		public String getName() {
			return this.name;
		}

		@Environment(EnvType.CLIENT)
		interface class_4575 {
			void setupBufferState(int i, int j, int k, long l, int m);
		}
	}
}
