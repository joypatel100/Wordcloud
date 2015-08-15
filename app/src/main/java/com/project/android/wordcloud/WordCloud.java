package com.project.android.wordcloud;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Joy on 8/11/15.
 */
public class WordCloud {

    // Generate Actual Word Cloud from Input String

    public HashMap<String,double[]> myWC;

    public WordCloud(String text, int numWords){
        String[] allWords = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for(String word: allWords){
            if(MainActivity.badWords.contains(word)){
                continue;
            }
            sb.append(word + " ");
        }
        Cloud cloud = new Cloud();
        cloud.setMaxWeight(3);
        cloud.setMinWeight(.5);
        cloud.setMaxTagsToDisplay(numWords);
        cloud.setTagCase(Cloud.Case.UPPER);
        cloud.addText(sb.toString());
        int ind = 0;
        myWC = new HashMap<>();
        for(Tag tag: cloud.tags()){
            double[] info = {tag.getWeight(),ind,ind+tag.getName().length()};
            myWC.put(tag.getName(),info);
            ind += tag.getName().length() + 1;
        }
    }

    public String words(){
        StringBuilder result = new StringBuilder();
        ArrayList<String> names = new ArrayList<>(myWC.keySet());
        Collections.sort(names);
        for(String name: names){
            result.append(name + " ");
        }
        return result.toString();

    }
}
