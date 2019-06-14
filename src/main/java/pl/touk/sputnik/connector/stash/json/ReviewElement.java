package pl.touk.sputnik.connector.stash.json;

public class ReviewElement {
    //{"extension":"scala","name":"RecoBuild.scala","parent":"project","toString":"project\/RecoBuild.scala","components":["project","RecoBuild.scala"]}

    public String extension;
    public String name;
    public String parent;
    public String[] components;
    public String toString;
}