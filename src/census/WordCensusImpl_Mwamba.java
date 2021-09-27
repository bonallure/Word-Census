package census;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class WordCensusImpl_Mwamba implements WordCensus {
	private Map<String, Integer> wordTFM;
	private List<Map.Entry<String, Integer>> sortedMapEntryList;
	
	public WordCensusImpl_Mwamba(List<String> wordList)
	{
		Set<String> distinctWordsSet =  new HashSet<>(wordList);
		Iterator<String> distinctWordsIterator = distinctWordsSet.iterator();
		
		// initializing wordTFM with the initial values of 0
		wordTFM = new HashMap<>();
		while (distinctWordsIterator.hasNext())
			wordTFM.put(distinctWordsIterator.next(), 0);
		
		Iterator<String> wordListIterator = wordList.iterator();
		
		// updating the values
		while (wordListIterator.hasNext())
			incrementCount(wordListIterator.next());
		
		sortedMapEntryList = sortEntriesByKey(sortEntriesByRank(wordTFM.entrySet()));
	}
	 
	@Override
	public int getCount(String word) {
		if (! wordTFM.containsKey(word))
			return 0;
		
		int count = wordTFM.get(word);
		return count;
	}
	
	//part of pre: i > 0
	//part of post: i < getDistinctWordCount() ==> getCount(getWordWithRank(i)) >= getCount(getWordWithRank(i + 1))
	public String getWordWithRank(int i)
	{	
		assert i > 0 : "i must be greater than 0";
		Map.Entry<String, Integer> iRankEntry = sortedMapEntryList.get(i-1);
		String wordWithRankI = iRankEntry.getKey();

		return wordWithRankI;
	}

	public int getDistinctWordCount()
	{
		return wordTFM.size();
	}
	
	private void incrementCount(String word)
	{
		int currentValue = this.wordTFM.get(word);
		int newValue = currentValue + 1;
		this.wordTFM.put(word, newValue);
	}

	private List<Map.Entry<String, Integer>> sortEntriesByRank(Set<Map.Entry<String, Integer>> entrySet){
		
		List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(entrySet.size());
		
		while (entrySet.size() > 0) {
			int highestRankedMention = 0;
			Entry<String, Integer> highestRankedEntry = null;
			
			for (Entry<String, Integer> entry : entrySet) 
			{
				int mentions = entry.getValue();
				if (mentions > highestRankedMention) 
				{
					highestRankedMention = mentions;
					highestRankedEntry = entry;
				}
			}
			sortedList.add(highestRankedEntry);
			entrySet.remove(highestRankedEntry);
		}
		for (int i =0; i < sortedList.size(); i++) 
		{
			wordTFM.put(sortedList.get(i).getKey(), sortedList.get(i).getValue());
		}
		return sortedList;
	}
	
	private List<List<Map.Entry<String, Integer>>> groupEntriesByRank(List<Map.Entry<String, Integer>> entryList){
		List<List<Map.Entry<String, Integer>>> rankedEntryList = new ArrayList<>();
		List<Map.Entry<String, Integer>> tempList = new ArrayList<>();
		tempList.add(entryList.get(0));
		
		for (int i = 1; i < entryList.size(); i++) {
			Map.Entry<String, Integer> currentEntry = entryList.get(i);
			Map.Entry<String, Integer> previousEntry = entryList.get(i-1);
			
			if (currentEntry.getValue() == previousEntry.getValue()) 
			{
				tempList.add(currentEntry);
			}
			else 
			{
				rankedEntryList.add(tempList);
				tempList = new ArrayList<>();
				tempList.add(currentEntry);
			}
		}
		return rankedEntryList;
	}
	
	private List<Entry<String, Integer>> sortEntriesByKey(List<Map.Entry<String, Integer>> list){
		List<List<Map.Entry<String, Integer>>> rankedEntryList = groupEntriesByRank(list);
		
		List<Map.Entry<String, Integer>> superSortedEntryList = new ArrayList<>(this.getDistinctWordCount());
		
		for (int j = 0; j < rankedEntryList.size(); j++) 
		{
			List<Map.Entry<String, Integer>> rankList = rankedEntryList.get(j);
			
			if (rankList.size() == 0)
				superSortedEntryList.add(rankList.get(0));
			else 
			{
				while (rankList.size() > 0)
				{
					Map.Entry<String, Integer> smallestEntry = rankList.get(0);
					for (int k = 1; k < rankList.size(); k++)
					{
						if (smallestEntry.getKey().compareToIgnoreCase(rankList.get(k).getKey()) > 0)
							smallestEntry = rankList.get(k);
					}
					superSortedEntryList.add(smallestEntry);
					rankList.remove(smallestEntry);
				}
			}
		}
		return superSortedEntryList;
	}
}
