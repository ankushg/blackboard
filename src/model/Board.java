package model;

import java.util.List;

import operations.Operation;

public class Board {
    int id;
    List<String> operations;

    
    public boolean addOperation(String op){
        return this.operations.add(op);
    }
}
