package naivebayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CounterMap {

	//全文書数
	private int docNum = 0;
	//カテゴリごとの文書数
	private Map<String, Integer> catDocNum = new HashMap<String,Integer>();
	//カテゴリごとの全単語数
	private Map<String, Integer> catWordNum = new HashMap<String,Integer>();
	//カテゴリの単語ごとの出現数
	private Map<String, Map<String,Integer>> catWordNumMap = new HashMap<String, Map<String,Integer>>();
	//カテゴリの集合
	public Set<String> categorySet = new HashSet<String>();
	//単語の集合
	public Set<String> vocabularies = new HashSet<String>();


	private void wordCounter(String category) {
		catWordNum.put(category, catWordNum.containsKey(category) ? catWordNum.get(category) + 1 : 1);
	}

	private void categoryWordCounter(String word,String category) {
		Map<String ,Integer> map = new HashMap<String,Integer>();

		if(catWordNumMap.containsKey(category)) {
			map = catWordNumMap.get(category);
			map.put(word, map.containsKey(word) ? map.get(word) + 1 : 1);
			catWordNumMap.put(category, map);
		} else {
			map.put(word, 1);
			catWordNumMap.put(category,map);
		}
	}

	private void docCounter(){
		docNum++;
	}

	private void categoryDocCounter(String category) {
		catDocNum.put(category, catDocNum.containsKey(category) ? catDocNum.get(category) + 1 : 1);
	}

	public void wordCounter(String word, String category) {
		wordCounter(category);
		categoryWordCounter(word, category);
		vocabularies.add(word);
	}

	public void docCounter(String category) {
		docCounter();
		categoryDocCounter(category);
		categorySet.add(category);
	}

	//事前分布を求める
	public double priorProb(String category) {
		return (double) catDocNum.get(category) / docNum;
	}

	//単語の出現確率を求める
	public double wordProb(String word,String category) {
		double prob = catWordNumMap.get(category).containsKey(word) ?  (double) catWordNumMap.get(category).get(word): 0.0;
		 return (prob + 1.0) / (catWordNum.get(category) + 1.0 * vocabularies.size());
	}

}
