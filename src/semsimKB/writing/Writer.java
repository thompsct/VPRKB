package semsimKB.writing;

import semsimKB.model.kbbuffer.KnowledgeBase;

public interface Writer {
	public String writeToString(KnowledgeBase kb);
}
