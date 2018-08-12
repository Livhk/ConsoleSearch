package ir.nimbo.moama.consolesearch.console;

import asg.cliche.Command;
import asg.cliche.Param;
import ir.nimbo.moama.consolesearch.database.ElasticDao;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Console {
    ElasticDao elasticDao = new ElasticDao();
    String input = "";

    @Command(description = "Advanced Search- by necessary, forbidden and preferred statements")
    public void advancedSearch(){
        ArrayList<String> necessaryWords = new ArrayList<>();
        ArrayList<String> forbiddenWords = new ArrayList<>();
        ArrayList<String> preferredWords = new ArrayList<>();
        getInput(necessaryWords, " necessary");
        getInput(forbiddenWords, " forbidden");
        getInput(preferredWords, " preferred");
        Map<String,Float> results = elasticDao.search(necessaryWords,preferredWords,forbiddenWords);
        int i = 1;
        for(Map.Entry result: results.entrySet()){
            System.out.println(i + "\t" + result.getKey() + "\t" + result.getValue());
            i++;
        }
    }

    @Command(description = "Simple Search")
    public void simpleSearch(){
        ArrayList<String> words = new ArrayList<>();
        getInput(words, "");
        Map<String,Float> results = elasticDao.search(words, new ArrayList<>(), new ArrayList<>());
        int i = 1;
        for(Map.Entry result: results.entrySet()){
            System.out.println(i + "\t" + result.getKey() + "\t" + result.getValue());
            i++;
        }
    }

    public void getInput(ArrayList<String> list, String type){
        System.out.println("Please add you desired" + type + " words or phrases for search.");
        System.out.println("Please Finish entering input by typing : -done-");
        Scanner scanner = new Scanner(System.in);
        input = scanner.nextLine();
        while(!input.equals("-done-")){
            list.add(input);
            input = scanner.nextLine();
        }
    }

}
