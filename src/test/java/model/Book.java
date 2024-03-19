package model;

import java.time.LocalDate;

public class Book {
    private String uuid;
    private Author author;
    private String title;
    private LocalDate publicationDate;
    private int pages;
    private String[] category;


    public void setUuid(String uuid) { this.uuid = uuid; }
    public void setAuthor(Author author) { this.author = author; }
    public void setTitle(String title) { this.title = title; }
    public void setPages(int pages) { this.pages = pages; }
    public void setPublicationDate(String publicationDate) { this.publicationDate = LocalDate.parse(publicationDate); }
    public void setCategory(String[] category) { this.category = category; }


    public String getTitle() { return title; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public Author getAuthor() { return author; }
    public String[] getCategory() { return category; }

}
