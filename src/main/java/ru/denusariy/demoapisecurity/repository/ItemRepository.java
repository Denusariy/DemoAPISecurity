package ru.denusariy.demoapisecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.denusariy.demoapisecurity.domain.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
}
