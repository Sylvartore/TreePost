package ca.bcit.planters.treepost;

import java.util.ArrayList;

public class Tree {
    public ArrayList<Message> privateMsg;
    public ArrayList<Message> publicMsg;

    public Tree(){}

    public Tree(ArrayList<Message> publicMsg, ArrayList<Message> privateMsg) {
        this.publicMsg = publicMsg;
        this.privateMsg = privateMsg;
    }
}
