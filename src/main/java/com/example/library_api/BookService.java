package com.example.library_api.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @Transactional
public class BookService {
  private final BookRepository repo;
  public BookService(BookRepository repo) { this.repo = repo; }

  public Book create(BookRequest r) {
    if (r.getIsbn() != null)
      repo.findByIsbn(r.getIsbn()).ifPresent(b -> { throw new IllegalArgumentException("ISBN already exists"); });
    return repo.save(new Book(r.getTitle(), r.getAuthor(), r.getIsbn(), r.getYearPublished()));
  }

  public List<Book> list() { return repo.findAll(); }

  public Book get(Long id) {
    return repo.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
  }

  public Book update(Long id, BookRequest r) {
    Book b = get(id);
    if (r.getIsbn() != null && !r.getIsbn().equals(b.getIsbn()))
      repo.findByIsbn(r.getIsbn()).ifPresent(x -> { throw new IllegalArgumentException("ISBN already exists"); });
    if (r.getTitle() != null) b.setTitle(r.getTitle());
    if (r.getAuthor() != null) b.setAuthor(r.getAuthor());
    if (r.getIsbn() != null) b.setIsbn(r.getIsbn());
    if (r.getYearPublished() != null) b.setYearPublished(r.getYearPublished());
    return repo.save(b);
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new NotFoundException("Book not found");
    repo.deleteById(id);
  }
}
