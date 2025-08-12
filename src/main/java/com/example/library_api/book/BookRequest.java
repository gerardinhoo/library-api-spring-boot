package com.example.library_api.book;

import jakarta.validation.constraints.*;

public class BookRequest {
  @NotBlank @Size(max=200) private String title;
  @NotBlank @Size(max=120) private String author;
  @Pattern(regexp="^[0-9\\-]{10,17}$", message="Invalid ISBN")
  private String isbn;
  @Min(1400) @Max(2100) private Integer yearPublished;

  public String getTitle() { return title; }
  public String getAuthor() { return author; }
  public String getIsbn() { return isbn; }
  public Integer getYearPublished() { return yearPublished; }

  public void setTitle(String v) { this.title = v; }
  public void setAuthor(String v) { this.author = v; }
  public void setIsbn(String v) { this.isbn = v; }
  public void setYearPublished(Integer v) { this.yearPublished = v; }
}
