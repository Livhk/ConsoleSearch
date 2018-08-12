package ir.nimbo.moama.consolesearch.database;


import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ElasticDao {
    private RestHighLevelClient client;
    private String index = "pages";
    private Logger errorLogger = Logger.getLogger("error");

    public ElasticDao() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("94.23.214.93", 9200, "http")));
    }


    public Map<String, Float> search(ArrayList<String> necessaryWords, ArrayList<String> preferredWords, ArrayList<String> forbiddenWords) {
        Map<String, Float> results = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.types("_doc");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(String necessaryWord:necessaryWords) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("pageLink", necessaryWord));
        }
        for(String preferredWord:preferredWords) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("pageText", preferredWord));
        }
        for(String forbiddenWord:forbiddenWords) {
            boolQueryBuilder.mustNot(QueryBuilders.matchQuery("pageText", forbiddenWord));
        }
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(20);
        sourceBuilder.timeout(new TimeValue(5, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        boolean searchStatus = false;
        while (!searchStatus) {
            try {
                searchResponse = client.search(searchRequest);
                searchStatus = true;
            } catch (IOException e) {
                System.out.println("Elastic connection timed out! Trying again...");
                searchStatus = false;
            }
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        int i = 1;
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            results.put((String) sourceAsMap.get("pageLink"), hit.getScore());
        }
        return sortByValues(results);
    }

    private static Map<String, Float> sortByValues(Map<String, Float> map) {
        List<Map.Entry<String, Float>> list = new LinkedList<>(map.entrySet());
        list.sort(new Compare());
        Map<String, Float> sortedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;

    }

    public Map<String, Float> findSimilar(String text) {
        Map<String, Float> results = new HashMap<>();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] fields = {"pageText"};
        String[] texts = {text};
        searchSourceBuilder.query(QueryBuilders.moreLikeThisQuery(fields, texts, null).minTermFreq(1));
        searchSourceBuilder.size(20);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        boolean searchStatus = false;
        while (!searchStatus) {
            try {
                searchResponse = client.search(searchRequest);
                searchStatus = true;
            } catch (IOException e) {
                System.out.println("Elastic connection timed out! Trying again...");
                searchStatus = false;
            }
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        int i = 1;
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            results.put((String) sourceAsMap.get("pageLink"), hit.getScore());
        }
        return sortByValues(results);
    }

}


class Compare implements Comparator{

    @Override
    public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o2)).getValue())
                .compareTo(((Map.Entry) (o1)).getValue());
    }
}