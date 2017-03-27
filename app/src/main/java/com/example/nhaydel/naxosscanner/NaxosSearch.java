package com.example.nhaydel.naxosscanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NaxosSearch extends AppCompatActivity {
    String author;
    String song;
    TextView mSongName;
    TextView mAuthor;
    String artistSearchURL="http://und.naxosmusiclibrary.com.proxy.library.nd.edu/artistlist.asp?filter=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naxos_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        author = getIntent().getStringExtra("AUTHOR");
        song = getIntent().getStringExtra("SONG");
        mSongName = (TextView) findViewById(R.id.song_name);
        mAuthor = (TextView) findViewById(R.id.song_author);
        mSongName.setText(song);
        mAuthor.setText(author);
        String url = getArtistURL();
        new GetAuthorLink(url,parseAuthor()).execute();
    }
    public String getArtistURL(){
        return artistSearchURL+author.toLowerCase().charAt(0);
    }
    public String parseAuthor(){
        List<String> terms = Arrays.asList(author.split(","));
        return terms.get(0)+","+terms.get(1);
    }
}

class GetAuthorLink extends AsyncTask<Void,Void,String> {
    String artistSearchURL;
    String author;
    public GetAuthorLink(String url, String auth){
        artistSearchURL = url;
        System.out.println(auth);
        author = auth;

    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            Document doc = Jsoup.connect(artistSearchURL).get();
            System.out.println(doc.title());
            Elements links = doc.select("a");
            for (Element link : links) {
                System.out.println(link.text());
                if (link.text().contains(author)){
                    return link.attr("abs:href");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Nothing found";
    }
    @Override
    protected void onPostExecute(String result) {
        //if you had a ui element, you could display the title
        System.out.println(result);
    }
}
