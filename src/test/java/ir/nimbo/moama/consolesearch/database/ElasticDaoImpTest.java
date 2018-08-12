package ir.nimbo.moama.consolesearch.database;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ElasticDaoImpTest {
    private ElasticDao elasticDaoImp;

    @Test
    public void search() {
        elasticDaoImp = new ElasticDao();
        ArrayList<String> necessaryWords = new ArrayList<>();
        ArrayList<String> forbiddenWords = new ArrayList<>();
        ArrayList<String> preferredWords = new ArrayList<>();
        necessaryWords.add("youtube");
//        forbiddenWords.add("israel");
//        forbiddenWords.add("U.S.A");
//        preferredWords.add("world");
        Map<String, Float> searchResult = elasticDaoImp.search(necessaryWords,preferredWords,forbiddenWords);
        System.out.println(searchResult.size());
        Set<String> hits = searchResult.keySet();
        int i = 1;
        for(String hit: hits){
            System.out.println(i + "\t" + hit + "\t" + searchResult.get(hit));
            i++;
        }
    }

    @Test
    public void findSimilar() {
        elasticDaoImp = new ElasticDao();
        Map<String, Float> searchResult = elasticDaoImp.findSimilar("fire climate southern america");
        System.out.println(searchResult.size());
        Set<String> hits = searchResult.keySet();
        int i = 1;
        for(String hit: hits){
            System.out.println(i + "\t" + hit + "\t" + searchResult.get(hit));
            i++;
        }
    }
}