package com.example.inhamap.Models;

public class TupleLong {
    private long prev;
    private long cur;
    private long next;

    public TupleLong(){
        // default constructor
    }

    public TupleLong(long p, long c, long n){
        this.prev = p;
        this.cur = c;
        this.next = n;
    }

    public long getPrev() {
        return prev;
    }

    public void setPrev(long prev) {
        this.prev = prev;
    }

    public long getCur() {
        return cur;
    }

    public void setCur(long cur) {
        this.cur = cur;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }
}
