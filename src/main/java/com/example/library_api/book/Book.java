package com.example.library_api.book;

import jakarta.persistence.*;

@Entity
public class Book {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false) private String title;
  @Column(nullable=false) private String author;
  @Column(unique=true)    private String isbn;
  private Integer yearPublished;

  public Book() {}
  public Book(String title, String author, String isbn, Integer yearPublished) {
    this.title = title; this.author = author; this.isbn = isbn; this.yearPublished = yearPublished;
  }

  public Long getId() { return id; }
  public String getTitle() { return title; }
  public String getAuthor() { return author; }
  public String getIsbn() { return isbn; }
  public Integer getYearPublished() { return yearPublished; }

  public void setTitle(String v) { this.title = v; }
  public void setAuthor(String v) { this.author = v; }
  public void setIsbn(String v) { this.isbn = v; }
  public void setYearPublished(Integer v) { this.yearPublished = v; }
}
