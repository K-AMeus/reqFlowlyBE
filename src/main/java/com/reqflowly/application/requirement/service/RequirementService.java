package com.reqflowly.application.requirement.service;

import com.reqflowly.application.common.exception.ErrorCode;
import com.reqflowly.application.common.exception.InvalidStateException;
import com.reqflowly.application.project.model.Project;
import com.reqflowly.application.project.repository.ProjectRepository;
import com.reqflowly.application.requirement.dto.RequirementCreateRequestDto;
import com.reqflowly.application.requirement.dto.RequirementCreateResponseDto;
import com.reqflowly.application.requirement.dto.RequirementDto;
import com.reqflowly.application.requirement.model.Requirement;
import com.reqflowly.application.requirement.repository.RequirementRepository;
import com.reqflowly.application.requirement.mapper.RequirementMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final RequirementMapper requirementMapper;
    private final ProjectRepository projectRepository;

    @Transactional
    public RequirementCreateResponseDto createPdfRequirement(MultipartFile file, UUID projectId, RequirementCreateRequestDto req) {

        Requirement newRequirement = requirementMapper.toEntity(req);
        try {
            String text = extractTextFromPDF(file.getInputStream());
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));
            newRequirement.setProjectId(projectId);
            project.setUpdatedAt(Instant.now());
            newRequirement.setSourceContent(text);
        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF file", e);
        }

        return requirementMapper.toResponseDto(requirementRepository.save(newRequirement));
    }

    public RequirementCreateResponseDto createTextRequirement(UUID projectId, RequirementCreateRequestDto req) {
        Requirement newRequirement = requirementMapper.toEntity(req);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));

        newRequirement.setProjectId(projectId);
        project.setUpdatedAt(Instant.now());
        projectRepository.save(project);

        return requirementMapper.toResponseDto(requirementRepository.save(newRequirement));
    }

    public RequirementDto getRequirement(UUID projectId, UUID requirementId) {
        return requirementMapper.toDto(requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND)));
    }

    public Page<RequirementDto> getAllRequirements(UUID projectId, Pageable pageable) {
        return requirementRepository.findAllByProjectIdOrderByUpdatedAtDesc(projectId, pageable)
                .map(requirementMapper::toDto);
    }
    

    public Page<RequirementDto> findRequirementsByIdsAndProject(UUID projectId, List<UUID> requirementIds, Pageable pageable) {
        if (CollectionUtils.isEmpty(requirementIds)) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        return requirementRepository.findAllByProjectIdAndIdInOrderByUpdatedAtDesc(projectId, requirementIds, pageable)
                .map(requirementMapper::toDto);
    }

    @Transactional
    public RequirementCreateResponseDto updateRequirement(UUID projectId, UUID requirementId, RequirementCreateRequestDto req) {
        Requirement requirement = requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));

        project.setUpdatedAt(Instant.now());
        projectRepository.save(project);

        requirementMapper.updateRequirement(requirement, req);
        requirement = requirementRepository.save(requirement);
        return requirementMapper.toResponseDto(requirement);
    }

    @Transactional
    public void deleteRequirement(UUID projectId, UUID requirementId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                        .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.PROJECT_NOT_FOUND));

        project.setUpdatedAt(Instant.now());
        projectRepository.save(project);

        requirementRepository.deleteByProjectIdAndId(projectId, requirementId);
    }

    private String extractTextFromPDF(InputStream inputStream) throws Exception {
        byte[] pdfBytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
