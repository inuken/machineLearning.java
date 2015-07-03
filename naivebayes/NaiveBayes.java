package naivebayes;

import java.util.ArrayList;
import java.util.List;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

public class NaiveBayes {
	CounterMap map = new CounterMap();

	private List<String> morphological(String doc) {
		List<String> words = new ArrayList<String>();
		Tokenizer tokenizer = Tokenizer.builder().build();

		for (Token token : tokenizer.tokenize(doc)) {
			if (token.getPartOfSpeech().contains("名詞"))
				words.add(token.getSurfaceForm());
		}
		return words;
	}

	private double calcScore(List<String> words ,String category) {
		double prob = Math.log(map.priorProb(category));

		for(String word : words) {
			prob += Math.log(map.wordProb(word, category));
		}
		return prob;
	}

	public String classifier(String doc) {
		String best = null;
		double bestScore = - Double.MAX_VALUE;
		double tmpScore;
		List<String> words = morphological(doc);

		for(String category : map.categorySet) {
			tmpScore = calcScore(words, category);

			if(tmpScore > bestScore) {
				best = category;
				bestScore = tmpScore;
			}
		}
		return best;
	}

	public void train(String doc, String category) {
		List<String> words = morphological(doc);

		for(String word : words) {
			map.wordCounter(word, category);
		}
		map.docCounter(category);
	}
}
