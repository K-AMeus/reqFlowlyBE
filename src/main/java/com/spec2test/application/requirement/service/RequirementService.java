package com.spec2test.application.requirement.service;

import com.spec2test.application.common.exception.ErrorCode;
import com.spec2test.application.common.exception.InvalidStateException;
import com.spec2test.application.requirement.dto.RequirementCreateRequestDto;
import com.spec2test.application.requirement.dto.RequirementCreateResponseDto;
import com.spec2test.application.requirement.dto.RequirementDto;
import com.spec2test.application.requirement.mapper.RequirementMapper;
import com.spec2test.application.requirement.model.Requirement;
import com.spec2test.application.requirement.repository.RequirementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequirementService {
    private final RequirementRepository requirementRepository;
    private final RequirementMapper requirementMapper;

    public RequirementCreateResponseDto createPdfRequirement(MultipartFile file, UUID projectId, RequirementCreateRequestDto req) {

        Requirement newRequirement = requirementMapper.toEntity(req);
        try {
            String text = extractTextFromPDF(file.getInputStream());
            newRequirement.setProjectId(projectId);
            newRequirement.setSourceContent(text);
        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF file", e);
        }

        return requirementMapper.toResponseDto(requirementRepository.save(newRequirement));
    }

    public RequirementCreateResponseDto createTextRequirement(UUID projectId, RequirementCreateRequestDto req) {
        Requirement newRequirement = requirementMapper.toEntity(req);
        newRequirement.setProjectId(projectId);

        return requirementMapper.toResponseDto(requirementRepository.save(newRequirement));
    }



    public RequirementDto getRequirement(UUID projectId, UUID requirementId) {
        return requirementMapper.toDto(requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND)));
    }

    public Page<RequirementDto> getAllRequirements(UUID projectId, Pageable pageable) {
        return requirementRepository.findAllByProjectId(projectId, pageable)
                .map(requirementMapper::toDto);
    }

    @Transactional
    public RequirementCreateResponseDto updateRequirement(UUID projectId, UUID requirementId, RequirementCreateRequestDto req) {
        Requirement requirement = requirementRepository.findByProjectIdAndId(projectId, requirementId)
                .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));


        requirementMapper.updateRequirement(requirement, req);
        requirement = requirementRepository.save(requirement);
        return requirementMapper.toResponseDto(requirement);
    }

    @Transactional
    public void deleteRequirement(UUID projectId, UUID requirementId) {
        requirementRepository.findByProjectIdAndId(projectId, requirementId)
                        .orElseThrow(() -> new InvalidStateException(ErrorCode.REQUIREMENT_NOT_FOUND));
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
