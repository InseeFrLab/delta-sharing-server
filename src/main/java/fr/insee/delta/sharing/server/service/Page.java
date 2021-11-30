package fr.insee.delta.sharing.server.service;

public class Page {

    Integer start;
    Integer size;
    Integer end;
    Integer totalSize;
    
    
	public Page(Integer start, Integer size, Integer totalSize) {
		super();
		this.start = start;
		this.size = size;
		this.end = Math.min(start+ size, totalSize);
		this.totalSize = totalSize;
	}


	public Integer getStart() {
		return start;
	}


	public void setStart(Integer start) {
		this.start = start;
	}


	public Integer getSize() {
		return size;
	}


	public void setSize(Integer size) {
		this.size = size;
	}


	public Integer getEnd() {
		return end;
	}


	public void setEnd(Integer end) {
		this.end = end;
	}


	public Integer getTotalSize() {
		return totalSize;
	}


	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public String getNextId() {
		if(this.end < this.totalSize) {
			return this.end.toString();
		}else {
			return null;
		}
	}


	@Override
	public String toString() {
		return "Page [start=" + start + ", size=" + size + ", end=" + end + ", totalSize=" + totalSize + "]";
	}
	
	

}
