package com.reqflowly.application.domainObject.service;

import com.reqflowly.application.common.exception.ErrorCode;
import com.reqflowly.application.common.exception.InvalidStateException;
import com.reqflowly.application.domainObject.repository.DomainObjectAttributeRepository;
import com.reqflowly.application.domainObject.dto.DomainObjectAttributeCreateRequestDto;
import com.reqflowly.application.domainObject.dto.DomainObjectAttributeCreateResponseDto;
import com.reqflowly.application.domainObject.dto.DomainObjectAttributeDto;
import com.reqflowly.application.domainObject.mapper.DomainObjectAttributeMapper;
import com.reqflowly.application.domainObject.model.DomainObjectAttribute;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainObjectAttributeService {

    private final DomainObjectAttributeRepository domainObjectAttributeRepository;
    private final DomainObjectAttributeMapper domainObjectAttributeMapper;

    public DomainObjectAttributeCreateResponseDto createAttribute(UUID projectId, UUID domainObjectId, DomainObjectAttributeCreateRequestDto req) {
        DomainObjectAttribute newAttribute = domainObjectAttributeMapper.toEntity(req);
        newAttribute.setDomainObjectId(domainObjectId);
        return domainObjectAttributeMapper.toCreateResponse(domainObjectAttributeRepository.save(newAttribute));
    }

    public DomainObjectAttributeDto getAttribute(UUID projectId, UUID domainObjectId, UUID attributeId) {
        return domainObjectAttributeMapper.toDto(domainObjectAttributeRepository.findByDomainObjectIdAndId(domainObjectId, attributeId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.ATTRIBUTE_NOT_FOUND)));
    }

    public Page<DomainObjectAttributeDto> getAllAttributes(UUID projectId, UUID domainObjectId, Pageable pageable) {
        return domainObjectAttributeRepository.findAllByDomainObjectId(domainObjectId, pageable)
                .map(domainObjectAttributeMapper::toDto);
    }

    @Transactional
    public DomainObjectAttributeCreateResponseDto updateAttribute(UUID projectId, UUID domainObjectId, UUID attributeId, DomainObjectAttributeCreateRequestDto req) {
        DomainObjectAttribute attribute = domainObjectAttributeRepository.findByDomainObjectIdAndId(domainObjectId, attributeId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.ATTRIBUTE_NOT_FOUND));

        domainObjectAttributeMapper.updateDomainObjectAttribute(attribute, req);
        attribute = domainObjectAttributeRepository.save(attribute);
        return domainObjectAttributeMapper.toCreateResponse(attribute);
    }

    @Transactional
    public void deleteAttribute(UUID projectId, UUID domainObjectId, UUID attributeId) {
        domainObjectAttributeRepository.findByDomainObjectIdAndId(domainObjectId, attributeId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.ATTRIBUTE_NOT_FOUND));
        domainObjectAttributeRepository.deleteByDomainObjectIdAndId(domainObjectId, attributeId);
    }
}
