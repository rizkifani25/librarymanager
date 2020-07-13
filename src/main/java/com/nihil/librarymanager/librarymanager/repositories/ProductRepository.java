package com.nihil.librarymanager.librarymanager.repositories;

import java.util.List;

import com.nihil.librarymanager.librarymanager.model.Product;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{id:'?0'}")
    List<Product> findCustom(String id);
}
