package com.reqflowly.application.domainObject.repository;

import com.reqflowly.application.domainObject.model.DomainObjectAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DomainObjectAttributeRepository extends JpaRepository<DomainObjectAttribute, UUID> {

    Optional<DomainObjectAttribute> findByDomainObjectIdAndId(UUID domainObjectId, UUID id);

    Page<DomainObjectAttribute> findAllByDomainObjectId(UUID domainObjectId, Pageable pageable);

    List<DomainObjectAttribute> findAllByDomainObjectIdIn(List<UUID> domainObjectIds);

    void deleteByDomainObjectIdAndId(UUID domainObjectId, UUID id);

}
