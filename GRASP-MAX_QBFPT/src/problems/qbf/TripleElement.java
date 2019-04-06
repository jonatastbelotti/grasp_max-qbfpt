package problems.qbf;

public class TripleElement {
	public final Integer index;
	public Boolean selected;
	
	public TripleElement(int index)
	{
		this.index = index;
		this.selected = false;
	}

	public Boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Integer getIndex() {
		return index;
	}
}
