package com.example.library_api;
import org.springframework.web.bind.annotation.*;
@RestController
public class RootController {
  @GetMapping("/") public String ok() { return "Library API is running"; }
}
