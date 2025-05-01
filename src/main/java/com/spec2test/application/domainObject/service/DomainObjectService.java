package com.spec2test.application.domainObject.service;

import com.spec2test.application.common.exception.ErrorCode;
import com.spec2test.application.common.exception.InvalidStateException;
import com.spec2test.application.domainObject.dto.*;
import com.spec2test.application.domainObject.mapper.DomainObjectMapper;
import com.spec2test.application.domainObject.model.DomainObject;
import com.spec2test.application.domainObject.model.DomainObjectAttribute;
import com.spec2test.application.domainObject.repository.DomainObjectAttributeRepository;
import com.spec2test.application.domainObject.repository.DomainObjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainObjectService {

    private final DomainObjectRepository domainObjectRepository;
    private final DomainObjectAttributeRepository domainObjectAttributeRepository;
    private final DomainObjectMapper domainObjectMapper;

    @Transactional
    public DomainObjectsCreateResponseDto createDomainObjects(UUID projectId, UUID requirementId, DomainObjectsCreateRequestDto req) {
        // Create domain objects
        List<DomainObject> domainObjects = new ArrayList<>();
        Map<String, DomainObject> nameToEntityDomainObjects = new HashMap<>();

        for (String domainObjectName : req.domainObjectsWithAttributes().keySet()) {
            DomainObject newDomainObject = new DomainObject();
            newDomainObject.setProjectId(projectId);
            newDomainObject.setName(domainObjectName);
            newDomainObject.setRequirementId(requirementId);
            domainObjects.add(newDomainObject);
            nameToEntityDomainObjects.put(domainObjectName, newDomainObject);
        }

        List<DomainObject> savedDomainObjects = domainObjectRepository.saveAll(domainObjects);

        // Create attributes
        List<DomainObjectAttribute> domainObjectAttributes = new ArrayList<>();
        Map<UUID, List<DomainObjectAttribute>> domainObjectIdToAttributes = new HashMap<>();

        for (Map.Entry<String, List<DomainObjectAttributeCreateRequestDto>> entry : req.domainObjectsWithAttributes().entrySet()) {
            DomainObject savedDomainObject = nameToEntityDomainObjects.get(entry.getKey());
            List<DomainObjectAttribute> domainAttributes = new ArrayList<>();

            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (DomainObjectAttributeCreateRequestDto attributeDto : entry.getValue()) {
                    DomainObjectAttribute attribute = new DomainObjectAttribute();
                    attribute.setName(attributeDto.name());
                    attribute.setDataType(attributeDto.dataType());
                    attribute.setDomainObjectId(savedDomainObject.getId());
                    domainObjectAttributes.add(attribute);
                    domainAttributes.add(attribute);
                }
                domainObjectIdToAttributes.put(savedDomainObject.getId(), domainAttributes);
            }
        }

        List<DomainObjectAttribute> savedAttributes = domainObjectAttributeRepository.saveAll(domainObjectAttributes);

        Map<UUID, List<DomainObjectAttribute>> savedDomainIdToAttributes = new HashMap<>();
        for (DomainObjectAttribute attr : savedAttributes) {
            savedDomainIdToAttributes
                    .computeIfAbsent(attr.getDomainObjectId(), k -> new ArrayList<>())
                    .add(attr);
        }

        List<DomainObjectResponseDto> domainObjectResponses = new ArrayList<>();

        for (DomainObject domainObject : savedDomainObjects) {
            List<DomainObjectAttribute> attributes =
                    savedDomainIdToAttributes.getOrDefault(domainObject.getId(), Collections.emptyList());

            List<DomainObjectAttributeCreateResponseDto> attributeResponses = attributes.stream()
                    .map(attr -> new DomainObjectAttributeCreateResponseDto(
                            attr.getId(),
                            attr.getDomainObjectId(),
                            attr.getName(),
                            attr.getDataType(),
                            attr.getCreatedAt(),
                            attr.getUpdatedAt()
                    ))
                    .collect(Collectors.toList());

            domainObjectResponses.add(new DomainObjectResponseDto(
                    domainObject.getId(),
                    domainObject.getName(),
                    domainObject.getCreatedAt(),
                    domainObject.getUpdatedAt(),
                    attributeResponses
            ));
        }

        return new DomainObjectsCreateResponseDto(domainObjectResponses);
    }

    public DomainObjectDto getDomainObject(UUID projectId, UUID domainObjectId) {
        return domainObjectMapper.toDto(domainObjectRepository.findByProjectIdAndId(projectId, domainObjectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.DOMAIN_OBJECT_NOT_FOUND)));
    }

    public Page<DomainObjectDto> getAllDomainObjects(UUID projectId, UUID requirementId, Pageable pageable) {
        return domainObjectRepository.findAllByProjectIdAndRequirementId(projectId, requirementId, pageable)
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


    @Transactional
    public DomainObjectsWithAttributesResponseDto getAllDomainObjectsWithAttributes(UUID projectId) {
        // Fetch all domain objects for the project
        List<DomainObject> domainObjects = domainObjectRepository.findAllByProjectId(projectId);
        
        if (domainObjects.isEmpty()) {
            return new DomainObjectsWithAttributesResponseDto(Collections.emptyMap());
        }
        
        // Collect all domain object IDs
        List<UUID> domainObjectIds = domainObjects.stream()
                .map(DomainObject::getId)
                .collect(Collectors.toList());
        
        // Fetch all attributes for these domain objects
        List<DomainObjectAttribute> allAttributes = domainObjectAttributeRepository.findAllByDomainObjectIdIn(domainObjectIds);
        
        // Group attributes by domain object ID
        Map<UUID, List<DomainObjectAttribute>> attributesByDomainObjectId = allAttributes.stream()
                .collect(Collectors.groupingBy(DomainObjectAttribute::getDomainObjectId));
        
        // Create a map of domain object names to their attributes
        Map<String, List<DomainObjectAttributeDto>> result = new HashMap<>();
        
        for (DomainObject domainObject : domainObjects) {
            List<DomainObjectAttributeDto> attributeDtos = attributesByDomainObjectId
                    .getOrDefault(domainObject.getId(), Collections.emptyList())
                    .stream()
                    .map(attr -> new DomainObjectAttributeDto(
                            attr.getId(),
                            attr.getDomainObjectId(),
                            attr.getName(),
                            attr.getDataType(),
                            attr.getCreatedAt(),
                            attr.getUpdatedAt()
                    ))
                    .collect(Collectors.toList());
            
            result.put(domainObject.getName(), attributeDtos);
        }
        
        return new DomainObjectsWithAttributesResponseDto(result);
    }

}
