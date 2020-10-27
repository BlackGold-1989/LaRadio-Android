package com.firecast.laradiosv.services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class ParserASX {

    public LinkedList<String> getRawUrl(String url) {
        LinkedList<String> linkedList = null;
        try {
            return getRawUrl(getConnection(url));
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return linkedList;
    }

    public LinkedList<String> getRawUrl(URLConnection conn) {

        final BufferedReader bufferedReader;
        String s = null;
        LinkedList<String> linkedList = null;
        linkedList = new LinkedList<String>();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    s = parseLine(line);
                    if (s != null && !s.equals("")) {
                        linkedList.add(s);
                    }
                } catch (IOException e) {

                }
            }
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }

        return linkedList;
    }

    private String parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.startsWith("<ref href=\"")) {
            trimmed = trimmed.replace("<ref href=\"", "");
            trimmed = trimmed.replace("/>", "").trim();
            if (trimmed.endsWith("\"")) {
                trimmed = trimmed.replace("\"", "");
                Log.v("INFO", "ASX: " + trimmed);
                return trimmed;
            }
        }
        return "";
    }

    private URLConnection getConnection(String url) throws MalformedURLException, IOException {
        URLConnection mUrl = new URL(url).openConnection();
        return mUrl;
    }

}
