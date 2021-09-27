package census;

public interface WordCensus {
	public int getCount(String word);
	//part of pre: i > 0
	//part of post: i < getDistinctWordCount() ==> getCount(getWordWithRank(i)) >= getCount(getWordWithRank(i + 1))
	public String getWordWithRank(int i);
	public int getDistinctWordCount();
}
