package com.tfg.packagetracking.infrastructure.repositories;

import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPackageRepository extends MongoRepository<PackageDocument, String> { }
