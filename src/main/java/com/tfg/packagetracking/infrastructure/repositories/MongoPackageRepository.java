package com.tfg.packagetracking.infrastructure.repositories;

import com.tfg.packagetracking.domain.models.Package;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoPackageRepository extends MongoRepository<Package, Long> { }
