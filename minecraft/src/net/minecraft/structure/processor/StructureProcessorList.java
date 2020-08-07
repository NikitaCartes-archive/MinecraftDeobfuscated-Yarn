package net.minecraft.structure.processor;

import java.util.List;

public class StructureProcessorList {
	private final List<StructureProcessor> list;

	public StructureProcessorList(List<StructureProcessor> list) {
		this.list = list;
	}

	public List<StructureProcessor> getList() {
		return this.list;
	}

	public String toString() {
		return "ProcessorList[" + this.list + "]";
	}
}
