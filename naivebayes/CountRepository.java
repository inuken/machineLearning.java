package naivebayes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CountRepository {
	// 出現単語の集合
	Set<String> vocabularies = new HashSet<String>();
	// カテゴリの単語出現回数
	Map<String, Map<String, Integer>> wordCount = new HashMap<String, Map<String, Integer>>();
	// カテゴリの出現回数
	Map<String, Integer> catCount = new HashMap<String, Integer>();

	//カテゴリの単語の出現回数を数える
	public void wordCountUp(String word, String cat) {
		if (!this.wordCount.containsKey(cat)) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put(word, 1);
			this.wordCount.put(cat, map);
		} else {
			Map<String, Integer> map = this.wordCount.get(cat);
			map.put(word, map.containsKey(word) ? map.get(word) + 1 : 1);
			this.wordCount.put(cat, map);
		}
	}

	// カテゴリの出現回数を数える
	public void catCountUp(String cat) {
		this.catCount
				.put(cat,
						this.catCount.containsKey(cat) ? this.catCount.get(cat) + 1
								: 1);
	}

	// カテゴリ数を返す
	public int sumCatValues() {
		int sum = 0;
		for (Entry<String, Integer> e : this.catCount.entrySet()) {
			sum += e.getValue();
		}
		return sum;
	}

	// カテゴリの出現回数を返す
	public double inCategory(String word, String cat) {
		if (this.wordCount.get(cat).containsKey(word))
			return this.wordCount.get(cat).get(word);
		return 0;
	}
}
