package com.linkvault.backend.service;

import com.linkvault.backend.model.Link;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LinkService {

    public List<Link> getAllLinks() {
        return List.of(
                new Link(
                        1L,
                        "Google",
                        "https://google.com"),
                new Link(
                        2L,
                        "OpenAI",
                        "https://openai.com"),

                new Link(
                        3L,
                        "Spring",
                        "https://spring.io"),
                new Link(
                        4L,
                        "YT",
                        "https://youTube.com"),
                new Link(
                        5L,
                        "Snap",
                        "https://SnapChat.io"));
    }

    public Link getDemoLink() {
        return new Link(
                101L,
                "My First Micro SaaS",
                "https://bharat.dev");
    }
}