package com.example.library_api.book;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookService service;
  public BookController(BookService service) { this.service = service; }

  @PostMapping
  public ResponseEntity<Book> create(@Valid @RequestBody BookRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
  }
  @GetMapping public List<Book> list() { return service.list(); }
  @GetMapping("/{id}") public Book get(@PathVariable Long id) { return service.get(id); }
  @PutMapping("/{id}") public Book update(@PathVariable Long id, @RequestBody BookRequest req) { return service.update(id, req); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
