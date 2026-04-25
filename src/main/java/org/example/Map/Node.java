package org.example.Map;

import lombok.AllArgsConstructor;


public class Node implements Comparable<Node>{
    int row,col,g,h,f ;
    Node parent;

    public Node(int row, int col, int g , int h, Node parent){
        this.row = row;
        this.col = col;
        this.g= g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
    }

    @Override
    public int compareTo(Node other){
        return Integer.compare(this.f, other.f);
    }

}
