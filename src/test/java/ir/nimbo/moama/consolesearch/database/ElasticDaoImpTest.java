package ir.nimbo.moama.consolesearch.database;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class ElasticDaoImpTest {
    private ElasticDaoImp elasticDaoImp;

    @Test
    public void search() {
        elasticDaoImp = new ElasticDaoImp();
        ArrayList<String> necessaryWords = new ArrayList<>();
        ArrayList<String> forbiddenWords = new ArrayList<>();
        ArrayList<String> preferredWords = new ArrayList<>();
        necessaryWords.add("iran");
        forbiddenWords.add("israel");
        forbiddenWords.add("U.S.A");
        preferredWords.add("world");
        Map<String, Float> searchResult = elasticDaoImp.search(necessaryWords,preferredWords,forbiddenWords);
        System.out.println(searchResult.size());
        Set<String> hits = searchResult.keySet();
        int i = 1;
        for(String hit: hits){
            System.out.println(i + "\t" + hit + "\t" + searchResult.get(hit));
            i++;
        }
    }
}