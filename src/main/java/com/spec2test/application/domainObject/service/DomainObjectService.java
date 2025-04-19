package com.spec2test.application.domainObject.service;

import com.spec2test.application.common.exception.ErrorCode;
import com.spec2test.application.common.exception.InvalidStateException;
import com.spec2test.application.domainObject.dto.DomainObjectCreateRequestDto;
import com.spec2test.application.domainObject.dto.DomainObjectCreateResponseDto;
import com.spec2test.application.domainObject.dto.DomainObjectDto;
import com.spec2test.application.domainObject.mapper.DomainObjectMapper;
import com.spec2test.application.domainObject.model.DomainObject;
import com.spec2test.application.domainObject.repository.DomainObjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainObjectService {

    private final DomainObjectRepository domainObjectRepository;

    private final DomainObjectMapper domainObjectMapper;

    public DomainObjectCreateResponseDto createDomainObject(UUID projectId, DomainObjectCreateRequestDto req) {
        DomainObject newDomainObject = domainObjectMapper.toEntity(req);
        newDomainObject.setProjectId(projectId);
        return domainObjectMapper.toCreateResponse(domainObjectRepository.save(newDomainObject));
    }

    public DomainObjectDto getDomainObject(UUID projectId, UUID domainObjectId) {
        return domainObjectMapper.toDto(domainObjectRepository.findByProjectIdAndId(projectId, domainObjectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.DOMAIN_OBJECT_NOT_FOUND)));
    }

    public Page<DomainObjectDto> getAllDomainObjects(UUID projectId, Pageable pageable) {
        return domainObjectRepository.findAllByProjectId(projectId, pageable)
                .map(domainObjectMapper::toDto);
    }

    @Transactional
    public DomainObjectCreateResponseDto updateDomainObject(UUID projectId, UUID domainObjectId,DomainObjectCreateRequestDto req) {
        DomainObject domainObject = domainObjectRepository.findByProjectIdAndId(projectId, domainObjectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.DOMAIN_OBJECT_NOT_FOUND));

        domainObjectMapper.updateDomainObject(domainObject, req);
        domainObject = domainObjectRepository.save(domainObject);
        return domainObjectMapper.toCreateResponse(domainObject);
    }

    @Transactional
    public void deleteDomainObject(UUID projectId, UUID domainObjectId) {
        domainObjectRepository.findByProjectIdAndId(projectId, domainObjectId)
                        .orElseThrow(() -> new InvalidStateException(ErrorCode.DOMAIN_OBJECT_NOT_FOUND));
        domainObjectRepository.deleteByProjectIdAndId(projectId, domainObjectId);
    }

}
