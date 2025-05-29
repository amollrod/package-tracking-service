package com.tfg.packagetracking.infrastructure.adapters;

import com.tfg.packagetracking.domain.ports.PackageRepositoryPort;
import com.tfg.packagetracking.domain.models.Package;
import com.tfg.packagetracking.domain.models.PackageStatus;
import com.tfg.packagetracking.infrastructure.documents.PackageDocument;
import com.tfg.packagetracking.infrastructure.mappers.PackageDocumentMapper;
import com.tfg.packagetracking.infrastructure.repositories.MongoPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PackageRepositoryAdapter implements PackageRepositoryPort {
    private final MongoPackageRepository repository;
    private final MongoTemplate mongoTemplate;

    public PackageRepositoryAdapter(MongoPackageRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Package> findById(String id) {
        return repository.findById(id).map(PackageDocumentMapper::toDomain);
    }

    @Override
    public Page<Package> findByFilters(PackageStatus status, String origin, String destination, String location, Instant fromDate, Instant toDate, Pageable pageable) {
        Query query = new Query();

        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (origin != null) {
            query.addCriteria(Criteria.where("origin").regex(origin, "i"));
        }
        if (destination != null) {
            query.addCriteria(Criteria.where("destination").regex(destination, "i"));
        }
        if (location != null) {
            query.addCriteria(Criteria.where("history.location").regex(location, "i"));
        }

        if (fromDate != null || toDate != null) {
            Criteria timeCriteria = Criteria.where("history.timestamp");
            if (fromDate != null) {
                timeCriteria = timeCriteria.gte(fromDate);
            }
            if (toDate != null) {
                timeCriteria = timeCriteria.lte(toDate);
            }
            query.addCriteria(timeCriteria);
        }

        query.with(pageable);
        long count = mongoTemplate.count(query, PackageDocument.class);
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, PackageDocument.class)
                        .stream()
                        .map(PackageDocumentMapper::toDomain)
                        .toList(),
                pageable,
                () -> count
        );
    }

    @Override
    public Package save(Package pkg) {
        return PackageDocumentMapper.toDomain(repository.save(PackageDocumentMapper.toDocument(pkg)));
    }
}
