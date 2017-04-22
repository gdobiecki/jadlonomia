package com.czesio.basic;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://www.jadlonomia.com/rodzaj_dania/dania-glowne/";
    private static final String BASE_URL_SUFIX = "page/";
    private static final String USER_AGENT = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    private static final String LINK_SELECTOR = "h2 > a[href]";
    private static final String HREF_ATTRIBUTE = "href";
    private static List<String> links = new ArrayList<>();
    private static int pageNo;
    private static List<String> availableLinks;

    private Document doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            availableLinks = selectAllLinks();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(selectRandomLink(availableLinks));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close_app) {
            finish();
            System.exit(0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static String selectRandomLink(List<String> links) {
        Random random = new Random();
        int index = random.nextInt(links.size());
        System.out.println(links.get(index));

        return links.get(index);
    }

    private static List<String> selectAllLinks() throws Exception {
        int code = connectToJadlonomia();

        while (code == HttpURLConnection.HTTP_OK) {
            String uri = BASE_URL + BASE_URL_SUFIX + pageNo;
            URL url = new URL(uri);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
            httpURLConnection.connect();
            code = httpURLConnection.getResponseCode();
            pageNo++;

            if (code == HttpURLConnection.HTTP_OK) {
                Document doc = Jsoup.connect(uri).get();
                Elements allLinks = doc.select(LINK_SELECTOR);
                for (Element link : allLinks) {
                    links.add(link.attr(HREF_ATTRIBUTE));
                }
            }
        }

        return links;
    }

    private static int connectToJadlonomia() throws IOException {
        pageNo = 1;
        String uri = BASE_URL + BASE_URL_SUFIX + pageNo;
        URL url = new URL(uri);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
        httpURLConnection.connect();

        return httpURLConnection.getResponseCode();
    }

}
