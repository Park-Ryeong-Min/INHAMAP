package com.example.inhamap.PathFindings;

public class GuideOutput {
    private String text;
    private int edgeNumber;
    private int status;

    public GuideOutput(){
        // default constructor
    }

    public GuideOutput(String text, int num, int status){
        this.text = text;
        this.edgeNumber = num;
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getEdgeNumber() {
        return edgeNumber;
    }

    public void setEdgeNumber(int edgeNumber) {
        this.edgeNumber = edgeNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
