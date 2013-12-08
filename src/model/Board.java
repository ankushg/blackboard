package model;

import java.util.List;

import operations.Operation;

public class Board {
    int id;
    List<Operation> operations;

    
    public String addOperation(Operation op){
        this.operations.add(op);
        return op.toString();
    }
}
