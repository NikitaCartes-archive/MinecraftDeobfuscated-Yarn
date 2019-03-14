package net.minecraft.client.render;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class VertexFormat {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<VertexFormatElement> elements = Lists.<VertexFormatElement>newArrayList();
	private final List<Integer> offsets = Lists.<Integer>newArrayList();
	private int size;
	private int offsetColor = -1;
	private final List<Integer> offsetsUv = Lists.<Integer>newArrayList();
	private int offsetNormal = -1;

	public VertexFormat(VertexFormat vertexFormat) {
		this();

		for (int i = 0; i < vertexFormat.getElementCount(); i++) {
			this.add(vertexFormat.getElement(i));
		}

		this.size = vertexFormat.getVertexSize();
	}

	public VertexFormat() {
	}

	public void clear() {
		this.elements.clear();
		this.offsets.clear();
		this.offsetColor = -1;
		this.offsetsUv.clear();
		this.offsetNormal = -1;
		this.size = 0;
	}

	public VertexFormat add(VertexFormatElement vertexFormatElement) {
		if (vertexFormatElement.isPosition() && this.hasPositionElement()) {
			LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
			return this;
		} else {
			this.elements.add(vertexFormatElement);
			this.offsets.add(this.size);
			switch (vertexFormatElement.getType()) {
				case NORMAL:
					this.offsetNormal = this.size;
					break;
				case COLOR:
					this.offsetColor = this.size;
					break;
				case UV:
					this.offsetsUv.add(vertexFormatElement.getIndex(), this.size);
			}

			this.size = this.size + vertexFormatElement.getSize();
			return this;
		}
	}

	public boolean hasNormalElement() {
		return this.offsetNormal >= 0;
	}

	public int getNormalOffset() {
		return this.offsetNormal;
	}

	public boolean hasColorElement() {
		return this.offsetColor >= 0;
	}

	public int getColorOffset() {
		return this.offsetColor;
	}

	public boolean hasUvElement(int i) {
		return this.offsetsUv.size() - 1 >= i;
	}

	public int getUvOffset(int i) {
		return (Integer)this.offsetsUv.get(i);
	}

	public String toString() {
		String string = "format: " + this.elements.size() + " elements: ";

		for (int i = 0; i < this.elements.size(); i++) {
			string = string + ((VertexFormatElement)this.elements.get(i)).toString();
			if (i != this.elements.size() - 1) {
				string = string + " ";
			}
		}

		return string;
	}

	private boolean hasPositionElement() {
		int i = 0;

		for (int j = this.elements.size(); i < j; i++) {
			VertexFormatElement vertexFormatElement = (VertexFormatElement)this.elements.get(i);
			if (vertexFormatElement.isPosition()) {
				return true;
			}
		}

		return false;
	}

	public int getVertexSizeInteger() {
		return this.getVertexSize() / 4;
	}

	public int getVertexSize() {
		return this.size;
	}

	public List<VertexFormatElement> getElements() {
		return this.elements;
	}

	public int getElementCount() {
		return this.elements.size();
	}

	public VertexFormatElement getElement(int i) {
		return (VertexFormatElement)this.elements.get(i);
	}

	public int getElementOffset(int i) {
		return (Integer)this.offsets.get(i);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			VertexFormat vertexFormat = (VertexFormat)object;
			if (this.size != vertexFormat.size) {
				return false;
			} else {
				return !this.elements.equals(vertexFormat.elements) ? false : this.offsets.equals(vertexFormat.offsets);
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.elements.hashCode();
		i = 31 * i + this.offsets.hashCode();
		return 31 * i + this.size;
	}
}
