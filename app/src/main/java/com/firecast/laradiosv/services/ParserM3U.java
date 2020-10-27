package com.firecast.laradiosv.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class ParserM3U {

    public LinkedList<String> getRawUrl(String url) {
        LinkedList<String> linkedList = new LinkedList<String>();
        try {
            return getRawUrl(getConnection(url));
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        linkedList.add(url);
        return linkedList;
    }

    public LinkedList<String> getRawUrl(URLConnection conn) {

        final BufferedReader bufferedReader;
        String s = null;
        LinkedList<String> murls = new LinkedList<String>();
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
                        murls.add(s);
                    }
                } catch (IOException e) {

                }
            }
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        murls.add(conn.getURL().toString());
        return murls;
    }

    private String parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.indexOf("http") >= 0) {
            return trimmed.substring(trimmed.indexOf("http"));
        }
        return "";
    }

    private URLConnection getConnection(String url) throws MalformedURLException, IOException {
        URLConnection mUrl = new URL(url).openConnection();
        return mUrl;
    }

}
