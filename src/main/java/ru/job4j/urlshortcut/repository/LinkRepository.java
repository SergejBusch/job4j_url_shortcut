package ru.job4j.urlshortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.urlshortcut.model.Link;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Integer> {
    Optional<Link> findByUrl(String url);

    Optional<Link> findByCode(String code);
}
