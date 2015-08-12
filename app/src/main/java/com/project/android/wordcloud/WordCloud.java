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

    public HashMap<String,double[]> myWC;

    public WordCloud(String text){
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
        cloud.setMaxTagsToDisplay(50);
        cloud.setTagCase(Cloud.Case.UPPER);
        cloud.addText(sb.toString());
        int ind = 0;
        myWC = new HashMap<>();
        for(Tag tag: cloud.tags()){
            double[] info = {tag.getWeight(),ind,ind+tag.getName().length()};
            myWC.put(tag.getName(),info);
            ind += tag.getName().length() + 1;
        }
        //StringBuilder html = new StringBuild
        // er();

        /*
        int lineCount = 0;

        for(Tag tag: cloud.tags()){
            //System.out.println(t.getName());
            html.append("<a><font size=" + tag.getWeight() +
                    "px>" + tag.getName() + "&nbsp</font></a>");

            lineCount++;
            if(lineCount%10==0){
                lineCount = 0;
                html.append("<br />");
            }
        }
        myWC = html.toString();
        */
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
