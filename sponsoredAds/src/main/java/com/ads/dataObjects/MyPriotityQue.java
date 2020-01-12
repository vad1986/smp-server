package com.ads.dataObjects;

import java.util.PriorityQueue;

public class MyPriotityQue<E> extends PriorityQueue {

    @Override
    public boolean add(Object o) {
        if(!campaignExists(o))
        return super.add(o);
        else return false;
    }

    private boolean campaignExists(Object o) {
        if (o == null){
            return false;
        }
        return super.contains(o);
    }


}
