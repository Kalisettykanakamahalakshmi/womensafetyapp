package com.example.mounika;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SafetyTipsManager {

    public static final String EXTRA_CATEGORY_TITLE = "" ;
    public static final String EXTRA_SAFETY_TIPS = "";
    private final Context context;
    private final HashMap<String, String> safetyTipsMap = new HashMap<>();

    public SafetyTipsManager(Context context) {
        this.context = context;
    }

    public HashMap<String, String> getSafetyTipsMap() {
        return safetyTipsMap;
    }

    public void loadSafetyTips() {
        try {
            // Read General Safety Tips
            String generalTips = readTipsFromFile("general_tips.txt");
            safetyTipsMap.put("General", generalTips);

            // Read Travel Safety Tips
            String travelTips = readTipsFromFile("travel_tips.txt");
            safetyTipsMap.put("Travel", travelTips);

            // Read Personal Security Tips
            String personalSecurityTips = readTipsFromFile("personal security_tips.txt");
            safetyTipsMap.put("Personal Security", personalSecurityTips);

            // Read Self-Defense Techniques Tips
            String selfDefenseTips = readTipsFromFile("self-defense techniques_tips.txt");
            safetyTipsMap.put("Self-Defense Techniques", selfDefenseTips);

            // Read Online Safety Tips
            String onlineSafetyTips = readTipsFromFile("online safety_tips.txt");
            safetyTipsMap.put("Online Safety", onlineSafetyTips);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readTipsFromFile(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }
}
