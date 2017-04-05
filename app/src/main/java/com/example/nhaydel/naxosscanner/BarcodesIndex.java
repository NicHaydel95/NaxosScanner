package com.example.nhaydel.naxosscanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by nhaydel on 4/5/17.
 */

public class BarcodesIndex {
    HashMap<String, List<String>> index;
    static BarcodesIndex b_index;
    private BarcodesIndex(){
        index = new HashMap<>();
    }

    public static BarcodesIndex getInstance(){
        if (b_index!=null){
            return b_index;
        }
        else {
            b_index = new BarcodesIndex();
        }
        return b_index;
    }

    public void add(String code, List<String> urls){
        index.put(code,urls);
    }

    public List<String> get(String code) {
        return index.get(code);
    }
}
