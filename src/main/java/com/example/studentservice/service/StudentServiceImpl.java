package com.example.studentservice.service;


import com.example.studentservice.dto.StudentDTO;
import com.example.studentservice.entity.Student;
import com.example.studentservice.repository.StudentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    @Autowired
    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (repository.existsByEmail(studentDTO.getEmail())) {
            throw new IllegalArgumentException("A student with email " + studentDTO.getEmail() + " already exists.");
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        Student saved = repository.save(student);
        StudentDTO response = new StudentDTO();
        BeanUtils.copyProperties(saved, response);
        return response;
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
        StudentDTO dto = new StudentDTO();
        BeanUtils.copyProperties(student, dto);
        return dto;
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return repository.findAll().stream().map(student -> {
            StudentDTO dto = new StudentDTO();
            BeanUtils.copyProperties(student, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        existing.setFirstName(studentDTO.getFirstName());
        existing.setLastName(studentDTO.getLastName());
        existing.setDateOfBirth(studentDTO.getDateOfBirth());
        existing.setGrade(studentDTO.getGrade());
        // Do not update email to avoid duplicates.
        Student updated = repository.save(existing);
        StudentDTO dto = new StudentDTO();
        BeanUtils.copyProperties(updated, dto);
        return dto;
    }

    @Override
    public void deleteStudent(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
