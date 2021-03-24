package model.database;

import java.util.Random;

public class Page {
    private Integer id;
    private Integer idBook;
    private Integer pageNumber;
    private String content;

    public Page(Integer idBook, Integer pageNumber, String content) {
        Random rand = new Random(0);
        this.id = rand.nextInt();
        this.idBook = idBook;
        this.pageNumber = pageNumber;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdBook() {
        return idBook;
    }

    public void setIdBook(Integer idBook) {
        this.idBook = idBook;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
