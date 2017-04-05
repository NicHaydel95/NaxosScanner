package com.example.nhaydel.naxosscanner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class NaxosSearch extends AppCompatActivity {
    String author;
    String song;
    TextView mSongName;
    WebView webView;
    TextView mAuthor;
    String artistSearchURL="http://und.naxosmusiclibrary.com.proxy.library.nd.edu/artistlist.asp?filter=";
    String composerURL="http://und.naxosmusiclibrary.com.proxy.library.nd.edu/composer/btm.asp?composerid=";
    List<String> playerLinks = new ArrayList<String>();
    ArrayList<String> worksLinks= new ArrayList<>();
    ListView links;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naxos_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        author = getIntent().getStringExtra("AUTHOR");
        song = getIntent().getStringExtra("SONG");
        //mSongName = (TextView) findViewById(R.id.song_name);
        //mAuthor = (TextView) findViewById(R.id.song_author);
        // mSongName.setText(song);
        // mAuthor.setText(author);
        song = song.split(":")[0];
        String url = getArtistURL();
        try {
            enableSSLSocket();
        } catch (Exception e) {
            System.out.println("Failed to enable SSL con");
        }
        new GetAuthorLink(url, parseAuthor()).execute();
    }
    public String getArtistURL(){
        return artistSearchURL+author.toLowerCase().charAt(0);
    }
    public String parseAuthor(){
        List<String> terms = Arrays.asList(author.split(","));
        return terms.get(0)+","+terms.get(1);
    }


    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
    public String getID(String url){
        return url.substring(url.lastIndexOf("=")+1);
    }
    class GetAuthorLink extends AsyncTask<Void,Void,String> {
        String artistSearchURL;
        String author;
        public GetAuthorLink(String url, String auth){
            artistSearchURL = url;
            System.out.println(url);
            author = auth;

        }
        @Override
        protected String doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(artistSearchURL).get();
                Elements links = doc.select("a");
                String convertedString =
                        Normalizer
                                .normalize(author, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "");
                for (Element link : links) {
                    if (link.text().contains(convertedString)){
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
            id=getID(result);
            composerURL=composerURL+id;
            new GetSongLink().execute();
        }
    }
    class GetSongLink extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(composerURL).get();
                Elements links = doc.select("a");
                String convertedString =
                        Normalizer
                                .normalize(song, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "");
                for (Element link : links) {
                    if (link.text().contains(convertedString.trim())){
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
            new GetWorksLink(result).execute();
        }
    }class GetWorksLink extends AsyncTask<Void,Void,String> {
        String url="http://und.naxosmusiclibrary.com.proxy.library.nd.edu/include/inc.cataloguereference.asp?wid=";
        String wid;
        GetWorksLink(String u){
            wid = u.substring(u.lastIndexOf("=")+1);
            url = url+wid;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                Document doc = Jsoup.connect(url).get();
                String convertedString =
                        Normalizer
                                .normalize(song, Normalizer.Form.NFD)
                                .replaceAll("[^\\p{ASCII}]", "");
                Elements links = doc.select("a");
                for (Element link : links) {
                    if (link.text().contains(convertedString)) {
                        worksLinks.add(link.attr("abs:href").toString());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Nothing found";
        }
        @Override
        protected void onPostExecute(String result) {
            new GetPlayerLinks().execute();
        }
    }
    class GetPlayerLinks extends AsyncTask<Void,Void,String> {
        String URL;
        @Override
        protected String doInBackground(Void... params) {
            try {
                for (String link : worksLinks) {
                    URL = link;
                    Document doc = Jsoup.connect(URL).get();
                    String convertedString =
                            Normalizer
                                    .normalize(song, Normalizer.Form.NFD)
                                    .replaceAll("[^\\p{ASCII}]", "");
                    Elements cells = doc.select("td");
                    for (Element cell : cells) {
                        if (cell.text().contains(convertedString) && Character.isDigit(cell.text().toString().charAt(0))) {
                            List<String> words = Arrays.asList(cell.text().split(" "));
                            for (String word : words) {
                                if (word.contains("http://")) {
                                    playerLinks.add(word);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Nothing found";
        }
        @Override
        protected void onPostExecute(String result) {
            links = (ListView) findViewById(R.id.links_list);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.link_item,
                    playerLinks );

            links.setAdapter(arrayAdapter);
            findViewById(R.id.spinner).setVisibility(View.GONE);
            links.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    Object item = links.getItemAtPosition(position);
                    String url = (String) item;
                    webView = new WebView(getBaseContext());
                    webView.getSettings().setJavaScriptEnabled(true);
                    setContentView(webView);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
                super.onBackPressed();
        }
    }


}

